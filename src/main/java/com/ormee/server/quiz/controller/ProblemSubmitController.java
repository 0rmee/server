package com.ormee.server.quiz.controller;

import com.ormee.server.quiz.service.ProblemSubmitService;
import com.ormee.server.quiz.dto.SubmitDto;
import com.ormee.server.global.response.ResponseDto;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ProblemSubmitController {
    private final ProblemSubmitService problemSubmitService;

    public ProblemSubmitController(ProblemSubmitService problemSubmitService) {
        this.problemSubmitService = problemSubmitService;
    }

    @PostMapping("problems/student")
    public ResponseDto submitQuiz(@RequestBody List<SubmitDto> submissions, Authentication authentication) {
        problemSubmitService.submit(submissions, authentication);
        return ResponseDto.success();
    }

    @GetMapping("/{quizId}/student/result")
    public ResponseDto readQuizResult(@PathVariable Long quizId, Authentication authentication) {
        return ResponseDto.success(problemSubmitService.getStudentResult(quizId, authentication));
    }
}
