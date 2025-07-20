package com.ormee.server.question.controller;

import com.ormee.server.global.exception.CustomException;
import com.ormee.server.global.exception.ExceptionType;
import com.ormee.server.question.dto.AnswerSaveDto;
import com.ormee.server.global.response.ResponseDto;
import com.ormee.server.question.service.AnswerService;
import com.ormee.server.question.service.QuestionService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/teachers")
public class TeacherQuestionController {
    private final QuestionService questionService;
    private final AnswerService answerService;
    public TeacherQuestionController(QuestionService questionService, AnswerService answerService) {
        this.questionService = questionService;
        this.answerService = answerService;
    }

    @GetMapping("/{lectureId}/questions")
    public ResponseDto readQuestionList(@PathVariable Long lectureId, @RequestParam(required = false, defaultValue = "전체") String filter,@RequestParam(defaultValue = "1") int page) {
        return switch (filter) {
            case "전체" -> ResponseDto.success(questionService.getQuestions(lectureId, page - 1));
            case "등록" -> ResponseDto.success(questionService.getAnsweredQuestions(lectureId, page - 1));
            case "미등록" -> ResponseDto.success(questionService.getNotAnsweredQuestions(lectureId, page - 1));
            default -> throw new CustomException(ExceptionType.FILTER_INVALID_EXCEPTION);
        };
    }

    @GetMapping("/questions/{questionId}")
    public ResponseDto readQuestion(@PathVariable Long questionId) {
        return ResponseDto.success(questionService.findById(questionId));
    }

    @PostMapping("/questions/{questionId}")
    public ResponseDto createAnswer(@PathVariable Long questionId, @RequestBody AnswerSaveDto answerSaveDto, Authentication authentication) throws Exception {
        answerService.writeAnswer(questionId, answerSaveDto, authentication.getName());
        return ResponseDto.success();
    }

    @GetMapping("/questions/{questionId}/answers")
    public ResponseDto readAnswer(@PathVariable Long questionId) {
        return ResponseDto.success(answerService.getByQuestion(questionId));
    }

    @PutMapping("/answers/{answerId}")
    public ResponseDto updateAnswer(@PathVariable Long answerId, @RequestBody AnswerSaveDto answerSaveDto) {
        answerService.modifyAnswer(answerId, answerSaveDto);
        return ResponseDto.success();
    }

    @DeleteMapping("/answers/{answerId}")
    public ResponseDto deleteAnswer(@PathVariable Long answerId) {
        answerService.deleteAnswer(answerId);
        return ResponseDto.success();
    }
}
