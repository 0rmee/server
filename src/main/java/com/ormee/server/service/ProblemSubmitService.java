package com.ormee.server.service;

import com.ormee.server.dto.quiz.*;
import com.ormee.server.exception.CustomException;
import com.ormee.server.exception.ExceptionType;
import com.ormee.server.model.*;
import com.ormee.server.repository.ProblemRepository;
import com.ormee.server.repository.ProblemSubmitRepository;
import com.ormee.server.repository.QuizRepository;
import com.ormee.server.repository.StudentRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProblemSubmitService {
    private final ProblemSubmitRepository problemSubmitRepository;
    private final ProblemRepository problemRepository;
    private final QuizRepository quizRepository;
    private final StudentRepository studentRepository;

    public ProblemSubmitService(ProblemSubmitRepository problemSubmitRepository, ProblemRepository problemRepository, QuizRepository quizRepository, StudentRepository studentRepository) {
        this.problemSubmitRepository = problemSubmitRepository;
        this.problemRepository = problemRepository;
        this.quizRepository = quizRepository;
        this.studentRepository = studentRepository;
    }


    public void submit(List<SubmitDto> submissions, Authentication authentication) {
        Student student = studentRepository.findByEmail(authentication.getName()).orElseThrow(() -> new CustomException(ExceptionType.STUDENT_NOT_FOUND_EXCEPTION));
        for(SubmitDto submitDto : submissions) {
            Problem problem = problemRepository.findById(submitDto.getProblemId()).orElseThrow(() -> new CustomException(ExceptionType.PROBLEM_NOT_FOUND_EXCEPTION));
            ProblemSubmit problemSubmit = ProblemSubmit.builder()
                    .problem(problem)
                    .student(student)
                    .content(submitDto.getContent())
                    .build();
            problemSubmitRepository.save(problemSubmit);
        }
    }

    public StudentQuizResultDto getStudentResult(UUID quizId, Authentication authentication) {
        Quiz quiz = quizRepository.findById(quizId).orElseThrow(() -> new CustomException(ExceptionType.QUIZ_NOT_FOUND_EXCEPTION));
        Student student = studentRepository.findByEmail(authentication.getName()).orElseThrow(() -> new CustomException(ExceptionType.STUDENT_NOT_FOUND_EXCEPTION));

        List<Problem> problems = problemRepository.findAllByQuiz(quiz);
        List<ProblemDto> problemDtos = new ArrayList<>();
        Integer correct = 0;
        for(Problem problem : problems) {
            ProblemSubmit problemSubmit = problemSubmitRepository.findByProblemAndStudent(problem, student).orElseThrow(() -> new CustomException(ExceptionType.SUBMIT_NOT_FOUND_EXCEPTION));
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
