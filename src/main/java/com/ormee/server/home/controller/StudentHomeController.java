package com.ormee.server.home.controller;

import com.ormee.server.global.response.ResponseDto;
import com.ormee.server.home.service.StudentHomeService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/students/home")
public class StudentHomeController {
    private final StudentHomeService studentHomeService;

    public StudentHomeController(StudentHomeService studentHomeService) {this.studentHomeService = studentHomeService;}

    @GetMapping("/quizzes")
    public ResponseDto getHomeQuizList(Authentication authentication) {
        return ResponseDto.success(studentHomeService.findAllQuiz(authentication));
    }

    @GetMapping("/homeworks")
    public ResponseDto getHomeHomeworkList(Authentication authentication) {
        return ResponseDto.success(studentHomeService.findAllHomework(authentication));
    }
}
