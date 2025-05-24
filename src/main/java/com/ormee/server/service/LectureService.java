package com.ormee.server.service;

import com.ormee.server.config.CodeGenerator;
import com.ormee.server.dto.lecture.LectureListDto;
import com.ormee.server.dto.lecture.LectureRequestDto;
import com.ormee.server.dto.lecture.LectureResponseDto;
import com.ormee.server.dto.quiz.QuizListDto;
import com.ormee.server.dto.student_lecture.StudentDetailDto;
import com.ormee.server.exception.CustomException;
import com.ormee.server.exception.ExceptionType;
import com.ormee.server.model.Lecture;
import com.ormee.server.model.LectureDay;
import com.ormee.server.model.Quiz;
import com.ormee.server.model.StudentLecture;
import com.ormee.server.model.member.Member;
import com.ormee.server.repository.LectureRepository;
import com.ormee.server.repository.MemberRepository;
import com.ormee.server.repository.MemoRepository;
import com.ormee.server.repository.QuizRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service

public class LectureService {

    private final LectureRepository lectureRepository;
    private final MemberRepository memberRepository;
    private final QuizService quizService;
    private final MemoRepository memoRepository;
    private final QuizRepository quizRepository;
    private final CodeGenerator codeGenerator;

    public LectureService(LectureRepository lectureRepository, MemberRepository memberRepository, QuizService quizService, MemoRepository memoRepository, QuizRepository quizRepository, CodeGenerator codeGenerator) {
        this.lectureRepository = lectureRepository;
        this.memberRepository = memberRepository;
        this.quizService = quizService;
        this.memoRepository = memoRepository;
        this.quizRepository = quizRepository;
        this.codeGenerator = codeGenerator;
    }

    public void save(LectureRequestDto lectureRequestDto, String username) {
        Member teacher = memberRepository.findByUsername(username).orElseThrow(() -> new CustomException(ExceptionType.MEMBER_NOT_FOUND_EXCEPTION));
        // Role이 teacher가 아닌 경우 예외 처리

        int lectureCode = codeGenerator.generateCode();

        while(lectureRepository.existsByCode(lectureCode)) {
            lectureCode = codeGenerator.generateCode();
        }

        Lecture lecture = Lecture.builder()
                .teacher(teacher)
                .code(lectureCode)
                .password(lectureRequestDto.getPassword())
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
        // Role이 teacher가 아닌 경우 예외 처리

        lecture.setPassword(lectureRequestDto.getPassword());
        lecture.setTitle(lectureRequestDto.getTitle());
        lecture.setDescription(lectureRequestDto.getDescription());
        lecture.setLectureDays(lectureRequestDto.getLectureDays().stream().map(LectureDay::fromKorean).toList());
        lecture.setStartTime(lectureRequestDto.getStartTime());
        lecture.setEndTime(lectureRequestDto.getEndTime());
        lecture.setStartDate(lectureRequestDto.getStartDate());
        lecture.setDueDate(lectureRequestDto.getDueDate());

        lectureRepository.save(lecture);
    }

    public void delete(Long lectureId, String username) {
        Lecture lecture = lectureRepository.findById(lectureId).orElseThrow(() -> new CustomException(ExceptionType.LECTURE_NOT_FOUND_EXCEPTION));
        Member teacher = memberRepository.findByUsername(username).orElseThrow(() -> new CustomException(ExceptionType.MEMBER_NOT_FOUND_EXCEPTION));
        // Role이 teacher가 아닌 경우 예외 처리

        teacher.removeLecture(lecture);
        memberRepository.save(teacher);
    }

    public LectureListDto findAllLectures(String username) {
        Member teacher = memberRepository.findByUsername(username).orElseThrow(() -> new CustomException(ExceptionType.MEMBER_NOT_FOUND_EXCEPTION));

        LocalDateTime now = LocalDateTime.now();

        List<Lecture> lectures = lectureRepository.findAllByTeacher(teacher);

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
                .code(lecture.getCode())
                .profileImage(teacher.getImage().getFilePath())
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


//    public LectureResponseDto findLectureByCode(Integer code) {
//        Lecture lecture = lectureRepository.findByCode(code).orElseThrow(()-> new CustomException(ExceptionType.LECTURE_NOT_FOUND_EXCEPTION));
//        return lectureToDto(lecture);
//    }
//
//    public void close(Integer code) {
//        Lecture lecture = lectureRepository.findByCode(code).orElseThrow(()-> new CustomException(ExceptionType.LECTURE_NOT_FOUND_EXCEPTION));
//        LocalDateTime now = LocalDateTime.now();
//        lecture.setDueDate(now);
//        lectureRepository.save(lecture);
//    }
//
//    public boolean validate(Integer code) {
//        return lectureRepository.existsByCode(code);
//    }
}
