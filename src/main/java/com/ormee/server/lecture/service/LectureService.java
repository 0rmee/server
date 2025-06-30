package com.ormee.server.lecture.service;

import com.ormee.server.lecture.domain.Lecture;
import com.ormee.server.lecture.domain.LectureDay;
import com.ormee.server.lecture.dto.LectureListDto;
import com.ormee.server.lecture.dto.LectureRequestDto;
import com.ormee.server.lecture.dto.LectureResponseDto;
import com.ormee.server.lecture.repository.LectureRepository;
import com.ormee.server.quiz.dto.QuizListDto;
import com.ormee.server.global.exception.CustomException;
import com.ormee.server.global.exception.ExceptionType;
import com.ormee.server.member.domain.Member;
import com.ormee.server.member.repository.MemberRepository;
import com.ormee.server.memo.repository.MemoRepository;
import com.ormee.server.quiz.service.QuizService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service

public class LectureService {

    private final LectureRepository lectureRepository;
    private final MemberRepository memberRepository;
    private final QuizService quizService;
    private final MemoRepository memoRepository;

    public LectureService(LectureRepository lectureRepository, MemberRepository memberRepository, QuizService quizService, MemoRepository memoRepository) {
        this.lectureRepository = lectureRepository;
        this.memberRepository = memberRepository;
        this.quizService = quizService;
        this.memoRepository = memoRepository;
    }

    public void save(LectureRequestDto lectureRequestDto, String username) {
        Member teacher = memberRepository.findByUsername(username).orElseThrow(() -> new CustomException(ExceptionType.MEMBER_NOT_FOUND_EXCEPTION));

        Lecture lecture = Lecture.builder()
                .teacher(teacher)
                .title(lectureRequestDto.getTitle())
                .description(lectureRequestDto.getDescription())
                .lectureDays(
                        new ArrayList<>(
                                lectureRequestDto.getLectureDays()
                                        .stream()
                                        .map(LectureDay::fromKorean)
                                        .toList()
                        )
                )
                .startTime(lectureRequestDto.getStartTime())
                .endTime(lectureRequestDto.getEndTime())
                .startDate(lectureRequestDto.getStartDate())
                .dueDate(lectureRequestDto.getDueDate())
                .build();

        Lecture savedLecture = lectureRepository.save(lecture);
        teacher.addLecture(savedLecture);
        memberRepository.save(teacher);
    }

    public void update(LectureRequestDto lectureRequestDto, Long lectureId, String username) {
        Lecture lecture = lectureRepository.findById(lectureId).orElseThrow(() -> new CustomException(ExceptionType.LECTURE_NOT_FOUND_EXCEPTION));
        Member teacher = memberRepository.findByUsername(username).orElseThrow(() -> new CustomException(ExceptionType.MEMBER_NOT_FOUND_EXCEPTION));

        checkAuth(lecture, teacher);

        isModifiable(lecture);

        lecture.setTitle(lectureRequestDto.getTitle());
        lecture.setDescription(lectureRequestDto.getDescription());
        lecture.setLectureDays(lectureRequestDto.getLectureDays().stream().map(LectureDay::fromKorean).toList());
        lecture.setStartTime(lectureRequestDto.getStartTime());
        lecture.setEndTime(lectureRequestDto.getEndTime());
        lecture.setStartDate(lectureRequestDto.getStartDate());
        lecture.setDueDate(lectureRequestDto.getDueDate());

        lectureRepository.save(lecture);
    }

    private void checkAuth(Lecture lecture, Member teacher) {
        if(teacher != lecture.getTeacher() || !lecture.getCollaborators().contains(teacher)) {
            throw new CustomException(ExceptionType.ACCESS_FORBIDDEN_EXCEPTION);
        }
    }

    private void isModifiable(Lecture lecture) {
        if(lecture.getStartDate().toLocalDate().isBefore(LocalDate.now()))
            throw new CustomException(ExceptionType.LECTURE_MODIFY_FORBIDDEN_EXCEPTION);
    }

    public void delete(Long lectureId, String username) {
        Lecture lecture = lectureRepository.findById(lectureId).orElseThrow(() -> new CustomException(ExceptionType.LECTURE_NOT_FOUND_EXCEPTION));
        Member teacher = memberRepository.findByUsername(username).orElseThrow(() -> new CustomException(ExceptionType.MEMBER_NOT_FOUND_EXCEPTION));

        checkAuth(lecture, teacher);

        isModifiable(lecture);

        teacher.removeLecture(lecture);
        memberRepository.save(teacher);
    }

    public LectureListDto findAllLectures(String username) {
        Member teacher = memberRepository.findByUsername(username).orElseThrow(() -> new CustomException(ExceptionType.MEMBER_NOT_FOUND_EXCEPTION));

        LocalDateTime now = LocalDateTime.now();

        List<Lecture> lectures = lectureRepository.findAllByTeacherOrCollaboratorsIn(teacher, List.of(teacher));

        List<LectureResponseDto> openLectures = lectures.stream()
                .filter(lecture -> !lecture.getDueDate().isBefore(now))
                .map(this::lectureToDto)
                .toList();

        List<LectureResponseDto> closedLectures = lectures.stream()
                .filter(lecture -> lecture.getDueDate().isBefore(now))
                .map(this::lectureToDto)
                .toList();

        return LectureListDto.builder()
                .openLectures(openLectures)
                .closedLectures(closedLectures)
                .build();
    }

    private LectureResponseDto lectureToDto(Lecture lecture) {
        Member teacher = lecture.getTeacher();
        List<QuizListDto> quizListDtos = quizService.findOpenQuizList(lecture.getId());
        long count = quizListDtos.stream()
                .filter(QuizListDto::getQuizAvailable)
                .count();

        return LectureResponseDto.builder()
                .id(lecture.getId().toString())
                .profileImage(teacher.getImage() == null ? null : teacher.getImage().getFilePath())
                .name(teacher.getName())
                .title(lecture.getTitle())
                .description(lecture.getDescription())
                .lectureDays(lecture.getLectureDays())
                .startTime(lecture.getStartTime())
                .endTime(lecture.getEndTime())
                .startDate(lecture.getStartDate())
                .dueDate(lecture.getDueDate())
                .students(lecture.getStudentLectures().size())
                .quizList(quizListDtos)
                .activeQuizCount(count)
                .messageAvailable(memoRepository.existsByLectureAndIsOpen(lecture, true))
                .build();
    }

    public void addCollaborator(Long lectureId, String username) {
        Lecture lecture = lectureRepository.findById(lectureId).orElseThrow(() -> new CustomException(ExceptionType.LECTURE_NOT_FOUND_EXCEPTION));

        if(!lecture.getCollaborators().isEmpty())
            throw new CustomException(ExceptionType.COLLABORATOR_ADD_FORBIDDEN_EXCEPTION);

        if(lecture.getCollaboratorChangeCount() != null && lecture.getCollaboratorChangeCount() > 1) {
            throw new CustomException(ExceptionType.COLLABORATOR_CHANGE_FORBIDDEN_EXCEPTION);
        }

        Member collaborator = memberRepository.findByUsername(username).orElseThrow(() -> new CustomException(ExceptionType.MEMBER_NOT_FOUND_EXCEPTION));
        lecture.addCollaborator(collaborator);
        lectureRepository.save(lecture);
    }

    public void removeCollaborator(Long lectureId, String username) {
        Lecture lecture = lectureRepository.findById(lectureId).orElseThrow(() -> new CustomException(ExceptionType.LECTURE_NOT_FOUND_EXCEPTION));
        if(lecture.getCollaborators().isEmpty()) {
            throw new CustomException(ExceptionType.COLLABORATOR_NOT_FOUND_EXCEPTION);
        }

        Member collaborator = memberRepository.findByUsername(username).orElseThrow(() -> new CustomException(ExceptionType.MEMBER_NOT_FOUND_EXCEPTION));
        lecture.removeCollaborator(collaborator);
        lectureRepository.save(lecture);
    }
}
