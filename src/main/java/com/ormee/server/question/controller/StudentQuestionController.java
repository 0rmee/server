package com.ormee.server.question.controller;

import com.ormee.server.question.dto.QuestionSaveDto;
import com.ormee.server.global.response.ResponseDto;
import com.ormee.server.global.exception.CustomException;
import com.ormee.server.global.exception.ExceptionType;
import com.ormee.server.question.service.AnswerService;
import com.ormee.server.question.service.QuestionService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/students")
public class StudentQuestionController {
    private final QuestionService questionService;
    private final AnswerService answerService;
    public StudentQuestionController(QuestionService questionService, AnswerService answerService) {
        this.questionService = questionService;
        this.answerService = answerService;
    }

    @PostMapping("/lectures/{lectureId}/questions")
    public ResponseDto createQuestion(@PathVariable Long lectureId, @ModelAttribute QuestionSaveDto questionSaveDto, Authentication authentication) throws IOException {
        questionService.saveQuestion(lectureId, questionSaveDto, authentication.getName());
        return ResponseDto.success();
    }

    @GetMapping("/lectures/{lectureId}/questions")
    public ResponseDto getQuestions(@PathVariable Long lectureId) {
        return ResponseDto.success();
    }

    @GetMapping("/questions/my")
    public ResponseDto getMyQuestions(Authentication authentication) {
        return ResponseDto.success();
    }

    @GetMapping("/questions/{questionId}")
    public ResponseDto getQuestion(@PathVariable Long questionId) {
        return ResponseDto.success();
    }

    @PutMapping("/questions/{questionId}")
    public ResponseDto updateQuestion(@PathVariable Long questionId, @ModelAttribute QuestionSaveDto questionSaveDto) throws IOException {
        questionService.modifyQuestion(questionId, questionSaveDto);
        return ResponseDto.success();
    }

    @DeleteMapping("/questions/{questionId}")
    public ResponseDto deleteQuestion(@PathVariable Long questionId) {
        questionService.deleteQuestion(questionId);
        return ResponseDto.success();
    }

    @GetMapping("/questions/{questionId}/answers")
    public ResponseDto getAnswer(@PathVariable Long questionId) {
        return ResponseDto.success();
    }
}
