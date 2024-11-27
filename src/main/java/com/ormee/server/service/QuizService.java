package com.ormee.server.service;

import com.ormee.server.dto.*;
import com.ormee.server.model.*;
import com.ormee.server.repository.LectureRepository;
import com.ormee.server.repository.ProblemRepository;
import com.ormee.server.repository.QuizRepository;
import com.ormee.server.repository.SubmitRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
public class QuizService {

    private final QuizRepository quizRepository;
    private final LectureRepository lectureRepository;
    private final ProblemRepository problemRepository;
    private final SubmitRepository submitRepository;

    public QuizService(QuizRepository quizRepository, LectureRepository lectureRepository, ProblemRepository problemRepository, SubmitRepository submitRepository) {
        this.quizRepository = quizRepository;
        this.lectureRepository = lectureRepository;
        this.problemRepository = problemRepository;
        this.submitRepository = submitRepository;
    }

    public void saveQuiz(UUID lectureId, QuizSaveDto quizSaveDto) {
        Lecture lecture = lectureRepository.findById(lectureId).orElseThrow();

        Quiz quiz = Quiz.builder()
                .lecture(lecture)
                .title(quizSaveDto.getTitle())
                .description(quizSaveDto.getDescription())
                .isDraft(quizSaveDto.getIsDraft())
                .isOpened(quizSaveDto.getOpenTime().isBefore(LocalDateTime.now()))
                .openTime(quizSaveDto.getOpenTime())
                .dueTime(quizSaveDto.getDueTime())
                .timeLimit(quizSaveDto.getTimeLimit())
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
        Quiz quiz = quizRepository.findById(quizId).orElseThrow();
        deleteQuiz(quizId);

        saveQuiz(quiz.getLecture().getId(), quizSaveDto);
    }

    public List<QuizListDto> findAllByLecture(UUID lectureId) {
        Lecture lecture = lectureRepository.findById(lectureId).orElseThrow();
        List<Quiz> quizList = quizRepository.findAllByLecture(lecture);

        return quizListToDtoList(quizList);
    }

    public List<QuizListDto> findAllDrafts(UUID lectureId) {
        Lecture lecture = lectureRepository.findById(lectureId).orElseThrow();
        List<Quiz> quizList = quizRepository.findAllByLectureAndIsDraft(lecture, true);

        return quizListToDtoList(quizList);
    }

    public List<QuizListDto> findOpenQuizList(UUID lectureId) {
        Lecture lecture = lectureRepository.findById(lectureId).orElseThrow();
        List<Quiz> quizList = quizRepository.findAllByLectureAndAndIsOpenedAndOpenTimeBeforeOrderByOpenTimeDesc(lecture, true, LocalDateTime.now());

        return quizListToDtoList(quizList);
    }

    private List<QuizListDto> quizListToDtoList(List<Quiz> quizList) {
        List<QuizListDto> quizListDtos = new ArrayList<>();

        for(Quiz quiz : quizList) {
            QuizListDto quizListDto = QuizListDto.builder()
                    .id(quiz.getId().toString())
                    .quizName(quiz.getTitle())
                    .timeLimit(quiz.getTimeLimit())
                    .quizDate(quiz.getDueTime().format(DateTimeFormatter.ofPattern("yyyy.MM.dd (E)", Locale.KOREAN)))
                    .quizAvailable(quiz.getIsOpened() && quiz.getDueTime().isBefore(LocalDateTime.now()))
                    .build();
            quizListDtos.add(quizListDto);
        }
        return quizListDtos;
    }

    public void deleteQuiz(UUID quizId) {
        Quiz quiz = quizRepository.findById(quizId).orElseThrow();

        List<Problem> problems = problemRepository.findAllByQuiz(quiz);

        for(Problem problem : problems) {
            problemRepository.delete(problem);
        }

        quizRepository.delete(quiz);
    }

    public List<ProblemDto> findQuiz(UUID quizId) {
        Quiz quiz = quizRepository.findById(quizId).orElseThrow();

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

        return problemDtos;
    }

    public void submit(SubmitRequestDto submitRequestDto) {
        String author = submitRequestDto.getAuthor();
        String password = submitRequestDto.getPassword();
        List<SubmitDto> submitDtos = submitRequestDto.getSubmissions();

        for(SubmitDto submitDto : submitDtos) {
            Problem problem = problemRepository.findById(submitDto.getProblemId()).orElseThrow();
            Submit submit = Submit.builder()
                    .problem(problem)
                    .author(author)
                    .password(password)
                    .content(submitDto.getContent())
                    .build();
            submitRepository.save(submit);
        }
    }

    public StudentQuizResultDto getStudentResult(UUID quizId, String author, String password) {
        Quiz quiz = quizRepository.findById(quizId).orElseThrow();
        List<Problem> problems = problemRepository.findAllByQuiz(quiz);
        List<ProblemDto> problemDtos = new ArrayList<>();
        Integer correct = 0;
        for(Problem problem : problems) {
            Submit submit = submitRepository.findByProblemAndAuthorAndPassword(problem, author, password).orElseThrow();
            ProblemDto problemDto = ProblemDto.builder()
                    .id(problem.getId())
                    .content(problem.getContent())
                    .type(problem.getType().toString())
                    .answer(problem.getAnswer())
                    .submission(submit.getContent())
                    .isCorrect(problem.getAnswer().equals(submit.getContent()))
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

}
