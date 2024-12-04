package com.ormee.server.service;

import com.ormee.server.config.CodeGenerator;
import com.ormee.server.dto.LectureListDto;
import com.ormee.server.dto.LectureRequestDto;
import com.ormee.server.dto.LectureResponseDto;
import com.ormee.server.dto.QuizListDto;
import com.ormee.server.model.Lecture;
import com.ormee.server.model.Quiz;
import com.ormee.server.model.Teacher;
import com.ormee.server.repository.LectureRepository;
import com.ormee.server.repository.MemoRepository;
import com.ormee.server.repository.QuizRepository;
import com.ormee.server.repository.TeacherRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service

public class LectureService {

    private final LectureRepository lectureRepository;
    private final TeacherRepository teacherRepository;
    private final CodeGenerator codeGenerator;
    private final QuizService quizService;
    private final MemoRepository memoRepository;
    private final QuizRepository quizRepository;

    public LectureService(LectureRepository lectureRepository, TeacherRepository teacherRepository, CodeGenerator codeGenerator, QuizService quizService, MemoRepository memoRepository, QuizRepository quizRepository) {
        this.lectureRepository = lectureRepository;
        this.teacherRepository = teacherRepository;
        this.codeGenerator = codeGenerator;
        this.quizService = quizService;
        this.memoRepository = memoRepository;
        this.quizRepository = quizRepository;
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

    public LectureListDto findAllLectures(Integer teacherCode) {
        Teacher teacher = teacherRepository.findByCode(teacherCode).orElseThrow();
        List<Lecture> lectures = lectureRepository.findAllByTeacher(teacher);
        List<LectureResponseDto> openLectures = new ArrayList<>();
        List<LectureResponseDto> closedLectures = new ArrayList<>();

        LocalDateTime now = LocalDateTime.now();
        for(Lecture lecture : lectures) {
            if(lecture.getDueTime().isAfter(now)) {
                openLectures.add(lectureToDto(lecture));
            } else {
                closedLectures.add(lectureToDto(lecture));
            }
        }

        return LectureListDto.builder()
                .openLectures(openLectures)
                .closedLectures(closedLectures)
                .build();
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
                .openTime(lecture.getOpenTime())
                .dueTime(lecture.getDueTime())
                .quizList(quizListDtos)
                .activeQuizCount(count)
                .messageAvailable(memoRepository.existsByLectureAndIsOpen(lecture, true))
                .build();
    }

    public void close(Integer code) {
        Lecture lecture = lectureRepository.findByCode(code).orElseThrow(()-> new IllegalArgumentException("Lecture not found: " + code));
        LocalDateTime now = LocalDateTime.now();
        lecture.setDueTime(now);
        lectureRepository.save(lecture);
    }

    public void delete(Integer code) {
        Lecture lecture = lectureRepository.findByCode(code).orElseThrow(()-> new IllegalArgumentException("Lecture not found: " + code));

        List<Quiz> quizzes = quizRepository.findAllByLecture(lecture);
        for(Quiz quiz : quizzes) {
            quizService.deleteQuiz(quiz.getId());
        }

        lectureRepository.delete(lecture);
    }
}
