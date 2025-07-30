package com.ormee.server.quiz.service;

import com.ormee.server.attachment.domain.Attachment;
import com.ormee.server.attachment.repository.AttachmentRepository;
import com.ormee.server.attachment.service.AttachmentService;
import com.ormee.server.global.exception.CustomException;
import com.ormee.server.global.exception.ExceptionType;
import com.ormee.server.lecture.domain.Lecture;
import com.ormee.server.lecture.repository.LectureRepository;
import com.ormee.server.member.domain.Member;
import com.ormee.server.member.domain.Role;
import com.ormee.server.member.dto.AuthorDto;
import com.ormee.server.member.repository.MemberRepository;
import com.ormee.server.notification.domain.NotificationDetailType;
import com.ormee.server.notification.domain.NotificationType;
import com.ormee.server.notification.dto.StudentNotificationRequestDto;
import com.ormee.server.notification.service.StudentNotificationService;
import com.ormee.server.quiz.domain.Problem;
import com.ormee.server.quiz.domain.ProblemSubmit;
import com.ormee.server.quiz.domain.ProblemType;
import com.ormee.server.quiz.domain.Quiz;
import com.ormee.server.quiz.dto.*;
import com.ormee.server.quiz.repository.ProblemRepository;
import com.ormee.server.quiz.repository.ProblemSubmitRepository;
import com.ormee.server.quiz.repository.QuizRepository;
import jakarta.transaction.Transactional;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PutMapping;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class QuizService {

    private final QuizRepository quizRepository;
    private final LectureRepository lectureRepository;
    private final MemberRepository memberRepository;
    private final ProblemRepository problemRepository;
    private final ProblemSubmitRepository problemSubmitRepository;
    private final AttachmentRepository attachmentRepository;
    private final AttachmentService attachmentService;
    private final StudentNotificationService studentNotificationService;

    public QuizService(QuizRepository quizRepository, LectureRepository lectureRepository, MemberRepository memberRepository, ProblemRepository problemRepository, ProblemSubmitRepository problemSubmitRepository, AttachmentRepository attachmentRepository, AttachmentService attachmentService, StudentNotificationService studentNotificationService) {
        this.quizRepository = quizRepository;
        this.lectureRepository = lectureRepository;
        this.memberRepository = memberRepository;
        this.problemRepository = problemRepository;
        this.problemSubmitRepository = problemSubmitRepository;
        this.attachmentRepository = attachmentRepository;
        this.attachmentService = attachmentService;
        this.studentNotificationService = studentNotificationService;
    }

    public void saveQuiz(Long lectureId, QuizSaveDto quizSaveDto, String username) {
        Lecture lecture = lectureRepository.findById(lectureId).orElseThrow(() -> new CustomException(ExceptionType.LECTURE_NOT_FOUND_EXCEPTION));
        Member author = memberRepository.findByUsername(username).orElseThrow(() -> new CustomException(ExceptionType.MEMBER_NOT_FOUND_EXCEPTION));

        validateQuizFields(quizSaveDto);

        Quiz quiz = Quiz.builder()
                .lecture(lecture)
                .author(author)
                .title(quizSaveDto.getTitle())
                .description(quizSaveDto.getDescription())
                .isDraft(quizSaveDto.getIsDraft())
                .isOpened(false)
                .openTime(quizSaveDto.getOpenTime())
                .dueTime(quizSaveDto.getDueTime())
                .timeLimit(quizSaveDto.getTimeLimit())
                .notified(false)
                .build();
        quiz = quizRepository.save(quiz);

        for(ProblemDto problemDto : quizSaveDto.getProblems()) {
            Problem problem = Problem.builder()
                    .quiz(quiz)
                    .content(problemDto.getContent())
                    .type(ProblemType.valueOf(problemDto.getType()))
                    .answer(problemDto.getAnswer())
                    .items(problemDto.getItems() == null ? new ArrayList<>() : problemDto.getItems())
                    .build();

            List<Attachment> attachments = problemDto.getFileIds().stream()
                    .map(id -> attachmentRepository.findById(id)
                            .orElseThrow(() -> new CustomException(ExceptionType.ATTACHMENT_NOT_FOUND_EXCEPTION)))
                    .toList();

            problem.setAttachments(attachments);
            problem = problemRepository.save(problem);

            for (Attachment attachment : attachments) {
                attachment.setParentId(problem.getId().toString());
                attachmentRepository.save(attachment);
            }
        }
    }

    @Transactional
    public void modifyQuiz(Long quizId, QuizSaveDto quizSaveDto) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new CustomException(ExceptionType.QUIZ_NOT_FOUND_EXCEPTION));

        if(!quiz.getIsDraft() && quiz.getOpenTime().isBefore(LocalDateTime.now()))
            throw new CustomException(ExceptionType.QUIZ_MODIFY_FORBIDDEN_EXCEPTION);

        validateQuizFields(quizSaveDto);

        List<Problem> existingProblems = problemRepository.findAllByQuiz(quiz);
        for (Problem problem : existingProblems) {
            for (Attachment attachment : problem.getAttachments()) {
                attachment.setParentId(null);
            }
            problemRepository.delete(problem);
        }

        for (ProblemDto problemDto : quizSaveDto.getProblems()) {
            Problem problem = Problem.builder()
                    .quiz(quiz)
                    .content(problemDto.getContent())
                    .type(ProblemType.valueOf(problemDto.getType()))
                    .answer(problemDto.getAnswer())
                    .items(problemDto.getItems() == null ? new ArrayList<>() : problemDto.getItems())
                    .build();

            List<Attachment> attachments = problemDto.getFileIds().stream()
                    .map(id -> attachmentRepository.findById(id)
                            .orElseThrow(() -> new CustomException(ExceptionType.ATTACHMENT_NOT_FOUND_EXCEPTION)))
                    .toList();

            problem.setAttachments(attachments);
            problem = problemRepository.save(problem);

            for (Attachment attachment : attachments) {
                attachment.setParentId(problem.getId().toString());
                attachmentRepository.save(attachment);
            }
        }

        quiz.setTitle(quizSaveDto.getTitle());
        quiz.setDescription(quizSaveDto.getDescription());
        quiz.setIsDraft(quizSaveDto.getIsDraft());
        quiz.setOpenTime(quizSaveDto.getOpenTime());
        quiz.setDueTime(quizSaveDto.getDueTime());
        quiz.setTimeLimit(quizSaveDto.getTimeLimit());

        quizRepository.save(quiz);
    }

    private void validateQuizFields(QuizSaveDto dto) {
        if (!Boolean.TRUE.equals(dto.getIsDraft())) {
            if (dto.getTitle() == null || dto.getOpenTime() == null || dto.getDueTime() == null || dto.getTimeLimit() == null) {
                throw new CustomException(ExceptionType.INVALID_QUIZ_FIELD_EXCEPTION);
            }
        }
    }


    public List<QuizListDto> findAllByLecture(Long lectureId, Boolean isDraft) {
        Lecture lecture = lectureRepository.findById(lectureId).orElseThrow(() -> new CustomException(ExceptionType.LECTURE_NOT_FOUND_EXCEPTION));
        List<Quiz> quizList = quizRepository.findAllByLectureAndIsDraftOrderByCreatedAtDesc(lecture, isDraft);

        return quizListToDtoList(quizList);
    }

    public List<QuizListDto> loadSavedQuizzes(Long lectureId) {
        Lecture lecture = lectureRepository.findById(lectureId).orElseThrow(() -> new CustomException(ExceptionType.LECTURE_NOT_FOUND_EXCEPTION));
        List<Quiz> quizzes = quizRepository.findAllByLectureAndIsDraftFalseOrderByCreatedAtDesc(lecture);

        return quizzes.stream().map(quiz -> QuizListDto.builder()
                .id(quiz.getId())
                .quizName(quiz.getTitle())
                .quizDate(quiz.getCreatedAt().toString())
                .build()).toList();
    }

    public TeacherQuizListDto teacherQuizList(Long lectureId, Boolean isDraft) {
        Lecture lecture = lectureRepository.findById(lectureId).orElseThrow(() -> new CustomException(ExceptionType.LECTURE_NOT_FOUND_EXCEPTION));
        List<Quiz> quizList = quizRepository.findAllByLectureAndIsDraftOrderByCreatedAtDesc(lecture, isDraft);
        List<Quiz> openQuizzes = new ArrayList<>();
        List<Quiz> closedQuizzes = new ArrayList<>();

        LocalDateTime now = LocalDateTime.now();

        for(Quiz quiz : quizList) {
            if(quiz.getDueTime() != null && quiz.getDueTime().isBefore(now)) {
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

    public List<QuizListDto> findOpenQuizList(Long lectureId) {
        Lecture lecture = lectureRepository.findById(lectureId).orElseThrow(() -> new CustomException(ExceptionType.LECTURE_NOT_FOUND_EXCEPTION));
        List<Quiz> quizList = quizRepository.findAllByLectureAndIsDraftAndIsOpenedOrderByCreatedAtDesc(lecture, false, true);

        return quizListToDtoList(quizList);
    }

    private List<QuizListDto> quizListToDtoList(List<Quiz> quizList) {
        List<QuizListDto> quizListDtos = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        for(Quiz quiz : quizList) {
            QuizListDto quizListDto = QuizListDto.builder()
                    .id(quiz.getId())
                    .author(Optional.ofNullable(quiz.getAuthor())
                            .map(Member::getNickname)
                            .orElse(quiz.getLecture().getTeacher().getNickname()))
                    .quizName(quiz.getTitle())
                    .timeLimit(quiz.getTimeLimit())
                    .quizDate(quiz.getDueTime() == null? null : quiz.getDueTime().format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm")))
                    .quizAvailable(!quiz.getIsDraft() && quiz.getIsOpened() && quiz.getOpenTime().isBefore(now) && quiz.getDueTime().isAfter(now))
                    .submitCount(problemSubmitRepository.countAllByProblem(problemRepository.findFirstByQuiz(quiz)))
                    .totalCount(quiz.getLecture().getStudentLectures().size())
                    .build();
            quizListDtos.add(quizListDto);
        }
        return quizListDtos;
    }

    public void deleteQuiz(Long quizId) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new CustomException(ExceptionType.QUIZ_NOT_FOUND_EXCEPTION));

        problemSubmitRepository.deleteAllByProblem_Quiz(quiz);

        List<Problem> problems = problemRepository.findAllByQuiz(quiz);

        for (Problem problem : problems) {
            for (Attachment attachment : problem.getAttachments()) {
                attachmentService.delete(attachment.getId());
            }
        }

        problemRepository.deleteAll(problems);
        quizRepository.delete(quiz);
    }

    public QuizDetailDto findQuiz(Long quizId) {
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
                    .fileIds(problem.getAttachments().stream().map(Attachment::getId).toList())
                    .filePaths(problem.getAttachments().stream().map(Attachment::getFilePath).toList())
                    .build();
            problemDtos.add(problemDto);
        }

        return QuizDetailDto.builder()
                .title(quiz.getTitle())
                .description(quiz.getDescription())
                .isOpened(quiz.getIsOpened())
                .openTime(quiz.getOpenTime())
                .dueTime(quiz.getDueTime())
                .createdAt(quiz.getCreatedAt())
                .timeLimit(quiz.getTimeLimit())
                .problems(problemDtos)
                .author(AuthorDto.builder()
                        .name(quiz.getAuthor().getNickname())
                        .image(Optional.ofNullable(quiz.getAuthor().getImage())
                                .map(Attachment::getFilePath)
                                .orElse(null))
                        .build())
                .build();
    }

    public QuizDetailDto findQuiz(Long quizId, String username) {
        Quiz quiz = quizRepository.findById(quizId).orElseThrow(() -> new CustomException(ExceptionType.QUIZ_NOT_FOUND_EXCEPTION));
        Member member = memberRepository.findByUsernameAndRole(username, Role.STUDENT).orElseThrow(() -> new CustomException(ExceptionType.MEMBER_NOT_FOUND_EXCEPTION));

        List<Problem> problems = problemRepository.findAllByQuiz(quiz);
        List<ProblemDto> problemDtos = new ArrayList<>();

        for(Problem problem : problems) {
            ProblemDto problemDto = ProblemDto.builder()
                    .id(problem.getId())
                    .type(problem.getType().toString())
                    .content(problem.getContent())
                    .answer(problem.getAnswer())
                    .items(problem.getItems())
                    .fileIds(problem.getAttachments().stream().map(Attachment::getId).toList())
                    .filePaths(problem.getAttachments().stream().map(Attachment::getFilePath).toList())
                    .build();
            problemDtos.add(problemDto);
        }

        return QuizDetailDto.builder()
                .title(quiz.getTitle())
                .description(quiz.getDescription())
                .isOpened(quiz.getIsOpened())
                .isSubmitted(problemSubmitRepository.existsByStudentAndProblem_Quiz(member, quiz))
                .openTime(quiz.getOpenTime())
                .dueTime(quiz.getDueTime())
                .createdAt(quiz.getCreatedAt())
                .timeLimit(quiz.getTimeLimit())
                .problems(problemDtos)
                .author(AuthorDto.builder()
                        .name(quiz.getAuthor().getNickname())
                        .image(Optional.ofNullable(quiz.getAuthor().getImage())
                                .map(Attachment::getFilePath)
                                .orElse(null))
                        .build())
                .build();
    }

    public void openQuiz(Long quizId) throws Exception {
        Quiz quiz = quizRepository.findById(quizId).orElseThrow(() -> new CustomException(ExceptionType.QUIZ_NOT_FOUND_EXCEPTION));
        LocalDateTime now = LocalDateTime.now();
        quiz.setIsOpened(true);
        quiz.setOpenTime(now);
        quizRepository.save(quiz);

        sendNotification(quiz, "퀴즈가 등록되었어요.", NotificationDetailType.REGISTER);
    }

    public void sendNotification(Quiz quiz, String body, NotificationDetailType detailType) throws Exception {
        studentNotificationService.create(quiz.getLecture().getStudentLectures().stream().map(studentLecture -> studentLecture.getStudent().getId()).toList(),
                StudentNotificationRequestDto.builder()
                        .parentId(quiz.getId())
                        .type(NotificationType.QUIZ)
                        .detailType(detailType)
                        .header(quiz.getLecture().getTitle())
                        .title(quiz.getTitle())
                        .body(body)
                        .content(quiz.getDescription())
                        .build());
    }

    public void closeQuiz(Long quizId) {
        Quiz quiz = quizRepository.findById(quizId).orElseThrow(() -> new CustomException(ExceptionType.QUIZ_NOT_FOUND_EXCEPTION));
        LocalDateTime now = LocalDateTime.now();
        quiz.setDueTime(now);
        quizRepository.save(quiz);
    }

    public List<QuizStatsDto> getQuizStats(Long quizId) {
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
                            .filter(problemSubmit -> !problemSubmit.getContent().equalsIgnoreCase(problem.getAnswer()))
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

    public ProblemStatsDto getProblemStats(Long problemId) {
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
                    .filter(content -> content != null && !content.isEmpty() && !content.equalsIgnoreCase(problem.getAnswer()))
                    .toList();

            Map<String, Long> contentCounts = submissions.stream()
                    .collect(Collectors.groupingBy(content -> content, Collectors.counting()));

            List<Map.Entry<String, Long>> sortedEntries = contentCounts.entrySet().stream()
                    .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                    .toList();

            int rank = 1;
            int previousRank = 1;
            long previousCount = -1;

            for (Map.Entry<String, Long> entry : sortedEntries) {
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

    public List<StudentQuizDto> findQuizzes(Long lectureId, String username) {
        Lecture lecture = lectureRepository.findById(lectureId).orElseThrow(() -> new CustomException(ExceptionType.LECTURE_NOT_FOUND_EXCEPTION));
        Member student = memberRepository.findByUsername(username).orElseThrow(() -> new CustomException(ExceptionType.MEMBER_NOT_FOUND_EXCEPTION));

        List<Quiz> quizzes = quizRepository.findAllByLectureAndIsDraftAndIsOpenedOrderByCreatedAtDesc(lecture, false, true);

        return quizzes.stream()
                .map(quiz -> StudentQuizDto.builder()
                        .quizId(quiz.getId())
                        .title(quiz.getTitle())
                        .author(quiz.getAuthor().getNickname())
                        .openTime(quiz.getOpenTime())
                        .dueTime(quiz.getDueTime())
                        .isSubmitted(problemSubmitRepository.existsByStudentAndProblem_Quiz(student, quiz))
                        .build())
                .toList();
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void deleteAllExpiredDrafts() {
        List<Quiz> quizzes = quizRepository.findAllByIsDraftTrueAndCreatedAtBefore(LocalDateTime.now().minusDays(30));
        quizzes.forEach(quiz -> deleteQuiz(quiz.getId()));
    }

    public void deleteByLecture(Lecture lecture) {
        List<Quiz> quizzes = quizRepository.findAllByLecture(lecture);
        quizzes.forEach(quiz -> deleteQuiz(quiz.getId()));
    }
}
