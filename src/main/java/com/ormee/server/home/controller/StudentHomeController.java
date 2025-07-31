package com.ormee.server.home.controller;

import com.ormee.server.global.response.ResponseDto;
import com.ormee.server.home.dto.BannerRequestDto;
import com.ormee.server.home.service.StudentHomeService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping("/banners")
    public ResponseDto getBannerList(){
        return ResponseDto.success(studentHomeService.findAllBanner());
    }

    @PostMapping("/banners")
    public ResponseDto createBanners(@RequestBody List<BannerRequestDto> requests) {
        studentHomeService.replaceAllBanners(requests);
        return ResponseDto.success();
    }

}
