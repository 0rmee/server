package com.ormee.server.controller;

import com.ormee.server.dto.quiz.QuizSaveDto;
import com.ormee.server.dto.quiz.SubmitRequestDto;
import com.ormee.server.dto.response.ResponseDto;
import com.ormee.server.service.QuizService;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/quizes")
public class QuizController {

    private final QuizService quizService;

    public QuizController(QuizService quizService) {
        this.quizService = quizService;
    }

    @PostMapping("/teacher/{lectureId}")
    public ResponseDto createQuiz(@PathVariable UUID lectureId, @RequestBody QuizSaveDto quizSaveDto) {
        quizService.saveQuiz(lectureId, quizSaveDto);
        return ResponseDto.success();
    }

    @PutMapping("/teacher/{quizId}")
    public ResponseDto updateQuiz(@PathVariable UUID quizId, @RequestBody QuizSaveDto quizSaveDto) {
        quizService.modifyQuiz(quizId, quizSaveDto);
        return ResponseDto.success();
    }

    @DeleteMapping("teacher/{quizId}")
    public ResponseDto deleteQuiz(@PathVariable UUID quizId) {
        quizService.deleteQuiz(quizId);
        return ResponseDto.success();
    }

    @GetMapping("/teacher/{lectureId}")
    public ResponseDto readQuizList(@PathVariable UUID lectureId) {
        return ResponseDto.success(quizService.teacherQuizList(lectureId, false));
    }

    @GetMapping("/teacher/{lectureId}/draft")
    public ResponseDto readDraftQuizList(@PathVariable UUID lectureId) {
        return ResponseDto.success(quizService.findAllByLecture(lectureId, true));
    }

    @PutMapping("/teacher/{quizId}/open")
    public ResponseDto openQuiz(@PathVariable UUID quizId) {
        quizService.openQuiz(quizId);
        return ResponseDto.success();
    }

    @PutMapping("/teacher/{quizId}/close")
    public ResponseDto closeQuiz(@PathVariable UUID quizId) {
        quizService.closeQuiz(quizId);
        return ResponseDto.success();
    }

    @GetMapping("/{quizId}")
    public ResponseDto readQuiz(@PathVariable UUID quizId) {
        return ResponseDto.success(quizService.findQuiz(quizId));
    }

    @GetMapping("/student/{lectureId}")
    public ResponseDto readOpenQuizList(@PathVariable UUID lectureId) {
        return ResponseDto.success(quizService.findOpenQuizList(lectureId));
    }

    @PostMapping("/student")
    public ResponseDto submitQuiz(@RequestBody SubmitRequestDto submitRequestDto) {
        quizService.submit(submitRequestDto);
        return ResponseDto.success();
    }

    @GetMapping("/{quizId}/student/result")
    public ResponseDto readQuizResult(@RequestParam(name = "author") String author, @RequestParam(name = "password") String password, @PathVariable UUID quizId) {
        return ResponseDto.success(quizService.getStudentResult(quizId, author, password));
    }

    @GetMapping("/{quizId}/student/check")
    public ResponseDto checkStudent(@RequestParam(name = "author") String author, @RequestParam(name = "password") String password, @PathVariable UUID quizId) {
        return ResponseDto.success(quizService.checkStudent(quizId, author, password));
    }

    @GetMapping("/{quizId}/teacher/statistics")
    public ResponseDto readQuizStatistics(@PathVariable UUID quizId) {
        return ResponseDto.success(quizService.getStatistics(quizId));
    }

    @GetMapping("/teacher/statistics/{problemId}")
    public ResponseDto readProblemStatistics(@PathVariable Long problemId) {
        return ResponseDto.success(quizService.getProblemstats(problemId));
    }
}
