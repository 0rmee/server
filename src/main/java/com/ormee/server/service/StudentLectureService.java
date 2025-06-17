package com.ormee.server.service;

import com.ormee.server.dto.response.PageResponseDto;
import com.ormee.server.dto.student_lecture.StudentDetailDto;
import com.ormee.server.dto.student_lecture.StudentDescriptionRequestDto;
import com.ormee.server.exception.CustomException;
import com.ormee.server.exception.ExceptionType;
import com.ormee.server.model.Lecture;
import com.ormee.server.model.StudentLecture;
import com.ormee.server.model.member.Member;
import com.ormee.server.repository.LectureRepository;
import com.ormee.server.repository.MemberRepository;
import com.ormee.server.repository.StudentLectureRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentLectureService {
    private final StudentLectureRepository studentLectureRepository;
    private final MemberRepository memberRepository;
    private final LectureRepository lectureRepository;

    public StudentLectureService(StudentLectureRepository studentLectureRepository, MemberRepository memberRepository, LectureRepository lectureRepository) {
        this.studentLectureRepository = studentLectureRepository;
        this.memberRepository = memberRepository;
        this.lectureRepository = lectureRepository;
    }

    public void in(Long lectureId, String username) {
        Member student = memberRepository.findByUsername(username).orElseThrow(() -> new CustomException(ExceptionType.MEMBER_NOT_FOUND_EXCEPTION));
        Lecture lecture = lectureRepository.findById(lectureId).orElseThrow(() -> new CustomException(ExceptionType.LECTURE_NOT_FOUND_EXCEPTION));

        StudentLecture studentLecture = StudentLecture.builder()
                .student(student)
                .lecture(lecture)
                .build();

        StudentLecture savedStudentLecture = studentLectureRepository.save(studentLecture);

        lecture.addStudentLecture(savedStudentLecture);
        student.addStudentLecture(savedStudentLecture);
        lectureRepository.save(lecture);
        memberRepository.save(student);
    }

    public void out(Long lectureId, String username) {
        Member student = memberRepository.findByUsername(username).orElseThrow(() -> new CustomException(ExceptionType.MEMBER_NOT_FOUND_EXCEPTION));
        Lecture lecture = lectureRepository.findById(lectureId).orElseThrow(() -> new CustomException(ExceptionType.LECTURE_NOT_FOUND_EXCEPTION));

        StudentLecture studentLecture = studentLectureRepository.findByStudentAndLecture(student, lecture).orElseThrow(() -> new CustomException(ExceptionType.STUDENT_LECTURE_NOT_FOUND_EXCEPTION));

        delete(studentLecture.getId());
    }

    public PageResponseDto<StudentDetailDto> findAllStudents(Long lectureId, String filter, int page) {
        Lecture lecture = lectureRepository.findById(lectureId).orElseThrow(() -> new CustomException(ExceptionType.LECTURE_NOT_FOUND_EXCEPTION));

        Pageable pageable = PageRequest.of(page, 15, Sort.by("createdAt").descending());

        Page<StudentLecture> studentLecturePage = switch (filter) {
            case "이름순" -> studentLectureRepository.findAllByLectureOrderByStudent_Name(lecture, pageable);
            case "최신순" -> studentLectureRepository.findAllByLectureOrderByCreatedAtDesc(lecture, pageable);
            default -> throw new CustomException(ExceptionType.FILTER_INVALID_EXCEPTION);
        };

        List<StudentDetailDto> content = studentLecturePage.stream()
                .map(studentLecture -> StudentDetailDto.builder()
                        .id(studentLecture.getId())
                        .enrolDate(studentLecture.getCreatedAt().toLocalDate())
                        .name(studentLecture.getStudent().getName() + studentLecture.getStudent().getPhoneNumber().substring(studentLecture.getStudent().getPhoneNumber().length() - 4))
                        .description(studentLecture.getDescription())
                        .build())
                .toList();

        return PageResponseDto.<StudentDetailDto>builder()
                .content(content)
                .totalPages(studentLecturePage.getTotalPages())
                .totalElements(studentLecturePage.getTotalElements())
                .currentPage(studentLecturePage.getNumber() + 1)
                .build();
    }

    public void updateDescription(StudentDescriptionRequestDto studentDescriptionRequestDto) {
        StudentLecture studentLecture = studentLectureRepository.findById(studentDescriptionRequestDto.getStudentLectureId()).orElseThrow(() -> new CustomException(ExceptionType.STUDENT_LECTURE_NOT_FOUND_EXCEPTION));
        studentLecture.setDescription(studentDescriptionRequestDto.getDescription());

        studentLectureRepository.save(studentLecture);
    }

    public void delete(Long studentLectureId) {
        StudentLecture studentLecture = studentLectureRepository.findById(studentLectureId).orElseThrow(() -> new CustomException(ExceptionType.STUDENT_LECTURE_NOT_FOUND_EXCEPTION));
        Lecture lecture = studentLecture.getLecture();
        Member student = studentLecture.getStudent();

        student.getStudentLectures().remove(studentLecture);
        lecture.getStudentLectures().remove(studentLecture);

        studentLectureRepository.delete(studentLecture);
    }
}
