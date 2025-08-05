package com.ormee.server.lecture.service;

import com.ormee.server.attachment.domain.Attachment;
import com.ormee.server.homework.service.HomeworkService;
import com.ormee.server.lecture.domain.Lecture;
import com.ormee.server.lecture.domain.LectureDay;
import com.ormee.server.lecture.dto.LectureListDto;
import com.ormee.server.lecture.dto.LectureRequestDto;
import com.ormee.server.lecture.dto.LectureResponseDto;
import com.ormee.server.lecture.repository.LectureRepository;
import com.ormee.server.member.domain.Role;
import com.ormee.server.member.dto.AuthorDto;
import com.ormee.server.memo.service.MemoService;
import com.ormee.server.notice.service.NoticeService;
import com.ormee.server.notification.service.NotificationService;
import com.ormee.server.question.service.QuestionService;
import com.ormee.server.quiz.dto.QuizListDto;
import com.ormee.server.global.exception.CustomException;
import com.ormee.server.global.exception.ExceptionType;
import com.ormee.server.member.domain.Member;
import com.ormee.server.member.repository.MemberRepository;
import com.ormee.server.memo.repository.MemoRepository;
import com.ormee.server.quiz.service.QuizService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service

public class LectureService {

    private final LectureRepository lectureRepository;
    private final MemberRepository memberRepository;
    private final QuizService quizService;
    private final MemoService memoService;
    private final HomeworkService homeworkService;
    private final QuestionService questionService;
    private final NoticeService noticeService;
    private final NotificationService notificationService;
    private final MemoRepository memoRepository;

    public LectureService(LectureRepository lectureRepository, MemberRepository memberRepository, QuizService quizService, MemoService memoService, HomeworkService homeworkService, QuestionService questionService, NoticeService noticeService, NotificationService notificationService, MemoRepository memoRepository) {
        this.lectureRepository = lectureRepository;
        this.memberRepository = memberRepository;
        this.quizService = quizService;
        this.memoService = memoService;
        this.homeworkService = homeworkService;
        this.questionService = questionService;
        this.noticeService = noticeService;
        this.notificationService = notificationService;
        this.memoRepository = memoRepository;
    }

    public LectureResponseDto save(LectureRequestDto lectureRequestDto, String username) {
        Member teacher = memberRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException(ExceptionType.MEMBER_NOT_FOUND_EXCEPTION));

        Lecture lecture = Lecture.builder()
                .teacher(teacher)
                .title(lectureRequestDto.getTitle())
                .description(lectureRequestDto.getDescription())
                .lectureDays(new ArrayList<>(lectureRequestDto.getLectureDays()
                        .stream()
                        .map(LectureDay::fromKorean)
                        .toList()))
                .startTime(lectureRequestDto.getStartTime())
                .endTime(lectureRequestDto.getEndTime())
                .startDate(lectureRequestDto.getStartDate())
                .dueDate(lectureRequestDto.getDueDate())
                .build();

        Lecture savedLecture = lectureRepository.save(lecture);
        teacher.addLecture(savedLecture);
        addCollaborator(savedLecture.getId(), lectureRequestDto.getCollaborator());

        return LectureResponseDto.builder()
                .id(savedLecture.getId())
                .title(savedLecture.getTitle())
                .build();
    }

    public void update(LectureRequestDto lectureRequestDto, Long lectureId, String username) {
        Lecture lecture = lectureRepository.findById(lectureId).orElseThrow(() -> new CustomException(ExceptionType.LECTURE_NOT_FOUND_EXCEPTION));
        Member teacher = memberRepository.findByUsername(username).orElseThrow(() -> new CustomException(ExceptionType.MEMBER_NOT_FOUND_EXCEPTION));

        checkAuth(lecture, teacher);

        isModifiable(lecture);

        if (lectureRequestDto.getTitle() != null) {
            lecture.setTitle(lectureRequestDto.getTitle());
        }
        if (lectureRequestDto.getDescription() != null) {
            lecture.setDescription(lectureRequestDto.getDescription());
        }
        if (lectureRequestDto.getLectureDays() != null) {
            lecture.setLectureDays(
                    lectureRequestDto.getLectureDays().stream()
                            .map(String::toUpperCase)
                            .map(LectureDay::valueOf)
                            .toList()
            );
        }
        if (lectureRequestDto.getStartTime() != null) {
            lecture.setStartTime(lectureRequestDto.getStartTime());
        }
        if (lectureRequestDto.getEndTime() != null) {
            lecture.setEndTime(lectureRequestDto.getEndTime());
        }
        if (lectureRequestDto.getStartDate() != null) {
            lecture.setStartDate(lectureRequestDto.getStartDate());
        }
        if (lectureRequestDto.getDueDate() != null) {
            lecture.setDueDate(lectureRequestDto.getDueDate());
        }

        lectureRepository.save(lecture);
    }

    private void checkAuth(Lecture lecture, Member teacher) {
        Long teacherId = teacher.getId();
        boolean isOwner = teacherId.equals(lecture.getTeacher().getId());
        boolean isCollaborator = lecture.getCollaborators().stream()
                .anyMatch(c -> teacherId.equals(c.getId()));

        if (!isOwner && !isCollaborator) {
            throw new CustomException(ExceptionType.ACCESS_FORBIDDEN_EXCEPTION);
        }
    }

    private void isModifiable(Lecture lecture) {
        if (lecture.getStartDate().toLocalDate().isBefore(LocalDate.now()))
            throw new CustomException(ExceptionType.LECTURE_MODIFY_FORBIDDEN_EXCEPTION);
    }

    @Transactional
    public void delete(Long lectureId, String username) {
        Lecture lecture = lectureRepository.findById(lectureId).orElseThrow(() -> new CustomException(ExceptionType.LECTURE_NOT_FOUND_EXCEPTION));
        Member teacher = memberRepository.findByUsername(username).orElseThrow(() -> new CustomException(ExceptionType.MEMBER_NOT_FOUND_EXCEPTION));

        checkAuth(lecture, teacher);

        isModifiable(lecture);

        quizService.deleteByLecture(lecture);
        memoService.deleteByLecture(lecture);
        homeworkService.deleteByLecture(lecture);
        questionService.deleteByLecture(lecture);
        noticeService.deleteByLecture(lecture);
        notificationService.deleteAll(lectureId);

        teacher.removeLecture(lecture);
        memberRepository.save(teacher);
    }

    public List<LectureResponseDto> getAllLectures(String username) {
        Member teacher = memberRepository.findByUsername(username).orElseThrow(() -> new CustomException(ExceptionType.MEMBER_NOT_FOUND_EXCEPTION));

        List<Lecture> lectures = lectureRepository.findAllByTeacherOrCollaboratorsInOrderByCreatedAtDesc(teacher, List.of(teacher));

        return lectures.stream().map(lecture -> LectureResponseDto.builder()
                        .id(lecture.getId())
                        .name(lecture.getTitle())
                        .build())
                .toList();
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
                .id(lecture.getId())
                .profileImage(teacher.getImage() == null ? null : teacher.getImage().getFilePath())
                .name(teacher.getNickname())
                .coTeachers(lecture.getCollaborators().stream().map(member -> AuthorDto.builder()
                        .name(member.getNickname())
                        .image(Optional.ofNullable(member.getImage())
                                .map(Attachment::getFilePath)
                                .orElse(null))
                        .build()).toList())
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

        if (lecture.getCollaborators() != null && !lecture.getCollaborators().isEmpty())
            throw new CustomException(ExceptionType.COLLABORATOR_ADD_FORBIDDEN_EXCEPTION);

        if (lecture.getCollaboratorChangeCount() != null && lecture.getCollaboratorChangeCount() > 1) {
            throw new CustomException(ExceptionType.COLLABORATOR_CHANGE_FORBIDDEN_EXCEPTION);
        }

        Member collaborator = memberRepository.findByUsername(username).orElseThrow(() -> new CustomException(ExceptionType.MEMBER_NOT_FOUND_EXCEPTION));
        lecture.addCollaborator(collaborator);
        lectureRepository.save(lecture);
    }

    @Transactional
    public void removeCollaborator(Long lectureId, String username) {
        Lecture lecture = lectureRepository.findById(lectureId)
                .orElseThrow(() -> new CustomException(ExceptionType.LECTURE_NOT_FOUND_EXCEPTION));
        Member collaborator = memberRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException(ExceptionType.MEMBER_NOT_FOUND_EXCEPTION));
        lecture.removeCollaborator(collaborator);
    }

    public LectureResponseDto getLecture(Long lectureId) {
        Lecture lecture = lectureRepository.findById(lectureId).orElseThrow(() -> new CustomException(ExceptionType.LECTURE_NOT_FOUND_EXCEPTION));

        return LectureResponseDto.builder()
                .id(lecture.getId())
                .profileImage(lecture.getTeacher().getImage() == null ? null : lecture.getTeacher().getImage().getFilePath())
                .name(lecture.getTeacher().getNickname())
                .coTeachers(lecture.getCollaborators().stream().map(member -> AuthorDto.builder()
                        .name(member.getNickname())
                        .image(Optional.ofNullable(member.getImage())
                                .map(Attachment::getFilePath)
                                .orElse(null))
                        .build()).toList())
                .title(lecture.getTitle())
                .description(lecture.getDescription())
                .lectureDays(lecture.getLectureDays())
                .startTime(lecture.getStartTime())
                .endTime(lecture.getEndTime())
                .startDate(lecture.getStartDate())
                .dueDate(lecture.getDueDate())
                .messageAvailable(memoRepository.existsByLectureAndIsOpen(lecture, true))
                .build();
    }

    public LectureResponseDto getLecture(Long lectureId, String username) {
        Lecture lecture = lectureRepository.findById(lectureId).orElseThrow(() -> new CustomException(ExceptionType.LECTURE_NOT_FOUND_EXCEPTION));
        Member teacher = memberRepository.findByUsernameAndRole(username, Role.TEACHER).orElseThrow(() -> new CustomException(ExceptionType.MEMBER_NOT_FOUND_EXCEPTION));

        return LectureResponseDto.builder()
                .id(lecture.getId())
                .coTeacher(Optional.ofNullable(lecture.getCollaborators())
                        .filter(list -> !list.isEmpty())
                        .map(list -> list.get(0).getUsername())
                        .orElse(null))
                .title(lecture.getTitle())
                .description(lecture.getDescription())
                .lectureDays(lecture.getLectureDays())
                .startTime(lecture.getStartTime())
                .endTime(lecture.getEndTime())
                .startDate(lecture.getStartDate())
                .dueDate(lecture.getDueDate())
                .isOwner(Objects.equals(lecture.getTeacher().getId(), teacher.getId()))
                .build();
    }
}
