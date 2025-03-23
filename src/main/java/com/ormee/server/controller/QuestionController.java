package com.ormee.server.controller;

import com.ormee.server.dto.question.QuestionSaveDto;
import com.ormee.server.dto.response.ResponseDto;
import com.ormee.server.service.QuestionService;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/questions")
public class QuestionController {
    private final QuestionService questionService;
    public QuestionController(QuestionService questionService) {
        this.questionService = questionService;
    }

    @PostMapping("/student/{lectureId}")
    public ResponseDto createQuestion(@PathVariable UUID lectureId, @RequestBody QuestionSaveDto questionSaveDto) {
        questionService.saveQuestion(lectureId, questionSaveDto);
        return ResponseDto.success();
    }

    @PutMapping("/student/{questionId}")
    public ResponseDto updateQuestion(@PathVariable Long questionId, @RequestBody QuestionSaveDto questionSaveDto) {
        questionService.modifyQuestion(questionId, questionSaveDto);
        return ResponseDto.success();
    }

    @DeleteMapping("/student/{questionId}")
    public ResponseDto deleteQuestion(@PathVariable Long questionId) {
        questionService.deleteQuestion(questionId);
        return ResponseDto.success();
    }

    @GetMapping("/teacher/{lectureId}")
    public ResponseDto readQuestionList(@PathVariable UUID lectureId) {
        return ResponseDto.success(questionService.findAllByLecture(lectureId));
    }

    @GetMapping("/teacher/{lectureId}/isAnswer")
    public ResponseDto readIsAnswerQuestionList(@PathVariable UUID lectureId) {
        return ResponseDto.success(questionService.findAllByLectureAndIsAnswered(lectureId, true));
    }

    @GetMapping("/teacher/{questionId}")
    public ResponseDto readQuestion(@PathVariable Long questionId) {
        return ResponseDto.success(questionService.findById(questionId));
    }
}
