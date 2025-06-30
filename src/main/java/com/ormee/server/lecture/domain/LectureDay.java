package com.ormee.server.lecture.domain;

import java.util.Arrays;

public enum LectureDay {
    MON("월"),
    TUE("화"),
    WED("수"),
    THU("목"),
    FRI("금"),
    SAT("토"),
    SUN("일");

    private final String korean;

    LectureDay(String korean) {
        this.korean = korean;
    }

    public static LectureDay fromKorean(String korean) {
        return Arrays.stream(values())
                .filter(day -> day.korean.equals(korean))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown day: " + korean));
    }
}