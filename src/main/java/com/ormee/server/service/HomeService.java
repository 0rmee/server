package com.ormee.server.service;

import com.ormee.server.dto.home.HomeDto;
import com.ormee.server.dto.home.LectureDto;
import com.ormee.server.dto.home.ListDto;
import com.ormee.server.exception.CustomException;
import com.ormee.server.exception.ExceptionType;
import com.ormee.server.model.Lecture;
import com.ormee.server.repository.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class HomeService {
    private final LectureRepository lectureRepository;
    private final QuizRepository quizRepository;
    private final ProblemRepository problemRepository;
    private final ProblemSubmitRepository problemSubmitRepository;
    private final AssignmentRepository assignmentRepository;
    private final AssignmentSubmitRepository assignmentSubmitRepository;
    private final QuestionRepository questionRepository;
    private final NoticeRepository noticeRepository;

    public HomeService(LectureRepository lectureRepository, QuizRepository quizRepository, ProblemRepository problemRepository, ProblemSubmitRepository problemSubmitRepository, AssignmentRepository assignmentRepository, AssignmentSubmitRepository assignmentSubmitRepository, QuestionRepository questionRepository, NoticeRepository noticeRepository) {
        this.lectureRepository = lectureRepository;
        this.quizRepository = quizRepository;
        this.problemRepository = problemRepository;
        this.problemSubmitRepository = problemSubmitRepository;
        this.assignmentRepository = assignmentRepository;
        this.assignmentSubmitRepository = assignmentSubmitRepository;
        this.questionRepository = questionRepository;
        this.noticeRepository = noticeRepository;
    }

    public HomeDto getInfo(Long lectureId) {
        Lecture lecture = lectureRepository.findById(lectureId).orElseThrow(() -> new CustomException(ExceptionType.LECTURE_NOT_FOUND_EXCEPTION));

        return HomeDto.builder()
                .lecture(getLectureInfo(lecture))
                .assignments(getAssignmentsInfo(lecture))
                .questions(getQuestionsInfo(lecture))
                .notices(getNoticesInfo(lecture))
                .build();
    }

    public LectureDto getLectureInfo(Lecture lecture) {
        return LectureDto.builder()
                .title(lecture.getTitle())
                .description(lecture.getDescription())
                .code(lecture.getCode())
                .openTime(lecture.getStartDate())
                .closeTime(lecture.getDueDate())
                .build();
    }

    public List<ListDto> getAssignmentsInfo(Lecture lecture) {
        Long totalStudents = (long) lecture.getStudentLectures().size();

        List<ListDto> quizzes = quizRepository.findAllByLectureAndIsDraftAndIsOpenedOrderByDueTimeDesc(lecture, false, true)
                .stream()
                .map(quiz -> ListDto.builder()
                        .type("퀴즈")
                        .title(quiz.getTitle())
                        .submitStudents(problemSubmitRepository.countAllByProblem(problemRepository.findFirstByQuiz(quiz)))
                        .totalStudents(totalStudents)
                        .openTime(quiz.getOpenTime())
                        .dueTime(quiz.getDueTime())
                        .build())
                .toList();

        List<ListDto> homeworks = assignmentRepository.findAllByLectureAndIsDraftFalseOrderByCreatedAtDesc(lecture)
                .stream()
                .map(assignment -> ListDto.builder()
                        .type("숙제")
                        .title(assignment.getTitle())
                        .submitStudents(assignmentSubmitRepository.countAllByAssignment(assignment))
                        .totalStudents(totalStudents)
                        .openTime(assignment.getOpenTime())
                        .dueTime(assignment.getDueTime())
                        .build())
                .toList();

        List<ListDto> assignments = new ArrayList<>();
        assignments.addAll(quizzes);
        assignments.addAll(homeworks);
        return assignments.stream()
                .sorted(Comparator.comparing(ListDto::getDueTime).reversed())
                .toList();
    }

    private List<ListDto> getQuestionsInfo(Lecture lecture) {
         Pageable pageable = PageRequest.of(0, 3, Sort.by("createdAt").descending());

        return questionRepository.findAllByLectureOrderByCreatedAtDesc(lecture, pageable)
                .stream()
                .map(question -> ListDto.builder()
                        .title(question.getTitle())
                        .openTime(question.getCreatedAt())
                        .build())
                .toList();
    }

    private List<ListDto> getNoticesInfo(Lecture lecture) {
        List<ListDto> notices = new ArrayList<>(noticeRepository.findAllByLectureAndIsPinnedTrueOrderByCreatedAtDesc(lecture)
                .stream()
                .map(notice -> ListDto.builder()
                        .type("고정")
                        .title(notice.getTitle())
                        .openTime(notice.getPostDate())
                        .build()
                ).toList());

        if(notices.size() < 3) {
            Pageable pageable = PageRequest.of(0, 3 - notices.size(), Sort.by("createdAt").descending());
            notices.addAll(noticeRepository.findAllByLectureAndIsDraftFalseOrderByCreatedAtDesc(lecture, pageable)
                    .stream()
                    .map(notice -> ListDto.builder()
                            .title(notice.getTitle())
                            .openTime(notice.getPostDate())
                            .build())
                    .toList());
        }

        return notices;
    }
}
