package com.ormee.server.controller;

import com.ormee.server.dto.answer.AnswerSaveDto;
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
    public ResponseDto createAnswer(@PathVariable Long questionId, @RequestBody AnswerSaveDto answerSaveDto) {
        answerService.writeAnswer(questionId, answerSaveDto);
        return ResponseDto.success();
    }

    @PutMapping("/teacher/{questionId}")
    public ResponseDto updateAnswer(@PathVariable Long questionId, @RequestBody AnswerSaveDto answerSaveDto) {
        answerService.modifyAnswer(questionId, answerSaveDto);
        return ResponseDto.success();
    }

    @DeleteMapping("/teacher/{answerId}")
    public ResponseDto deleteAnswer(@PathVariable Long answerId) {
        answerService.deleteAnswer(answerId);
        return ResponseDto.success();
    }
}
