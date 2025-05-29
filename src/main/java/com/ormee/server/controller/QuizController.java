package com.ormee.server.controller;

import com.ormee.server.dto.quiz.QuizSaveDto;
import com.ormee.server.dto.response.ResponseDto;
import com.ormee.server.service.QuizService;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/teachers")
public class QuizController {

    private final QuizService quizService;

    public QuizController(QuizService quizService) {
        this.quizService = quizService;
    }

    @PostMapping("/{lectureId}/quizzes")
    public ResponseDto createQuiz(@PathVariable Long lectureId, @ModelAttribute QuizSaveDto quizSaveDto) throws IOException {
        quizService.saveQuiz(lectureId, quizSaveDto);
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

    @PutMapping("/quizzes/{quizId}")
    public ResponseDto updateQuiz(@PathVariable Long quizId, @RequestBody QuizSaveDto quizSaveDto) {
        quizService.modifyQuiz(quizId, quizSaveDto);
        return ResponseDto.success();
    }

    @PutMapping("/quizzes/problems/{problemId}") // 추후 problemSaveDto 추가
    public ResponseDto updateProblem(@PathVariable Long problemId) {
//        quizService.modifyProblem(problemId, problemSaveDto);
        return ResponseDto.success();
    }

    @DeleteMapping("/quizzes/{quizId}")
    public ResponseDto deleteQuiz(@PathVariable Long quizId) {
        quizService.deleteQuiz(quizId);
        return ResponseDto.success();
    }

    @PutMapping("/quizzes/{quizId}/open")
    public ResponseDto openQuiz(@PathVariable Long quizId) {
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
