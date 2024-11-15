package com.ormee.server.controller;

import com.ormee.server.dto.response.ResponseDto;
import com.ormee.server.service.TeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/teacher/users")
public class TeacherController {

    @Autowired
    private TeacherService teacherService;

    @GetMapping("/{id}")
    public ResponseDto teacherInfo(@PathVariable UUID id) {
        return ResponseDto.success(teacherService.getTeacherById(id));
    }

//    @GetMapping("/{id}")
//    public ResponseDto<TeacherDto> teacherInfo(@PathVariable UUID id) {
//        return ResponseDto.success(teacherService.getTeacherById(id));
//    }
}
