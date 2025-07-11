package com.ormee.server.notification.service;

import com.ormee.server.notification.domain.Notification;
import com.ormee.server.notification.domain.NotificationType;
import com.ormee.server.notification.dto.NotificationDto;
import com.ormee.server.global.exception.CustomException;
import com.ormee.server.global.exception.ExceptionType;
import com.ormee.server.homework.domain.Homework;
import com.ormee.server.homework.repository.HomeworkRepository;
import com.ormee.server.lecture.domain.Lecture;
import com.ormee.server.lecture.repository.LectureRepository;
import com.ormee.server.memo.domain.Memo;
import com.ormee.server.memo.repository.MemoRepository;
import com.ormee.server.notification.repository.NotificationRepository;
import com.ormee.server.question.domain.Question;
import com.ormee.server.quiz.domain.Quiz;
import com.ormee.server.quiz.repository.QuizRepository;
import jakarta.transaction.Transactional;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final LectureRepository lectureRepository;
    private final QuizRepository quizRepository;
    private final HomeworkRepository homeworkRepository;
    private final MemoRepository memoRepository;

    public NotificationService(NotificationRepository notificationRepository, LectureRepository lectureRepository, QuizRepository quizRepository, HomeworkRepository homeworkRepository, MemoRepository memoRepository) {
        this.notificationRepository = notificationRepository;
        this.lectureRepository = lectureRepository;
        this.quizRepository = quizRepository;
        this.homeworkRepository = homeworkRepository;
        this.memoRepository = memoRepository;
    }

    public void create(NotificationType type, Object parent) {
        Lecture lecture;
        Long parentId;
        String title;
        String description;

        switch (type) {
            case QUIZ -> {
                Quiz quiz = (Quiz) parent;
                lecture = quiz.getLecture();
                parentId = quiz.getId();
                title = lecture.getTitle();
                description = "퀴즈가 마감되었어요.";
            }
            case HOMEWORK -> {
                Homework homework = (Homework) parent;
                lecture = homework.getLecture();
                parentId = homework.getId();
                title = lecture.getTitle();
                description = "숙제가 마감되었어요.";
            }
            case MEMO -> {
                Memo memo = (Memo) parent;
                lecture = memo.getLecture();
                parentId = memo.getId();
                title = lecture.getTitle();
                description = "쪽지가 마감되었어요.";
            }
            case QUESTION -> {
                Question question = (Question) parent;
                lecture = question.getLecture();
                parentId = question.getId();
                title = lecture.getTitle();
                description = question.getStudent().getName() + " 학생이 질문을 등록했어요.";
            }
            default -> throw new IllegalArgumentException("지원하지 않는 알림 타입");
        }

        Notification notification = Notification.builder()
                .lecture(lecture)
                .type(type)
                .parentId(parentId)
                .title(title)
                .description(description)
                .isRead(false)
                .build();

        notificationRepository.save(notification);
    }

    public List<NotificationDto> getNotificationsByLectureIdAndType(Long lectureId, String filter) {
        Lecture lecture = lectureRepository.findById(lectureId).orElseThrow(() -> new CustomException(ExceptionType.LECTURE_NOT_FOUND_EXCEPTION));
        List<Notification> notifications = switch (filter) {
            case "퀴즈" -> notificationRepository.findAllByLectureAndTypeOrderByCreatedAtDesc(lecture, NotificationType.QUIZ);
            case "쪽지" -> notificationRepository.findAllByLectureAndTypeOrderByCreatedAtDesc(lecture, NotificationType.MEMO);
            case "숙제" -> notificationRepository.findAllByLectureAndTypeOrderByCreatedAtDesc(lecture, NotificationType.HOMEWORK);
            case "질문" -> notificationRepository.findAllByLectureAndTypeOrderByCreatedAtDesc(lecture, NotificationType.QUESTION);
            default -> notificationRepository.findAllByLectureOrderByCreatedAtDesc(lecture);
        };

        return notifications.stream()
                .map(notification -> NotificationDto.builder()
                        .notificationId(notification.getId())
                        .type(notification.getType().getKorean())
                        .title(notification.getTitle())
                        .description(notification.getDescription())
                        .parentId(notification.getParentId())
                        .isRead(notification.getIsRead() != null && notification.getIsRead())
                        .createdAt(notification.getCreatedAt())
                        .build())
                .toList();
    }

    @Transactional
    public void markAllAsRead(Long lectureId) {
        Lecture lecture = lectureRepository.findById(lectureId).orElseThrow(() -> new CustomException(ExceptionType.LECTURE_NOT_FOUND_EXCEPTION));
        List<Notification> notifications = notificationRepository.findAllByLectureOrderByCreatedAtDesc(lecture);

        notifications.forEach(notification -> notification.setIsRead(true));
    }

    @Transactional
    public void deleteAll(Long lectureId) {
        Lecture lecture = lectureRepository.findById(lectureId).orElseThrow(() -> new CustomException(ExceptionType.LECTURE_NOT_FOUND_EXCEPTION));
        notificationRepository.deleteAllByLecture(lecture);
    }

    @Transactional
    public void markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId).orElseThrow(() -> new CustomException(ExceptionType.NOTIFICATION_NOT_FOUND_EXCEPTION));
        notification.setIsRead(true);
    }

    public void deleteById(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId).orElseThrow(() -> new CustomException(ExceptionType.NOTIFICATION_NOT_FOUND_EXCEPTION));
        notificationRepository.delete(notification);
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void deleteAllExpiredNotifications() {
        notificationRepository.deleteAllByCreatedAtBefore(LocalDateTime.now().minusDays(30));
    }

    @Scheduled(cron = "0 * * * * *")
    public void createAllNotifications() {
        LocalDateTime now = LocalDateTime.now();
        createQuizNotifications(now);
        createHomeworkNotifications(now);
        createMemoNotifications(now);
    }

    private void createQuizNotifications(LocalDateTime now) {
        List<Quiz> quizzes = quizRepository.findAllByIsDraftFalseAndNotifiedFalseAndDueTimeBefore(now);
        for(Quiz quiz : quizzes) {
            create(NotificationType.QUIZ, quiz);
            quiz.setNotified(true);
            quizRepository.save(quiz);
        }
    }

    private void createHomeworkNotifications(LocalDateTime now) {
        List<Homework> homeworks = homeworkRepository.findAllByIsDraftFalseAndNotifiedFalseAndDueTimeBefore(now);
        for(Homework homework : homeworks) {
            create(NotificationType.HOMEWORK, homework);
            homework.setNotified(true);
            homeworkRepository.save(homework);
        }
    }

    private void createMemoNotifications(LocalDateTime now) {
        List<Memo> memos = memoRepository.findAllByNotifiedFalseAndDueTimeBefore(now);
        for(Memo memo : memos) {
            create(NotificationType.MEMO, memo);
            memo.setNotified(true);
            memoRepository.save(memo);
        }
    }
}
