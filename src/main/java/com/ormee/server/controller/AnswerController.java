package com.ormee.server.controller;

import com.ormee.server.dto.response.ResponseDto;
import com.ormee.server.model.Answer;
import com.ormee.server.service.AnswerService;
import org.springframework.web.bind.annotation.*;

@RestController("/answers")
public class AnswerController {
    private final AnswerService answerService;
    public AnswerController(AnswerService answerService) {
        this.answerService = answerService;
    }

    @PostMapping("/teacher/{questionId}")
    public ResponseDto createAnswer(@PathVariable Long questionId, @RequestBody Answer answer) {
        answerService.writeAnswer(questionId, answer);
        return ResponseDto.success();
    }

    @PutMapping("/teacher/{answerId}")
    public ResponseDto updateAnswer(@PathVariable Long answerId) {
        answerService.modifyAnswer(answerId);
        return ResponseDto.success();
    }

    @DeleteMapping("/teacher/{answerId}")
    public ResponseDto deleteAnswer(@PathVariable Long answerId) {
        answerService.deleteAnswer(answerId);
        return ResponseDto.success();
    }
}
