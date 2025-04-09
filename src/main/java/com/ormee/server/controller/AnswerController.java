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

    @PostMapping("/teachers/questions/{questionId}")
    public ResponseDto createAnswer(@PathVariable Long questionId, @RequestBody AnswerSaveDto answerSaveDto) {
        answerService.writeAnswer(questionId, answerSaveDto);
        return ResponseDto.success();
    }

    @PutMapping("/teachers/answers/{answerId}")
    public ResponseDto updateAnswer(@PathVariable Long answerId, @RequestBody AnswerSaveDto answerSaveDto) {
        answerService.modifyAnswer(answerId, answerSaveDto);
        return ResponseDto.success();
    }

    @DeleteMapping("/teachers/answers/{answerId}")
    public ResponseDto deleteAnswer(@PathVariable Long answerId) {
        answerService.deleteAnswer(answerId);
        return ResponseDto.success();
    }
}
