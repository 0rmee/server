package com.ormee.server.service;

import com.ormee.server.config.CodeGenerator;
import com.ormee.server.dto.LectureDto;
import com.ormee.server.model.Lecture;
import com.ormee.server.model.Teacher;
import com.ormee.server.repository.LectureRepository;
import com.ormee.server.repository.TeacherRepository;
import org.springframework.stereotype.Service;

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

    public Lecture saveLecture(LectureDto lectureDto, Integer teacherCode) {
        Teacher teacher = teacherRepository.findByCode(teacherCode).orElse(null);
        Integer lectureCode = codeGenerator.generateCode();

        while(!lectureRepository.existsByCode(lectureCode)) {
            lectureCode = codeGenerator.generateCode();
        }

        Lecture lecture = Lecture.builder()
                .teacher(teacher)
                .code(lectureCode)
                .title(lectureDto.getTitle())
                .description(lectureDto.getDescription())
                .lectureDays(lectureDto.getLectureDays())
                .dueTime(lectureDto.getDueTime())
                .build();

        return lectureRepository.save(lecture);
    }

    public LectureDto findLectureByCode(Integer code) {
        Lecture lecture = lectureRepository.findByCode(code).orElse(null);

        if(lecture == null) {
            return null;
        } else {
            return LectureDto.builder()
                    .title(lecture.getTitle())
                    .description(lecture.getDescription())
                    .lectureDays(lecture.getLectureDays())
                    .lectureTime(lecture.getLectureTime())
                    .dueTime(lecture.getDueTime())
                    .build();
        }
    }
}
