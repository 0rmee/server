package com.ormee.server.quiz.controller;

import com.ormee.server.quiz.service.QuizService;
import com.ormee.server.quiz.dto.QuizSaveDto;
import com.ormee.server.global.response.ResponseDto;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/teachers")
public class TeacherQuizController {

    private final QuizService quizService;

    public TeacherQuizController(QuizService quizService) {
        this.quizService = quizService;
    }

    @PostMapping("/{lectureId}/quizzes")
    public ResponseDto createQuiz(@PathVariable Long lectureId, @RequestBody QuizSaveDto quizSaveDto, Authentication authentication) throws IOException {
        quizService.saveQuiz(lectureId, quizSaveDto, authentication.getName());
        return ResponseDto.success();
    }

    @GetMapping("/quizzes/{quizId}")
    public ResponseDto readQuiz(@PathVariable Long quizId) {
        return ResponseDto.success(quizService.findQuiz(quizId));
    }

    @GetMapping("/{lectureId}/quizzes")
    public ResponseDto readQuizList(@PathVariable Long lectureId) {
        return ResponseDto.success(quizService.teacherQuizList(lectureId, false));
    }

    @GetMapping("/{lectureId}/quizzes/draft")
    public ResponseDto readDraftQuizList(@PathVariable Long lectureId) {
        return ResponseDto.success(quizService.findAllByLecture(lectureId, true));
    }

    @GetMapping("/{lectureId}/quizzes/load")
    public ResponseDto loadQuizzes(@PathVariable Long lectureId) {
        return ResponseDto.success(quizService.loadSavedQuizzes(lectureId));
    }

    @PutMapping("/quizzes/{quizId}")
    public ResponseDto updateQuiz(@PathVariable Long quizId, @RequestBody QuizSaveDto quizSaveDto) {
        quizService.modifyQuiz(quizId, quizSaveDto);
        return ResponseDto.success();
    }

    @DeleteMapping("/quizzes/{quizId}")
    public ResponseDto deleteQuiz(@PathVariable Long quizId) {
        quizService.deleteQuiz(quizId);
        return ResponseDto.success();
    }

    @PutMapping("/quizzes/{quizId}/open")
    public ResponseDto openQuiz(@PathVariable Long quizId) throws Exception {
        quizService.openQuiz(quizId);
        return ResponseDto.success();
    }

    @PutMapping("/quizzes/{quizId}/close")
    public ResponseDto closeQuiz(@PathVariable Long quizId) {
        quizService.closeQuiz(quizId);
        return ResponseDto.success();
    }

    @GetMapping("/quizzes/{quizId}/stats")
    public ResponseDto readQuizStats(@PathVariable Long quizId) {
        return ResponseDto.success(quizService.getQuizStats(quizId));
    }

    @GetMapping("/quizzes/problems/{problemId}/stats")
    public ResponseDto readProblemStats(@PathVariable Long problemId) {
        return ResponseDto.success(quizService.getProblemStats(problemId));
    }

    // 아래는 기존 학생 uri
//
//    @GetMapping("/student/{lectureId}")
//    public ResponseDto readOpenQuizList(@PathVariable Long lectureId) {
//        return ResponseDto.success(quizService.findOpenQuizList(lectureId));
//    }
}
