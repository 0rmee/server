package com.ormee.server.question.controller;

import com.ormee.server.question.dto.AnswerSaveDto;
import com.ormee.server.global.response.ResponseDto;
import com.ormee.server.question.service.AnswerService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
public class AnswerController {
    private final AnswerService answerService;
    public AnswerController(AnswerService answerService) {
        this.answerService = answerService;
    }

    @PostMapping("/teachers/questions/{questionId}")
    public ResponseDto createAnswer(@PathVariable Long questionId, @ModelAttribute AnswerSaveDto answerSaveDto, Authentication authentication) throws IOException {
        answerService.writeAnswer(questionId, answerSaveDto, authentication.getName());
        return ResponseDto.success();
    }

    @GetMapping("/teachers/questions/{questionId}/answers")
    public ResponseDto readAnswer(@PathVariable Long questionId) {
        return ResponseDto.success(answerService.getByQuestion(questionId));
    }

    @PutMapping("/teachers/answers/{answerId}")
    public ResponseDto updateAnswer(@PathVariable Long answerId, @RequestBody AnswerSaveDto answerSaveDto) throws IOException {
        answerService.modifyAnswer(answerId, answerSaveDto);
        return ResponseDto.success();
    }

    @DeleteMapping("/teachers/answers/{answerId}")
    public ResponseDto deleteAnswer(@PathVariable Long answerId) {
        answerService.deleteAnswer(answerId);
        return ResponseDto.success();
    }
}
