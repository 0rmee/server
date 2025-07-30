package com.ormee.server.notification.controller;

import com.ormee.server.global.response.ResponseDto;
import com.ormee.server.notification.dto.NotificationSettingDto;
import com.ormee.server.notification.service.NotificationSettingService;
import com.ormee.server.notification.service.StudentNotificationService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/students/notifications")
public class StudentNotificationController {
    private final StudentNotificationService studentNotificationService;
    private final NotificationSettingService settingService;

    public StudentNotificationController(StudentNotificationService studentNotificationService, NotificationSettingService settingService) {
        this.studentNotificationService = studentNotificationService;
        this.settingService = settingService;
    }

    @GetMapping
    public ResponseDto getNotifications(@RequestParam(defaultValue = "과제") String type, Authentication authentication) {
        return ResponseDto.success(studentNotificationService.getByType(type, authentication.getName()));
    }

    @GetMapping("/count")
    public ResponseDto getUnreadNotificationCount(Authentication authentication) {
        return ResponseDto.success(studentNotificationService.getCount(authentication.getName()));
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

    @GetMapping("/settings")
    public ResponseDto getNotificationSettings(Authentication authentication) {
        return ResponseDto.success(settingService.getSetting(authentication.getName()));
    }

    @PutMapping("/settings")
    public ResponseDto updateNotificationSettings(Authentication authentication, @RequestBody NotificationSettingDto settingDto) {
        settingService.updateSetting(authentication.getName(), settingDto);
        return ResponseDto.success();
    }
}
