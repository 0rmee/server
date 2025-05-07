package com.ormee.server.controller;

import com.ormee.server.dto.quiz.SubmitDto;
import com.ormee.server.dto.response.ResponseDto;
import com.ormee.server.service.ProblemSubmitService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("/problems")
public class ProblemSubmitController {
    private final ProblemSubmitService problemSubmitService;

    public ProblemSubmitController(ProblemSubmitService problemSubmitService) {
        this.problemSubmitService = problemSubmitService;
    }

    @PostMapping("/student")
    public ResponseDto submitQuiz(@RequestBody List<SubmitDto> submissions, Authentication authentication) {
        problemSubmitService.submit(submissions, authentication);
        return ResponseDto.success();
    }

    @GetMapping("/{quizId}/student/result")
    public ResponseDto readQuizResult(@PathVariable Long quizId, Authentication authentication) {
        return ResponseDto.success(problemSubmitService.getStudentResult(quizId, authentication));
    }
}
