package com.ormee.server.notification.controller;

import com.ormee.server.global.response.ResponseDto;
import com.ormee.server.notification.service.StudentNotificationService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/students/notifications")
public class StudentNotificationController {
    private final StudentNotificationService studentNotificationService;

    public StudentNotificationController(StudentNotificationService studentNotificationService) {
        this.studentNotificationService = studentNotificationService;
    }

    @GetMapping
    public ResponseDto getNotifications(@RequestParam(defaultValue = "과제") String type, Authentication authentication) {
        return ResponseDto.success(studentNotificationService.getByType(type, authentication.getName()));
    }

    @GetMapping("/search")
    public ResponseDto searchNotifications(@RequestParam String keyword, Authentication authentication) {
        return ResponseDto.success(studentNotificationService.search(keyword, authentication.getName()));
    }

    @PutMapping("/{notificationId}")
    public ResponseDto readNotification(@PathVariable Long notificationId) {
        studentNotificationService.read(notificationId);
        return ResponseDto.success();
    }

    @DeleteMapping("/{notificationId}")
    public ResponseDto deleteNotification(@PathVariable Long notificationId) {
        studentNotificationService.delete(notificationId);
        return ResponseDto.success();
    }
}
