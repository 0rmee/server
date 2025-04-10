package com.ormee.server.controller;

import com.ormee.server.dto.quiz.ProblemDto;
import com.ormee.server.dto.quiz.QuizSaveDto;
import com.ormee.server.dto.response.ResponseDto;
import com.ormee.server.service.QuizService;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/teachers")
public class QuizController {

    private final QuizService quizService;

    public QuizController(QuizService quizService) {
        this.quizService = quizService;
    }

    @PostMapping("/{lectureId}/quizzes")
    public ResponseDto createQuiz(@PathVariable UUID lectureId, @RequestBody QuizSaveDto quizSaveDto) {
        quizService.saveQuiz(lectureId, quizSaveDto);
        return ResponseDto.success();
    }

    @GetMapping("/quizzes/{quizId}")
    public ResponseDto readQuiz(@PathVariable UUID quizId) {
        return ResponseDto.success(quizService.findQuiz(quizId));
    }

    @GetMapping("/{lectureId}/quizzes")
    public ResponseDto readQuizList(@PathVariable UUID lectureId) {
        return ResponseDto.success(quizService.teacherQuizList(lectureId, false));
    }

    @GetMapping("/{lectureId}/quizzes/draft")
    public ResponseDto readDraftQuizList(@PathVariable UUID lectureId) {
        return ResponseDto.success(quizService.findAllByLecture(lectureId, true));
    }

    @PutMapping("/quizzes/{quizId}")
    public ResponseDto updateQuiz(@PathVariable UUID quizId, @RequestBody QuizSaveDto quizSaveDto) {
        quizService.modifyQuiz(quizId, quizSaveDto);
        return ResponseDto.success();
    }

    @PutMapping("/quizzes/problems/{problemId}") // 추후 problemSaveDto 추가
    public ResponseDto updateProblem(@PathVariable UUID problemId) {
//        quizService.modifyProblem(problemId, problemSaveDto);
        return ResponseDto.success();
    }

    @DeleteMapping("/quizzes/{quizId}")
    public ResponseDto deleteQuiz(@PathVariable UUID quizId) {
        quizService.deleteQuiz(quizId);
        return ResponseDto.success();
    }

    @PutMapping("/quizzes/{quizId}/open")
    public ResponseDto openQuiz(@PathVariable UUID quizId) {
        quizService.openQuiz(quizId);
        return ResponseDto.success();
    }

    @PutMapping("/quizzes/{quizId}/close")
    public ResponseDto closeQuiz(@PathVariable UUID quizId) {
        quizService.closeQuiz(quizId);
        return ResponseDto.success();
    }

    @GetMapping("/quizzes/{quizId}/stats") // 추후 서비스로직 추가
    public ResponseDto readQuizStats(@PathVariable UUID quizId) {
        return ResponseDto.success();
    }

    @GetMapping("/quizzes/problems/{problemId}/stats") // 추후 서비스로직 추가
    public ResponseDto readProblemStats(@PathVariable UUID problemId) {
        return ResponseDto.success();
    }

    // 아래는 기존 학생 uri

    @GetMapping("/student/{lectureId}")
    public ResponseDto readOpenQuizList(@PathVariable UUID lectureId) {
        return ResponseDto.success(quizService.findOpenQuizList(lectureId));
    }
}
