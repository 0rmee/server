package com.ormee.server.home.service;

import com.ormee.server.global.exception.CustomException;
import com.ormee.server.global.exception.ExceptionType;
import com.ormee.server.home.domain.Banner;
import com.ormee.server.home.dto.BannerDto;
import com.ormee.server.home.dto.BannerRequestDto;
import com.ormee.server.home.dto.HomeworkDto;
import com.ormee.server.home.dto.QuizDto;
import com.ormee.server.home.repository.BannerRepository;
import com.ormee.server.homework.domain.Homework;
import com.ormee.server.homework.repository.HomeworkRepository;
import com.ormee.server.homework.repository.HomeworkSubmitRepository;
import com.ormee.server.lecture.domain.Lecture;
import com.ormee.server.lecture.domain.StudentLecture;
import com.ormee.server.lecture.repository.StudentLectureRepository;
import com.ormee.server.member.domain.Member;
import com.ormee.server.member.repository.MemberRepository;
import com.ormee.server.quiz.domain.Quiz;
import com.ormee.server.quiz.repository.ProblemSubmitRepository;
import com.ormee.server.quiz.repository.QuizRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class StudentHomeService {
    private final MemberRepository memberRepository;
    private final QuizRepository quizRepository;
    private final StudentLectureRepository studentLectureRepository;
    private final ProblemSubmitRepository problemSubmitRepository;
    private final HomeworkRepository homeworkRepository;
    private final HomeworkSubmitRepository homeworkSubmitRepository;
    private final BannerRepository bannerRepository;
    public StudentHomeService(MemberRepository memberRepository,QuizRepository quizRepository, StudentLectureRepository studentLectureRepository, ProblemSubmitRepository problemSubmitRepository, HomeworkRepository homeworkRepository, HomeworkSubmitRepository homeworkSubmitRepository, BannerRepository bannerRepository) {
        this.memberRepository = memberRepository;
        this.quizRepository = quizRepository;
        this.studentLectureRepository = studentLectureRepository;
        this.problemSubmitRepository = problemSubmitRepository;
        this.homeworkRepository = homeworkRepository;
        this.homeworkSubmitRepository = homeworkSubmitRepository;
        this.bannerRepository = bannerRepository;
    }

    public List<QuizDto> findAllQuiz(Authentication authentication) {
        Member student = memberRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new CustomException(ExceptionType.MEMBER_NOT_FOUND_EXCEPTION));

        List<StudentLecture> enrolledLectures = studentLectureRepository.findAllByStudent(student);
        List<Lecture> lectures = enrolledLectures.stream()
                .filter(l -> !Boolean.TRUE.equals(l.getBlocked()))
                .map(StudentLecture::getLecture)
                .toList();

        List<Quiz> quizList = quizRepository.findAllByIsDraftFalseAndIsOpenedTrueAndLectureInOrderByCreatedAtAsc(lectures);

        List<QuizDto> quizListDtos = new ArrayList<>();
        for (Quiz quiz : quizList) {
            boolean isSubmitted = problemSubmitRepository.existsByStudentAndProblem_Quiz(student, quiz);
            if (isSubmitted) continue;

            quizListDtos.add(QuizDto.builder()
                    .id(quiz.getId())
                    .lectureTitle(quiz.getLecture().getTitle())
                    .quizTitle(quiz.getTitle())
                    .quizDueTime(
                            quiz.getDueTime() == null
                                    ? null
                                    : quiz.getDueTime().format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm"))
                    )
                    .build());
        }

        return quizListDtos;
    }

    public List<HomeworkDto> findAllHomework(Authentication authentication) {
        Member student = memberRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new CustomException(ExceptionType.MEMBER_NOT_FOUND_EXCEPTION));

        List<StudentLecture> enrolledLectures = studentLectureRepository.findAllByStudent(student);
        List<Lecture> lectures = enrolledLectures.stream()
                .filter(l -> !Boolean.TRUE.equals(l.getBlocked()))
                .map(StudentLecture::getLecture)
                .toList();

        List<Homework> homeworkList = homeworkRepository.findAllByIsDraftFalseAndLectureInOrderByCreatedAtAsc(lectures);

        List<HomeworkDto> homeworkListDtos = new ArrayList<>();
        for (Homework homework : homeworkList) {
            boolean isSubmitted = homeworkSubmitRepository.existsByHomeworkAndStudent(homework, student);
            if (isSubmitted) continue;

            homeworkListDtos.add(HomeworkDto.builder()
                    .id(homework.getId())
                    .lectureTitle(homework.getLecture().getTitle())
                    .homeworkTitle(homework.getTitle())
                    .homeworkDueTime(
                            homework.getDueTime() == null
                                    ? null
                                    : homework.getDueTime().format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm"))
                    ).build());
        }
        return homeworkListDtos;
    }

    public List<BannerDto> findAllBanner() {
        List<Banner> bannerList = bannerRepository.findAll();

        List<BannerDto> bannerListDtos = new ArrayList<>();
        for (Banner banner : bannerList){
            bannerListDtos.add(BannerDto.builder()
                    .id(banner.getId())
                    .image(banner.getImage())
                    .path(banner.getPath()).build());
        }
        return bannerListDtos;
    }

    public void saveBanner(BannerRequestDto request) {
        Banner banner = Banner.builder()
                .image(request.getImage())
                .path(request.getPath())
                .build();

        bannerRepository.save(banner);
    }
}
