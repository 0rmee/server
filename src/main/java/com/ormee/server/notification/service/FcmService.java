package com.ormee.server.notification.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.ormee.server.notification.domain.StudentNotification;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class FcmService {

    public void sendMessageTo(String targetToken, StudentNotification studentNotification) throws Exception {
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

        String response = FirebaseMessaging.getInstance().send(message);

        System.out.println("FCM 응답: " + response);
    }
}