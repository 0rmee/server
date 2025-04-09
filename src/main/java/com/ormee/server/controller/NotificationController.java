package com.ormee.server.controller;

import com.ormee.server.dto.response.ResponseDto;
import com.ormee.server.service.NotificationService;
import org.springframework.web.bind.annotation.*;

@RestController
public class NotificationController {
    private final NotificationService notificationService;
    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping("/{lectureId}/notifications") // 추후 서비스로직 추가
    public ResponseDto getNotifications(@PathVariable Long lectureId, @RequestParam(required = false, defaultValue = "전체") String filter) {
        return ResponseDto.success();
    }

    @PutMapping("/teachers/notifications/{notificationId}") // 추후 서비스로직 추가
    public ResponseDto readNotification(@PathVariable Long notificationId) {
        // 읽음 로직
        return ResponseDto.success();
    }

    @DeleteMapping("/teachers/notifications/{notificationId}") // 추후 서비스로직 추가
    public ResponseDto deleteNotification(@PathVariable Long notificationId) {
        return ResponseDto.success();
    }
}
