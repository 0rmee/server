package com.ormee.server.quiz.controller;

import com.ormee.server.quiz.service.ProblemSubmitService;
import com.ormee.server.quiz.dto.SubmitDto;
import com.ormee.server.global.response.ResponseDto;
import com.ormee.server.quiz.service.QuizService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/students")
public class StudentQuizController {
    private final QuizService quizService;
    private final ProblemSubmitService problemSubmitService;

    public StudentQuizController(QuizService quizService, ProblemSubmitService problemSubmitService) {
        this.quizService = quizService;
        this.problemSubmitService = problemSubmitService;
    }

    @PostMapping("/quizzes")
    public ResponseDto submitQuiz(@RequestBody List<SubmitDto> submissions, Authentication authentication) {
        problemSubmitService.submit(submissions, authentication);
        return ResponseDto.success();
    }

    @GetMapping("/quizzes/{quizId}")
    public ResponseDto readQuiz(@PathVariable Long quizId) {
        return ResponseDto.success(quizService.findQuiz(quizId));
    }

    @GetMapping("/quizzes/{quizId}/result")
    public ResponseDto readQuizResult(@PathVariable Long quizId, Authentication authentication) {
        return ResponseDto.success(problemSubmitService.getStudentResult(quizId, authentication));
    }

    @GetMapping("/lectures/{lectureId}/quizzes")
    public ResponseDto readQuizzes(@PathVariable Long lectureId, Authentication authentication) {
        return ResponseDto.success(quizService.findQuizzes(lectureId, authentication.getName()));
    }
}
