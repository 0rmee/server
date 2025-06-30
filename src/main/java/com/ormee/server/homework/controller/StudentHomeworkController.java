package com.ormee.server.homework.controller;

import com.ormee.server.global.response.ResponseDto;
import com.ormee.server.homework.dto.HomeworkSubmitSaveDto;
import com.ormee.server.homework.service.HomeworkService;
import com.ormee.server.homework.service.HomeworkSubmitService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController("/student")
public class StudentHomeworkController {

    private final HomeworkService homeworkService;
    private final HomeworkSubmitService homeworkSubmitService;

    public StudentHomeworkController(HomeworkService homeworkService, HomeworkSubmitService homeworkSubmitService) {
        this.homeworkService = homeworkService;
        this.homeworkSubmitService = homeworkSubmitService;
    }

    @PostMapping("/homeworks/{homeworkId}")
    public ResponseDto createHomeworkSubmit(@PathVariable Long homeworkId, @ModelAttribute HomeworkSubmitSaveDto homeworkSubmitSaveDto, Authentication authentication) throws IOException {
        homeworkSubmitService.create(homeworkId, homeworkSubmitSaveDto, authentication.getName());
        return ResponseDto.success();
    }



    @GetMapping("/homeworks/submit/{homeworkSubmitId}")
    public ResponseDto getHomeworkSubmit(@PathVariable Long homeworkSubmitId, Authentication authentication) {
        return ResponseDto.success(homeworkSubmitService.get(homeworkSubmitId, authentication.getName()));
    }
}
