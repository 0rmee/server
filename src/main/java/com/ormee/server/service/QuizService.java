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
}
