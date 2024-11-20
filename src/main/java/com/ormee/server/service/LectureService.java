package com.ormee.server.service;

import com.ormee.server.config.CodeGenerator;
import com.ormee.server.dto.LectureRequestDto;
import com.ormee.server.dto.LectureResponseDto;
import com.ormee.server.dto.QuizDto;
import com.ormee.server.model.Lecture;
import com.ormee.server.model.Teacher;
import com.ormee.server.repository.LectureRepository;
import com.ormee.server.repository.TeacherRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service

public class LectureService {

    private final LectureRepository lectureRepository;
    private final TeacherRepository teacherRepository;
    private final CodeGenerator codeGenerator;

    public LectureService(LectureRepository lectureRepository, TeacherRepository teacherRepository, CodeGenerator codeGenerator) {
        this.lectureRepository = lectureRepository;
        this.teacherRepository = teacherRepository;
        this.codeGenerator = codeGenerator;
    }

    public Lecture saveLecture(LectureRequestDto lectureRequestDto, Integer teacherCode) {
        Teacher teacher = teacherRepository.findByCode(teacherCode).orElse(null);
        Integer lectureCode = codeGenerator.generateCode();

        while(lectureRepository.existsByCode(lectureCode)) {
            lectureCode = codeGenerator.generateCode();
        }

        Lecture lecture = Lecture.builder()
                .teacher(teacher)
                .code(lectureCode)
                .title(lectureRequestDto.getTitle())
                .description(lectureRequestDto.getDescription())
                .lectureDays(lectureRequestDto.getLectureDays())
                .startTime(lectureRequestDto.getStartTime())
                .endTime(lectureRequestDto.getEndTime())
                .dueTime(lectureRequestDto.getDueTime())
                .build();

        return lectureRepository.save(lecture);
    }

    public LectureResponseDto findLectureByCode(Integer code) {
        Lecture lecture = lectureRepository.findByCode(code).orElse(null);
        Teacher teacher = lecture.getTeacher();

        if(lecture == null) {
            return null;
        } else {
            return LectureResponseDto.builder()
                    .id(lecture.getId().toString())
                    .profileImage(teacher.getImage())
                    .name(teacher.getName())
                    .title(lecture.getTitle())
                    .description(lecture.getDescription())
                    .lectureDays(lecture.getLectureDays())
                    .startTime(lecture.getStartTime())
                    .endTime(lecture.getEndTime())
                    .dueTime(lecture.getDueTime())
                    .quizList(new ArrayList<QuizDto>())
                    .activeQuizCount(0)
                    .messageAvailable(false)
                    .build();
        }
    }
}
