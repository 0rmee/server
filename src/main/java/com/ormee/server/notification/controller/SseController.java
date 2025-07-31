package com.ormee.server.notification.controller;

import com.ormee.server.memo.service.MemoService;
import com.ormee.server.notification.repository.SseEmitterRepository;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

@RestController
public class SseController {
    private final SseEmitterRepository sseEmitterRepository;
    private final MemoService memoService;

    public SseController(SseEmitterRepository sseEmitterRepository, MemoService memoService) {
        this.sseEmitterRepository = sseEmitterRepository;
        this.memoService = memoService;
    }

    @GetMapping("/subscribe/lectures/{lectureId}/memos")
    public SseEmitter subscribe(@PathVariable Long lectureId) {
        SseEmitter emitter = new SseEmitter(60L * 60 * 1000);
        sseEmitterRepository.addEmitter(lectureId, emitter);

        emitter.onCompletion(() -> sseEmitterRepository.removeEmitter(lectureId, emitter));
        emitter.onTimeout(() -> sseEmitterRepository.removeEmitter(lectureId, emitter));
        emitter.onError(e -> sseEmitterRepository.removeEmitter(lectureId, emitter));

        try {
            Long memoId = memoService.getOpenMemo(lectureId);
            emitter.send(SseEmitter.event().name("connect").data(memoId));
        } catch (IOException e) {
            emitter.complete();
            sseEmitterRepository.removeEmitter(lectureId, emitter);
        }

        return emitter;
    }
}
