package com.ormee.server.controller;

import com.ormee.server.dto.question.QuestionSaveDto;
import com.ormee.server.dto.response.ResponseDto;
import com.ormee.server.exception.CustomException;
import com.ormee.server.exception.ExceptionType;
import com.ormee.server.service.QuestionService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
public class QuestionController {
    private final QuestionService questionService;
    public QuestionController(QuestionService questionService) {
        this.questionService = questionService;
    }

    @GetMapping("/teachers/{lectureId}/questions")
    public ResponseDto readQuestionList(@PathVariable Long lectureId, @RequestParam(required = false, defaultValue = "전체") String filter,@RequestParam(defaultValue = "1") int page) {
        return switch (filter) {
            case "전체" -> ResponseDto.success(questionService.getQuestions(lectureId, page - 1));
            case "등록" -> ResponseDto.success(questionService.getAnsweredQuestions(lectureId, page - 1));
            case "미등록" -> ResponseDto.success(questionService.getNotAnsweredQuestions(lectureId, page - 1));
            default -> throw new CustomException(ExceptionType.FILTER_INVALID_EXCEPTION);
        };
    }

//    @GetMapping("/teacher/{lectureId}/isAnswer")
//    public ResponseDto readIsAnswerQuestionList(@PathVariable UUID lectureId) {
//        return ResponseDto.success(questionService.findAllByLectureAndIsAnswered(lectureId, true));
//    }

    @GetMapping("/teachers/questions/{questionId}")
    public ResponseDto readQuestion(@PathVariable Long questionId) {
        return ResponseDto.success(questionService.findById(questionId));
    }

    @PostMapping("/students/{lectureId}/questions")
    public ResponseDto createQuestion(@PathVariable Long lectureId, @ModelAttribute QuestionSaveDto questionSaveDto, Authentication authentication) throws IOException {
        questionService.saveQuestion(lectureId, questionSaveDto, authentication.getName());
        return ResponseDto.success();
    }

    @PutMapping("/students/questions/{questionId}")
    public ResponseDto updateQuestion(@PathVariable Long questionId, @ModelAttribute QuestionSaveDto questionSaveDto) throws IOException {
        questionService.modifyQuestion(questionId, questionSaveDto);
        return ResponseDto.success();
    }

    @DeleteMapping("/students/questions/{questionId}")
    public ResponseDto deleteQuestion(@PathVariable Long questionId) {
        questionService.deleteQuestion(questionId);
        return ResponseDto.success();
    }
}
