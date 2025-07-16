package com.ormee.server.notification.repository;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SseEmitterRepository {

    private final Map<Long, Set<SseEmitter>> emitters = new ConcurrentHashMap<>();

    public void addEmitter(Long lectureId, SseEmitter emitter) {
        emitters.computeIfAbsent(lectureId, id -> ConcurrentHashMap.newKeySet()).add(emitter);
    }

    public void removeEmitter(Long lectureId, SseEmitter emitter) {
        Set<SseEmitter> set = emitters.get(lectureId);
        if (set != null) {
            set.remove(emitter);
        }
    }

    public void sendToLecture(Long lectureId, Object data) {
        Set<SseEmitter> set = emitters.getOrDefault(lectureId, Set.of());
        for (SseEmitter emitter : set) {
            try {
                emitter.send(SseEmitter.event().name("new_memo").data(data));
            } catch (IOException e) {
                emitter.complete();
            }
        }
    }
}

