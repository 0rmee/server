package com.ormee.server.notification.controller;

import com.ormee.server.global.response.ResponseDto;
import com.ormee.server.notification.service.NotificationService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/students/notifications")
public class StudentNotificationController {
    private final NotificationService notificationService;

    public StudentNotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    public ResponseDto getNotifications(@RequestParam(defaultValue = "과제") String filter) {
        return ResponseDto.success();
    }

    @GetMapping("/search")
    public ResponseDto searchNotifications(@RequestParam String keyword) {
        return ResponseDto.success();
    }

    @PutMapping("/{notificationId}")
    public ResponseDto readNotification(@PathVariable Long notificationId) {
        return ResponseDto.success();
    }

    @DeleteMapping("/{notificationId}")
    public ResponseDto deleteNotification(@PathVariable Long notificationId) {
        return ResponseDto.success();
    }
}
