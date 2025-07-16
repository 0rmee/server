package com.ormee.server.lecture.service;

import com.ormee.server.attachment.domain.Attachment;
import com.ormee.server.global.response.PageResponseDto;
import com.ormee.server.lecture.domain.Lecture;
import com.ormee.server.lecture.domain.StudentLecture;
import com.ormee.server.lecture.dto.LectureListDto;
import com.ormee.server.lecture.dto.LectureResponseDto;
import com.ormee.server.lecture.dto.StudentDetailDto;
import com.ormee.server.lecture.dto.StudentDescriptionRequestDto;
import com.ormee.server.global.exception.CustomException;
import com.ormee.server.global.exception.ExceptionType;
import com.ormee.server.lecture.repository.LectureRepository;
import com.ormee.server.member.domain.Member;
import com.ormee.server.member.dto.AuthorDto;
import com.ormee.server.member.repository.MemberRepository;
import com.ormee.server.lecture.repository.StudentLectureRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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

        List<StudentLecture> studentLectures = studentLectureRepository.findByStudentAndLecture(student, lecture);

        studentLectures.forEach(studentLecture -> delete(studentLecture.getId()));
    }

    public PageResponseDto<StudentDetailDto> findAllStudents(Long lectureId, String filter, int page) {
        Lecture lecture = lectureRepository.findById(lectureId).orElseThrow(() -> new CustomException(ExceptionType.LECTURE_NOT_FOUND_EXCEPTION));

        Pageable pageable = PageRequest.of(page, 15, Sort.by("createdAt").descending());

        Page<StudentLecture> studentLecturePage = switch (filter) {
            case "이름순" -> studentLectureRepository.findAllByLectureAndBlockedFalseOrderByStudent_Name(lecture, pageable);
            case "최신순" -> studentLectureRepository.findAllByLectureAndBlockedFalseOrderByCreatedAtDesc(lecture, pageable);
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

    public void block(Long studentLectureId, boolean blocked) {
        StudentLecture studentLecture = studentLectureRepository.findById(studentLectureId).orElseThrow(() -> new CustomException(ExceptionType.STUDENT_LECTURE_NOT_FOUND_EXCEPTION));
        studentLecture.setBlocked(blocked);
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

    public List<StudentDetailDto> findBlockedStudentsByLecture(Long lectureId) {
        Lecture lecture = lectureRepository.findById(lectureId).orElseThrow(() -> new CustomException(ExceptionType.LECTURE_NOT_FOUND_EXCEPTION));
        List<StudentLecture> studentLectures = studentLectureRepository.findAllByLectureAndBlockedTrueOrderByStudent_Name(lecture);

        return studentLectures.stream()
                .map(studentLecture -> StudentDetailDto.builder()
                        .name(studentLecture.getStudent().getName() + studentLecture.getStudent().getPhoneNumber().substring(studentLecture.getStudent().getPhoneNumber().length() - 4))
                        .build())
                .toList();
    }

    public List<LectureResponseDto> getInLectures(String username) {
        Member student = memberRepository.findByUsername(username).orElseThrow(() -> new CustomException(ExceptionType.MEMBER_NOT_FOUND_EXCEPTION));
        LocalDateTime now = LocalDateTime.now();
        List<StudentLecture> studentLectures = studentLectureRepository.findAllByStudentAndLecture_StartDateBeforeAndLecture_DueDateAfter(student, now, now);

        List<Lecture> lectures = studentLectures.stream()
                .map(StudentLecture::getLecture)
                .toList();

        return lectures.stream()
                .map(lecture -> LectureResponseDto.builder()
                        .id(lecture.getId())
                        .title(lecture.getTitle())
                        .description(lecture.getDescription())
                        .name(lecture.getTeacher().getNickname())
                        .profileImage(
                                Optional.ofNullable(lecture.getTeacher().getImage())
                                        .map(Attachment::getFilePath)
                                        .orElse(null)
                        )
                        .coTeachers(lecture.getCollaborators().stream()
                                .map(member -> AuthorDto.builder()
                                        .name(member.getNickname())
                                        .image(
                                                Optional.ofNullable(member.getImage())
                                                        .map(Attachment::getFilePath)
                                                        .orElse(null)
                                        )
                                        .build())
                                .toList())
                        .startDate(lecture.getStartDate())
                        .dueDate(lecture.getDueDate())
                        .build())
                .toList();
    }

    public LectureListDto getMyLectures(String username) {
        Member student = memberRepository.findByUsername(username).orElseThrow(() -> new CustomException(ExceptionType.MEMBER_NOT_FOUND_EXCEPTION));
        LocalDateTime now = LocalDateTime.now();
        List<StudentLecture> studentLectures = studentLectureRepository.findAllByStudentOrderByLecture_StartDateDesc(student);

        return LectureListDto.builder()
                .openLectures(studentLectures.stream()
                        .filter(studentLecture -> !studentLecture.getLecture().getDueDate().isBefore(now))
                        .map(studentLecture -> entityToDto(studentLecture.getLecture()))
                        .toList())
                .closedLectures(studentLectures.stream()
                        .filter(studentLecture -> studentLecture.getLecture().getDueDate().isBefore(now))
                        .map(studentLecture -> entityToDto(studentLecture.getLecture()))
                        .toList())
                .build();
    }

    public LectureResponseDto entityToDto(Lecture lecture) {
        return LectureResponseDto.builder()
                .id(lecture.getId())
                .title(lecture.getTitle())
                .startDate(lecture.getStartDate())
                .dueDate(lecture.getDueDate())
                .startTime(lecture.getStartTime())
                .endTime(lecture.getEndTime())
                .lectureDays(lecture.getLectureDays())
                .build();
    }
}
