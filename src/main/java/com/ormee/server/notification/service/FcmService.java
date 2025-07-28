package com.ormee.server.notification.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;
import com.ormee.server.notification.domain.StudentNotification;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
public class FcmService {

    @Value("${fcm.project-id}")
    private String projectId;

    private static final String MESSAGING_SCOPE =
            "https://www.googleapis.com/auth/firebase.messaging";

    private final ObjectMapper objectMapper = new ObjectMapper();

    private String getAccessToken() throws Exception {
        InputStream serviceAccount =
                getClass().getClassLoader().getResourceAsStream("serviceAccountKey.json");

        if (serviceAccount == null) {
            throw new FileNotFoundException("serviceAccountKey.json not found in classpath");
        }

        GoogleCredentials googleCredentials = GoogleCredentials.fromStream(serviceAccount)
                .createScoped(Collections.singletonList(MESSAGING_SCOPE));
        googleCredentials.refreshIfExpired();

        return googleCredentials.getAccessToken().getTokenValue();
    }

    public void sendMessageTo(String targetToken, StudentNotification studentNotification) throws Exception {
        String message = buildMessage(targetToken, studentNotification);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(getAccessToken());

        HttpEntity<String> entity = new HttpEntity<>(message, headers);

        RestTemplate restTemplate = new RestTemplate();
        String fcmApiUrl = "https://fcm.googleapis.com/v1/projects/"
                + projectId + "/messages:send";

        ResponseEntity<String> response = restTemplate.postForEntity(fcmApiUrl, entity, String.class);

        System.out.println("FCM 응답: " + response.getBody());
    }

    private String buildMessage(String targetToken, StudentNotification studentNotification) throws Exception {
        Map<String, Object> messageContent = new HashMap<>();

        Map<String, String> notification = new HashMap<>();
        notification.put("title", studentNotification.getHeader());
        notification.put("body", studentNotification.getBody());

        Map<String, String> data = new HashMap<>();
        data.put("type", studentNotification.getType().getKorean());
        data.put("id", String.valueOf(studentNotification.getParentId()));

        messageContent.put("notification", notification);
        messageContent.put("data", data);
        messageContent.put("token", targetToken);

        Map<String, Object> finalMessage = new HashMap<>();
        finalMessage.put("message", messageContent);

        return objectMapper.writeValueAsString(finalMessage);
    }
}
