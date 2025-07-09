package com.ormee.server.question.controller;

import com.ormee.server.question.dto.QuestionSaveDto;
import com.ormee.server.global.response.ResponseDto;
import com.ormee.server.question.service.AnswerService;
import com.ormee.server.question.service.QuestionService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

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
    public ResponseDto createQuestion(@PathVariable Long lectureId, @RequestBody QuestionSaveDto questionSaveDto, Authentication authentication) {
        questionService.saveQuestion(lectureId, questionSaveDto, authentication.getName());
        return ResponseDto.success();
    }

    @GetMapping("/lectures/{lectureId}/questions")
    public ResponseDto getQuestions(@PathVariable Long lectureId, Authentication authentication) {
        return ResponseDto.success(questionService.getQuestions(lectureId, authentication.getName()));
    }

    @GetMapping("/questions/my")
    public ResponseDto getMyQuestions(Authentication authentication) {
        return ResponseDto.success(questionService.getMyQuestions(authentication.getName()));
    }

    @GetMapping("/questions/{questionId}")
    public ResponseDto getQuestion(@PathVariable Long questionId) {
        return ResponseDto.success(questionService.findById(questionId));
    }

    @PutMapping("/questions/{questionId}")
    public ResponseDto updateQuestion(@PathVariable Long questionId, @RequestBody QuestionSaveDto questionSaveDto) {
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
        return ResponseDto.success(answerService.getByQuestion(questionId));
    }
}
