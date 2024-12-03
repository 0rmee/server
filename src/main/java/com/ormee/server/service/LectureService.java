package com.ormee.server.service;

import com.ormee.server.config.CodeGenerator;
import com.ormee.server.dto.LectureRequestDto;
import com.ormee.server.dto.LectureResponseDto;
import com.ormee.server.dto.QuizListDto;
import com.ormee.server.model.Lecture;
import com.ormee.server.model.Teacher;
import com.ormee.server.repository.LectureRepository;
import com.ormee.server.repository.MemoRepository;
import com.ormee.server.repository.TeacherRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service

public class LectureService {

    private final LectureRepository lectureRepository;
    private final TeacherRepository teacherRepository;
    private final CodeGenerator codeGenerator;
    private final QuizService quizService;
    private final MemoRepository memoRepository;

    public LectureService(LectureRepository lectureRepository, TeacherRepository teacherRepository, CodeGenerator codeGenerator, QuizService quizService, MemoRepository memoRepository) {
        this.lectureRepository = lectureRepository;
        this.teacherRepository = teacherRepository;
        this.codeGenerator = codeGenerator;
        this.quizService = quizService;
        this.memoRepository = memoRepository;
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
        Lecture lecture = lectureRepository.findByCode(code).orElseThrow(()-> new IllegalArgumentException("Lecture not found: " + code));
        return lectureToDto(lecture);
    }

    public List<LectureResponseDto> findAllLectures(Integer teacherCode) {
        Teacher teacher = teacherRepository.findByCode(teacherCode).orElseThrow();
        List<Lecture> lectures = lectureRepository.findAllByTeacher(teacher);
        List<LectureResponseDto> lectureResponseDtos = new ArrayList<>();

        for(Lecture lecture : lectures) {
            lectureResponseDtos.add(lectureToDto(lecture));
        }

        return lectureResponseDtos;
    }

    private LectureResponseDto lectureToDto(Lecture lecture) {
        Teacher teacher = lecture.getTeacher();
        List<QuizListDto> quizListDtos = quizService.findOpenQuizList(lecture.getId());
        long count = quizListDtos.stream()
                .filter(QuizListDto::getQuizAvailable)
                .count();

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
                .quizList(quizListDtos)
                .activeQuizCount(count)
                .messageAvailable(memoRepository.existsByLectureAndIsOpen(lecture, true))
                .build();
    }
}
