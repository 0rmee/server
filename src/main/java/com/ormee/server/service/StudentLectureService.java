package com.ormee.server.service;

import com.ormee.server.dto.student_lecture.StudentDetailDto;
import com.ormee.server.dto.student_lecture.StudentDescriptionRequestDto;
import com.ormee.server.exception.CustomException;
import com.ormee.server.exception.ExceptionType;
import com.ormee.server.model.Lecture;
import com.ormee.server.model.Student;
import com.ormee.server.model.StudentLecture;
import com.ormee.server.repository.LectureRepository;
import com.ormee.server.repository.StudentLectureRepository;
import com.ormee.server.repository.StudentRepository;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class StudentLectureService {
    private final StudentLectureRepository studentLectureRepository;
    private final StudentRepository studentRepository;
    private final LectureRepository lectureRepository;

    public StudentLectureService(StudentLectureRepository studentLectureRepository, StudentRepository studentRepository, LectureRepository lectureRepository) {
        this.studentLectureRepository = studentLectureRepository;
        this.studentRepository = studentRepository;
        this.lectureRepository = lectureRepository;
    }

    public void in(String email, UUID lectureId) {
        Student student = studentRepository.findByEmail(email).orElseThrow(() -> new CustomException(ExceptionType.STUDENT_NOT_FOUND_EXCEPTION));
        Lecture lecture = lectureRepository.findById(lectureId).orElseThrow(() -> new CustomException(ExceptionType.LECTURE_NOT_FOUND_EXCEPTION));

        StudentLecture studentLecture = StudentLecture.builder()
                .student(student)
                .lecture(lecture)
                .build();

        studentLectureRepository.save(studentLecture);
    }

    public void out(String email, UUID lectureId) {
        Student student = studentRepository.findByEmail(email).orElseThrow(() -> new CustomException(ExceptionType.STUDENT_NOT_FOUND_EXCEPTION));
        Lecture lecture = lectureRepository.findById(lectureId).orElseThrow(() -> new CustomException(ExceptionType.LECTURE_NOT_FOUND_EXCEPTION));

        StudentLecture studentLecture = studentLectureRepository.findByStudentAndLecture(student, lecture).orElseThrow(() -> new CustomException(ExceptionType.STUDENT_LECTURE_NOT_FOUND_EXCEPTION));

        studentLectureRepository.delete(studentLecture);
    }

    public List<StudentDetailDto> getStudentList(UUID lectureId) {
        Lecture lecture = lectureRepository.findById(lectureId).orElseThrow(() -> new CustomException(ExceptionType.LECTURE_NOT_FOUND_EXCEPTION));
        List<StudentLecture> studentLectures = studentLectureRepository.findAllByLecture(lecture);

        List<StudentDetailDto> studentDetailDtos = studentLectures.stream()
                .map(studentLecture -> StudentDetailDto.builder()
                        .name(studentLecture.getStudent().getName())
                        .description(studentLecture.getDescription())
                        .build())
                .sorted(Comparator.comparing(StudentDetailDto::getName))
                .collect(Collectors.toList());

        return studentDetailDtos;
    }

    public void describe(UUID lectureId, StudentDescriptionRequestDto studentDescriptionRequestDto) {
        Student student = studentRepository.findByEmail(studentDescriptionRequestDto.getEmail()).orElseThrow(() -> new CustomException(ExceptionType.STUDENT_NOT_FOUND_EXCEPTION));
        Lecture lecture = lectureRepository.findById(lectureId).orElseThrow(() -> new CustomException(ExceptionType.LECTURE_NOT_FOUND_EXCEPTION));

        StudentLecture studentLecture = studentLectureRepository.findByStudentAndLecture(student, lecture).orElseThrow(() -> new CustomException(ExceptionType.STUDENT_LECTURE_NOT_FOUND_EXCEPTION));
        studentLecture.setDescription(studentDescriptionRequestDto.getDescription());

        studentLectureRepository.save(studentLecture);
    }
}
