package com.ormee.server.notification.controller;

import com.ormee.server.global.response.ResponseDto;
import com.ormee.server.notification.service.NotificationService;
import org.springframework.web.bind.annotation.*;

@RestController
public class NotificationController {
    private final NotificationService notificationService;
    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping("/teachers/{lectureId}/notifications")
    public ResponseDto getNotifications(@PathVariable Long lectureId, @RequestParam(required = false, defaultValue = "전체") String filter) {
        return ResponseDto.success(notificationService.getNotificationsByLectureIdAndType(lectureId, filter));
    }

    @PutMapping("/teachers/{lectureId}/notifications")
    public ResponseDto setAllNotificationsAsRead(@PathVariable Long lectureId) {
        notificationService.markAllAsRead(lectureId);
        return ResponseDto.success();
    }

    @DeleteMapping("/teachers/{lectureId}/notifications")
    public ResponseDto deleteAllNotifications(@PathVariable Long lectureId) {
        notificationService.deleteAll(lectureId);
        return ResponseDto.success();
    }

    @PutMapping("/teachers/notifications/{notificationId}")
    public ResponseDto setNotificationAsRead(@PathVariable Long notificationId) {
        notificationService.markAsRead(notificationId);
        return ResponseDto.success();
    }

    @DeleteMapping("/teachers/notifications/{notificationId}")
    public ResponseDto deleteNotification(@PathVariable Long notificationId) {
        notificationService.deleteById(notificationId);
        return ResponseDto.success();
    }
}
