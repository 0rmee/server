package com.ormee.server.notification.service;

import com.ormee.server.attachment.domain.Attachment;
import com.ormee.server.global.exception.CustomException;
import com.ormee.server.global.exception.ExceptionType;
import com.ormee.server.homework.domain.Homework;
import com.ormee.server.homework.repository.HomeworkRepository;
import com.ormee.server.member.domain.DeviceToken;
import com.ormee.server.member.domain.Member;
import com.ormee.server.member.domain.Role;
import com.ormee.server.member.repository.DeviceTokenRepository;
import com.ormee.server.member.repository.MemberRepository;
import com.ormee.server.memo.domain.Memo;
import com.ormee.server.memo.repository.MemoRepository;
import com.ormee.server.notice.domain.Notice;
import com.ormee.server.notice.repository.NoticeRepository;
import com.ormee.server.notification.domain.NotificationType;
import com.ormee.server.notification.domain.StudentNotification;
import com.ormee.server.notification.dto.StudentNotificationDto;
import com.ormee.server.notification.dto.StudentNotificationListDto;
import com.ormee.server.notification.dto.StudentNotificationRequestDto;
import com.ormee.server.notification.repository.StudentNotificationRepository;
import com.ormee.server.question.domain.Answer;
import com.ormee.server.question.domain.Question;
import com.ormee.server.question.repository.AnswerRepository;
import com.ormee.server.quiz.domain.Quiz;
import com.ormee.server.quiz.repository.QuizRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StudentNotificationService {
    private final StudentNotificationRepository studentNotificationRepository;
    private final MemberRepository memberRepository;
    private final DeviceTokenRepository deviceTokenRepository;
    private final QuizRepository quizRepository;
    private final HomeworkRepository homeworkRepository;
    private final NoticeRepository noticeRepository;
    private final MemoRepository memoRepository;
    private final AnswerRepository answerRepository;
    private final FcmService fcmService;
    public StudentNotificationService(StudentNotificationRepository studentNotificationRepository, MemberRepository memberRepository, DeviceTokenRepository deviceTokenRepository, QuizRepository quizRepository, HomeworkRepository homeworkRepository, NoticeRepository noticeRepository, MemoRepository memoRepository, AnswerRepository answerRepository, FcmService fcmService) {
        this.studentNotificationRepository = studentNotificationRepository;
        this.memberRepository = memberRepository;
        this.deviceTokenRepository = deviceTokenRepository;
        this.quizRepository = quizRepository;
        this.homeworkRepository = homeworkRepository;
        this.noticeRepository = noticeRepository;
        this.memoRepository = memoRepository;
        this.answerRepository = answerRepository;
        this.fcmService = fcmService;
    }

    @Transactional
    public void create(List<Long> targets, StudentNotificationRequestDto studentNotificationRequestDto) throws Exception {
        for(Long memberId : targets) {
            StudentNotification studentNotification = StudentNotification.builder()
                    .memberId(memberId)
                    .parentId(studentNotificationRequestDto.getParentId())
                    .type(studentNotificationRequestDto.getType())
                    .header(studentNotificationRequestDto.getHeader())
                    .title(studentNotificationRequestDto.getTitle())
                    .body(studentNotificationRequestDto.getBody())
                    .content(studentNotificationRequestDto.getContent())
                    .isRead(false)
                    .build();
            studentNotificationRepository.save(studentNotification);

            List<String> targetTokens = deviceTokenRepository.findAllByMemberId(studentNotification.getMemberId()).stream().map(DeviceToken::getDeviceToken).toList();
            for(String targetToken : targetTokens) {
                fcmService.sendMessageTo(targetToken, studentNotification);
            }
        }
    }

    public StudentNotificationListDto getByType(String type, String username) {
        Member student = memberRepository.findByUsernameAndRole(username, Role.STUDENT).orElseThrow(() -> new CustomException(ExceptionType.MEMBER_NOT_FOUND_EXCEPTION));

        List<StudentNotification> notifications;
        if ("과제".equals(type)) {
            notifications = studentNotificationRepository.findAllByMemberIdAndTypeInOrderByCreatedAtDesc(
                    student.getId(),
                    List.of(NotificationType.QUIZ, NotificationType.HOMEWORK)
            );
        } else {
            notifications = studentNotificationRepository.findAllByMemberIdAndTypeInOrderByCreatedAtDesc(
                    student.getId(),
                    List.of(NotificationType.fromKorean(type))
            );
        }

        long unreadCount;
        if ("과제".equals(type)) {
            unreadCount = studentNotificationRepository.countAllByMemberIdAndTypeInAndIsReadFalse(
                    student.getId(),
                    List.of(NotificationType.QUIZ, NotificationType.HOMEWORK)
            );
        } else {
            unreadCount = studentNotificationRepository.countAllByMemberIdAndTypeInAndIsReadFalse(
                    student.getId(),
                    List.of(NotificationType.fromKorean(type))
            );
        }

        return StudentNotificationListDto.builder()
                .count(unreadCount)
                .notifications(notifications.stream().map(studentNotification -> StudentNotificationDto.toDto(studentNotification, getAuthorImage(studentNotification.getType(), studentNotification.getParentId()))).toList())
                .build();
    }

    public List<StudentNotificationDto> search(String keyword, String username) {
        Member student = memberRepository.findByUsernameAndRole(username, Role.STUDENT).orElseThrow(() -> new CustomException(ExceptionType.MEMBER_NOT_FOUND_EXCEPTION));
        List<StudentNotification> notifications = studentNotificationRepository.findAllByMemberIdAndKeyword(student.getId(), keyword);

        return notifications.stream().map(studentNotification -> StudentNotificationDto.toDto(studentNotification, getAuthorImage(studentNotification.getType(), studentNotification.getParentId()))).toList();
    }

    public String getAuthorImage(NotificationType type, Long parentId) {
        switch (type) {
            case QUIZ:
                Quiz quiz = quizRepository.findById(parentId).orElseThrow(() -> new CustomException(ExceptionType.QUESTION_NOT_FOUND_EXCEPTION));
                return Optional.ofNullable(quiz.getAuthor().getImage())
                        .map(Attachment::getFilePath)
                        .orElse(null);
            case MEMO:
                Memo memo = memoRepository.findById(parentId).orElseThrow(() -> new CustomException(ExceptionType.MEMO_NOT_FOUND_EXCEPTION));
                return Optional.ofNullable(memo.getAuthor().getImage())
                        .map(Attachment::getFilePath)
                        .orElse(null);
            case HOMEWORK:
                Homework homework = homeworkRepository.findById(parentId).orElseThrow(() -> new CustomException(ExceptionType.HOMEWORK_NOT_FOUND_EXCEPTION));
                return Optional.ofNullable(homework.getAuthor().getImage())
                        .map(Attachment::getFilePath)
                        .orElse(null);
            case NOTICE:
                Notice notice = noticeRepository.findById(parentId).orElseThrow(() -> new CustomException(ExceptionType.NOTICE_NOT_FOUND_EXCEPTION));
                return Optional.ofNullable(notice.getAuthor().getImage())
                        .map(Attachment::getFilePath)
                        .orElse(null);
            case QUESTION:
                Answer answer = answerRepository.findByQuestion_Id(parentId).orElseThrow(() -> new CustomException(ExceptionType.ANSWER_NOT_FOUND_EXCEPTION));
                return Optional.ofNullable(answer.getAuthor().getImage())
                        .map(Attachment::getFilePath)
                        .orElse(null);
            default: return null;
        }
    }

    public void read(Long notificationId) {
        StudentNotification studentNotification = studentNotificationRepository.findById(notificationId).orElseThrow(() -> new CustomException(ExceptionType.NOTIFICATION_NOT_FOUND_EXCEPTION));
        studentNotification.setIsRead(true);
        studentNotificationRepository.save(studentNotification);
    }

    public void delete(Long notificationId) {
        studentNotificationRepository.deleteById(notificationId);
    }

    public Long getCount(String username) {
        Member student = memberRepository.findByUsername(username).orElseThrow(() -> new  CustomException(ExceptionType.MEMBER_NOT_FOUND_EXCEPTION));
        return studentNotificationRepository.countAllByMemberIdAndIsReadFalse(student.getId());
    }
}
