package com.ormee.server.service;

import com.ormee.server.dto.quiz.*;
import com.ormee.server.exception.CustomException;
import com.ormee.server.exception.ExceptionType;
import com.ormee.server.model.*;
import com.ormee.server.repository.LectureRepository;
import com.ormee.server.repository.ProblemRepository;
import com.ormee.server.repository.QuizRepository;
import com.ormee.server.repository.ProblemSubmitRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class QuizService {

    private final QuizRepository quizRepository;
    private final LectureRepository lectureRepository;
    private final ProblemRepository problemRepository;
    private final ProblemSubmitRepository problemSubmitRepository;

    public QuizService(QuizRepository quizRepository, LectureRepository lectureRepository, ProblemRepository problemRepository, ProblemSubmitRepository problemSubmitRepository) {
        this.quizRepository = quizRepository;
        this.lectureRepository = lectureRepository;
        this.problemRepository = problemRepository;
        this.problemSubmitRepository = problemSubmitRepository;
    }

    public void saveQuiz(UUID lectureId, QuizSaveDto quizSaveDto) {
        Lecture lecture = lectureRepository.findById(lectureId).orElseThrow(() -> new CustomException(ExceptionType.LECTURE_NOT_FOUND_EXCEPTION));

        Quiz quiz = Quiz.builder()
                .lecture(lecture)
                .title(quizSaveDto.getTitle())
                .description(quizSaveDto.getDescription())
                .isDraft(quizSaveDto.getIsDraft())
                .isOpened(false)
                .openTime(quizSaveDto.getOpenTime())
                .dueTime(quizSaveDto.getDueTime())
                .timeLimit(quizSaveDto.getTimeLimit())
                // 학생수 추가
                .build();
        quiz = quizRepository.save(quiz);

        for(ProblemDto problemDto : quizSaveDto.getProblems()) {
            Problem problem = Problem.builder()
                    .quiz(quiz)
                    .content(problemDto.getContent())
                    .type(ProblemType.valueOf(problemDto.getType()))
                    .answer(problemDto.getAnswer())
                    .items(problemDto.getItems())
                    .build();
            problemRepository.save(problem);
        }
    }

    public void modifyQuiz(UUID quizId, QuizSaveDto quizSaveDto) {
        Quiz quiz = quizRepository.findById(quizId).orElseThrow(() -> new CustomException(ExceptionType.QUIZ_NOT_FOUND_EXCEPTION));
        saveQuiz(quiz.getLecture().getId(), quizSaveDto);
        deleteQuiz(quizId);
    }

    public List<QuizListDto> findAllByLecture(UUID lectureId, Boolean isDraft) {
        Lecture lecture = lectureRepository.findById(lectureId).orElseThrow(() -> new CustomException(ExceptionType.LECTURE_NOT_FOUND_EXCEPTION));
        List<Quiz> quizList = quizRepository.findAllByLectureAndIsDraftOrderByCreatedAtDesc(lecture, isDraft);

        return quizListToDtoList(quizList);
    }

    public TeacherQuizListDto teacherQuizList(UUID lectureId, Boolean isDraft) {
        Lecture lecture = lectureRepository.findById(lectureId).orElseThrow(() -> new CustomException(ExceptionType.LECTURE_NOT_FOUND_EXCEPTION));
        List<Quiz> quizList = quizRepository.findAllByLectureAndIsDraftOrderByCreatedAtDesc(lecture, isDraft);
        List<Quiz> openQuizzes = new ArrayList<>();
        List<Quiz> closedQuizzes = new ArrayList<>();

        LocalDateTime now = LocalDateTime.now();

        for(Quiz quiz : quizList) {
            if(quiz.getDueTime().isBefore(now)) {
                closedQuizzes.add(quiz);
            } else {
                openQuizzes.add(quiz);
            }
        }

        return TeacherQuizListDto.builder()
                .openQuizzes(quizListToDtoList(openQuizzes))
                .closedQuizzes(quizListToDtoList(closedQuizzes))
                .build();
    }

    public List<QuizListDto> findOpenQuizList(UUID lectureId) {
        Lecture lecture = lectureRepository.findById(lectureId).orElseThrow(() -> new CustomException(ExceptionType.LECTURE_NOT_FOUND_EXCEPTION));
        List<Quiz> quizList = quizRepository.findAllByLectureAndIsDraftAndIsOpenedOrderByDueTimeDesc(lecture, false, true);

        return quizListToDtoList(quizList);
    }

    private List<QuizListDto> quizListToDtoList(List<Quiz> quizList) {
        List<QuizListDto> quizListDtos = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        for(Quiz quiz : quizList) {
            QuizListDto quizListDto = QuizListDto.builder()
                    .id(quiz.getId().toString())
                    .quizName(quiz.getTitle())
                    .timeLimit(quiz.getTimeLimit())
                    .quizDate(quiz.getDueTime().format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm")))
                    .quizAvailable(quiz.getIsOpened() && quiz.getOpenTime().isBefore(now) && quiz.getDueTime().isAfter(now))
                    .submitCount(problemSubmitRepository.countAllByProblem(problemRepository.findFirstByQuiz(quiz)))

                    .build();
            quizListDtos.add(quizListDto);
        }
        return quizListDtos;
    }

    public void deleteQuiz(UUID quizId) {
        Quiz quiz = quizRepository.findById(quizId).orElseThrow(() -> new CustomException(ExceptionType.QUIZ_NOT_FOUND_EXCEPTION));

        List<Problem> problems = problemRepository.findAllByQuiz(quiz);

        for(Problem problem : problems) {
            problemRepository.delete(problem);
        }

        quizRepository.delete(quiz);
    }

    public QuizDetailDto findQuiz(UUID quizId) {
        Quiz quiz = quizRepository.findById(quizId).orElseThrow(() -> new CustomException(ExceptionType.QUIZ_NOT_FOUND_EXCEPTION));

        List<Problem> problems = problemRepository.findAllByQuiz(quiz);
        List<ProblemDto> problemDtos = new ArrayList<>();

        for(Problem problem : problems) {
            ProblemDto problemDto = ProblemDto.builder()
                    .id(problem.getId())
                    .type(problem.getType().toString())
                    .content(problem.getContent())
                    .answer(problem.getAnswer())
                    .items(problem.getItems())
                    .build();
            problemDtos.add(problemDto);
        }

        return QuizDetailDto.builder()
                .title(quiz.getTitle())
                .description(quiz.getDescription())
                .isOpened(quiz.getIsOpened())
                .dueTime(quiz.getDueTime())
                .timeLimit(quiz.getTimeLimit())
                .problems(problemDtos)
                .build();
    }

    public void submit(SubmitRequestDto submitRequestDto) {
        String author = submitRequestDto.getAuthor();
        String password = submitRequestDto.getPassword();
        List<SubmitDto> submitDtos = submitRequestDto.getSubmissions();

        for(SubmitDto submitDto : submitDtos) {
            Problem problem = problemRepository.findById(submitDto.getProblemId()).orElseThrow(() -> new CustomException(ExceptionType.PROBLEM_NOT_FOUND_EXCEPTION));
            ProblemSubmit problemSubmit = ProblemSubmit.builder()
                    .problem(problem)
                    .author(author)
                    .password(password)
                    .content(submitDto.getContent())
                    .build();
            problemSubmitRepository.save(problemSubmit);
        }
    }

    public StudentQuizResultDto getStudentResult(UUID quizId, String author, String password) {
        Quiz quiz = quizRepository.findById(quizId).orElseThrow(() -> new CustomException(ExceptionType.QUIZ_NOT_FOUND_EXCEPTION));
        List<Problem> problems = problemRepository.findAllByQuiz(quiz);
        List<ProblemDto> problemDtos = new ArrayList<>();
        Integer correct = 0;
        for(Problem problem : problems) {
            ProblemSubmit problemSubmit = problemSubmitRepository.findByProblemAndAuthorAndPassword(problem, author, password).orElseThrow(() -> new CustomException(ExceptionType.SUBMIT_NOT_FOUND_EXCEPTION));;
            ProblemDto problemDto = ProblemDto.builder()
                    .id(problem.getId())
                    .content(problem.getContent())
                    .type(problem.getType().toString())
                    .items(problem.getItems())
                    .answer(problem.getAnswer())
                    .submission(problemSubmit.getContent())
                    .isCorrect(problem.getAnswer().equals(problemSubmit.getContent()))
                    .build();
            if(problemDto.getIsCorrect()) {
                correct++;
            }
            problemDtos.add(problemDto);
        }
        return StudentQuizResultDto.builder()
                .correct(correct)
                .problemDtos(problemDtos)
                .build();
    }

    public void openQuiz(UUID quizId) {
        Quiz quiz = quizRepository.findById(quizId).orElseThrow(() -> new CustomException(ExceptionType.QUIZ_NOT_FOUND_EXCEPTION));
        LocalDateTime now = LocalDateTime.now();
        quiz.setIsOpened(true);
        quiz.setOpenTime(now);
        quizRepository.save(quiz);
    }

    public void closeQuiz(UUID quizId) {
        Quiz quiz = quizRepository.findById(quizId).orElseThrow(() -> new CustomException(ExceptionType.QUIZ_NOT_FOUND_EXCEPTION));
        LocalDateTime now = LocalDateTime.now();
        quiz.setDueTime(now);
        quizRepository.save(quiz);
    }

    public List<QuizStatsDto> getStatistics(UUID quizId) {
        Quiz quiz = quizRepository.findById(quizId).orElseThrow(() -> new CustomException(ExceptionType.QUIZ_NOT_FOUND_EXCEPTION));
        List<Problem> problems = problemRepository.findAllByQuiz(quiz);
        long firstNum = problems.get(0).getId() - 1;

        List<QuizStatsDto> quizStatsDtos = problems.stream()
                .map(problem -> {
                    List<ProblemSubmit> problemSubmits = problemSubmitRepository.findAllByProblem(problem);

                    if(problemSubmits.isEmpty()) {
                        return QuizStatsDto.builder()
                                .problemId(problem.getId())
                                .problemNum(problem.getId() - firstNum)
                                .incorrectRate(0)
                                .incorrectCount(0)
                                .build();
                    }

                    long incorrectCount = problemSubmits.stream()
                            .filter(problemSubmit -> !problemSubmit.getContent().equals(problem.getAnswer()))
                            .count();

                    long incorrectRate = (incorrectCount * 100) / problemSubmits.size();

                    return QuizStatsDto.builder()
                            .problemId(problem.getId())
                            .problemNum(problem.getId() - firstNum)
                            .incorrectRate(incorrectRate)
                            .incorrectCount(incorrectCount)
                            .build();
                })
                .sorted(Comparator
                .comparingLong(QuizStatsDto::getIncorrectRate).reversed()
                .thenComparingLong(QuizStatsDto::getIncorrectCount))
                .collect(Collectors.toList());

        for (int i = 0; i < quizStatsDtos.size(); i++) {
            if (i == 0) {
                quizStatsDtos.get(i).setRank(1);
            } else {
                QuizStatsDto current = quizStatsDtos.get(i);
                QuizStatsDto previous = quizStatsDtos.get(i - 1);

                if (current.getIncorrectRate() == previous.getIncorrectRate()) {
                    current.setRank(previous.getRank());
                } else {
                    current.setRank(i + 1);
                }
            }
        }

        return quizStatsDtos;
    }

    public boolean checkStudent(UUID quizId, String author, String password) {
        Quiz quiz = quizRepository.findById(quizId).orElseThrow(() -> new CustomException(ExceptionType.QUIZ_NOT_FOUND_EXCEPTION));
        List<Problem> problems = problemRepository.findAllByQuiz(quiz);

        for(Problem problem : problems) {
            if(!problemSubmitRepository.existsByProblemAndAuthorAndPassword(problem, author, password)) {
                return false;
            }
        }

        return true;
    }

    public ProblemStatsDto getProblemstats(Long problemId) {
        Problem problem = problemRepository.findById(problemId).orElseThrow(() -> new CustomException(ExceptionType.PROBLEM_NOT_FOUND_EXCEPTION));
        List<Map<String, Object>> results = new ArrayList<>();

        if (problem.getType().equals(ProblemType.CHOICE)) {
            long noResponseCount = problemSubmitRepository.findAllByProblem(problem).stream()
                    .map(ProblemSubmit::getContent)
                    .filter(content -> content == null || content.isEmpty())
                    .count();

            if (noResponseCount > 0) {
                Map<String, Object> noResponseResult = new HashMap<>();
                noResponseResult.put("option", "무응답");
                noResponseResult.put("count", noResponseCount);
                results.add(0, noResponseResult);
            }

            for (String option : problem.getItems()) {
                long count = problemSubmitRepository.countAllByProblemAndContentLike(problem, option);
                Map<String, Object> result = new HashMap<>();
                result.put("option", option);
                result.put("count", count);
                results.add(result);
            }
        } else {
            List<String> submissions = problemSubmitRepository.findAllByProblem(problem)
                    .stream()
                    .map(ProblemSubmit::getContent)
                    .collect(Collectors.toList());

            long noResponseCount = submissions.stream()
                    .filter(content -> content == null || content.isEmpty())
                    .count();

            submissions = submissions.stream()
                    .filter(content -> content != null && !content.isEmpty() && !content.equals(problem.getAnswer()))
                    .collect(Collectors.toList());

            Map<String, Long> contentCounts = submissions.stream()
                    .collect(Collectors.groupingBy(content -> content, Collectors.counting()));

            List<Map.Entry<String, Long>> sortedEntries = contentCounts.entrySet().stream()
                    .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                    .collect(Collectors.toList());

            int rank = 1;
            int previousRank = 1;
            long previousCount = -1;

            for (int i = 0; i < sortedEntries.size(); i++) {
                Map.Entry<String, Long> entry = sortedEntries.get(i);

                if (previousCount != -1 && !entry.getValue().equals(previousCount)) {
                    rank = previousRank + 1;
                }
                previousRank = rank;
                previousCount = entry.getValue();

                Map<String, Object> result = new HashMap<>();
                result.put("rank", rank);
                result.put("content", entry.getKey());
                result.put("count", entry.getValue());
                results.add(result);
            }

            if (noResponseCount > 0) {
                Map<String, Object> noResponseResult = new HashMap<>();
                noResponseResult.put("rank", -1);
                noResponseResult.put("content", "무응답");
                noResponseResult.put("count", noResponseCount);
                results.add(0, noResponseResult);
            }
        }

        return ProblemStatsDto.builder()
                .content(problem.getContent())
                .type(problem.getType().toString())
                .answer(problem.getAnswer())
                .results(results)
                .build();
    }
}
