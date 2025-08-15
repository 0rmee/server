package com.ormee.server.notification.service;

import com.google.firebase.ErrorCode;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.ormee.server.notification.domain.StudentNotification;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class FcmService {

    public void sendMessageTo(String targetToken, StudentNotification studentNotification) {
        Notification notification = Notification.builder()
                .setTitle(studentNotification.getHeader())
                .setBody(studentNotification.getBody())
                .build();

        Map<String, String> data = new HashMap<>();
        data.put("type", studentNotification.getType().getKorean());
        data.put("id", String.valueOf(studentNotification.getParentId()));

        Message message = Message.builder()
                .setToken(targetToken)
                .setNotification(notification)
                .putAllData(data)
                .build();

        try {
            String response = FirebaseMessaging.getInstance().send(message);
            System.out.println("FCM 응답: " + response);
        } catch (FirebaseMessagingException e) {
            if ("UNREGISTERED".equals(e.getErrorCode().toString())) {
                System.err.println("UNREGISTERED: 유효하지 않은 토큰입니다. 토큰: " + targetToken + " 에러 메시지: " + e.getMessage());
            } else {
                System.err.println("FCM 메시지 전송 실패: " + e.getMessage());
            }
        }
    }
}