package com.ormee.server.home.controller;

import com.ormee.server.global.response.ResponseDto;
import com.ormee.server.home.service.HomeService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TeacherHomeController {
    private final HomeService homeService;

    public TeacherHomeController(HomeService homeService) {
        this.homeService = homeService;
    }

    @GetMapping("/home/{lectureId}")
    public ResponseDto getHomeInfo(@PathVariable Long lectureId) {
        return ResponseDto.success(homeService.getInfo(lectureId));
    }
}
