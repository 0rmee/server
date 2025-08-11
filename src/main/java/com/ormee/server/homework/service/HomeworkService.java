package com.ormee.server.homework.service;

import com.ormee.server.attachment.repository.AttachmentRepository;
import com.ormee.server.homework.domain.Homework;
import com.ormee.server.homework.dto.HomeworkDto;
import com.ormee.server.homework.dto.HomeworkListDto;
import com.ormee.server.homework.dto.HomeworkSaveDto;
import com.ormee.server.homework.dto.FeedbackHomeworkListDto;
import com.ormee.server.global.exception.CustomException;
import com.ormee.server.global.exception.ExceptionType;
import com.ormee.server.attachment.domain.Attachment;
import com.ormee.server.homework.repository.FeedbackRepository;
import com.ormee.server.homework.repository.HomeworkRepository;
import com.ormee.server.homework.repository.HomeworkSubmitRepository;
import com.ormee.server.lecture.domain.Lecture;
import com.ormee.server.lecture.repository.LectureRepository;
import com.ormee.server.attachment.service.AttachmentService;
import com.ormee.server.member.domain.Member;
import com.ormee.server.member.repository.MemberRepository;
import com.ormee.server.notification.domain.NotificationDetailType;
import com.ormee.server.notification.domain.NotificationType;
import com.ormee.server.notification.dto.StudentNotificationRequestDto;
import com.ormee.server.notification.service.StudentNotificationService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class HomeworkService {
    private final HomeworkRepository homeworkRepository;
    private final MemberRepository memberRepository;
    private final LectureRepository lectureRepository;
    private final HomeworkSubmitRepository homeworkSubmitRepository;
    private final FeedbackRepository feedbackRepository;
    private final AttachmentRepository attachmentRepository;
    private final HomeworkSubmitService homeworkSubmitService;
    private final FeedbackService feedbackService;
    private final AttachmentService attachmentService;
    private final StudentNotificationService studentNotificationService;

    public HomeworkService(HomeworkRepository homeworkRepository, MemberRepository memberRepository, LectureRepository lectureRepository, HomeworkSubmitRepository homeworkSubmitRepository, FeedbackRepository feedbackRepository, AttachmentRepository attachmentRepository, HomeworkSubmitService homeworkSubmitService, FeedbackService feedbackService, AttachmentService attachmentService, StudentNotificationService studentNotificationService) {
        this.homeworkRepository = homeworkRepository;
        this.memberRepository = memberRepository;
        this.lectureRepository = lectureRepository;
        this.homeworkSubmitRepository = homeworkSubmitRepository;
        this.feedbackRepository = feedbackRepository;
        this.attachmentRepository = attachmentRepository;
        this.homeworkSubmitService = homeworkSubmitService;
        this.feedbackService = feedbackService;
        this.attachmentService = attachmentService;
        this.studentNotificationService = studentNotificationService;
    }

    public void create(Long lectureId, HomeworkSaveDto homeworkSaveDto, String username) throws Exception {
        Lecture lecture = lectureRepository.findById(lectureId)
                .orElseThrow(() -> new CustomException(ExceptionType.LECTURE_NOT_FOUND_EXCEPTION));
        Member author = memberRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException(ExceptionType.MEMBER_NOT_FOUND_EXCEPTION));

        Homework homework = Homework.builder()
                .lecture(lecture)
                .author(author)
                .title(homeworkSaveDto.getTitle())
                .description(homeworkSaveDto.getDescription())
                .isDraft(homeworkSaveDto.getIsDraft())
                .notified(false)
                .openTime(homeworkSaveDto.getOpenTime())
                .dueTime(homeworkSaveDto.getDueTime())
                .build();

        homework = homeworkRepository.save(homework);

        List<Attachment> attachments = homeworkSaveDto.getFileIds().stream()
                .map(id -> attachmentRepository.findById(id)
                        .orElseThrow(() -> new CustomException(ExceptionType.ATTACHMENT_NOT_FOUND_EXCEPTION)))
                .collect(Collectors.toList());

        for (Attachment attachment : attachments) {
            attachment.setParentId(homework.getId().toString());
            attachmentRepository.save(attachment);
        }

        homework.setAttachments(attachments);

        if(!homeworkSaveDto.getIsDraft()) {
            sendNotification(lecture, homework, NotificationDetailType.REGISTER, "숙제가 등록되었어요.");
        }

        homeworkRepository.save(homework);
    }

    public HomeworkListDto getList(Long lectureId) {
        Lecture lecture = lectureRepository.findById(lectureId).orElseThrow(() -> new CustomException(ExceptionType.LECTURE_NOT_FOUND_EXCEPTION));
        List<Homework> homeworks = homeworkRepository.findAllByLectureAndIsDraftFalseOrderByCreatedAtDesc(lecture);

        LocalDateTime now = LocalDateTime.now();

        List<HomeworkDto> openedHomeworks = homeworks.stream()
                .filter(homework -> homework.getDueTime().isAfter(now))
                .map(homework -> HomeworkDto.builder()
                        .id(homework.getId())
                        .author(Optional.ofNullable(homework.getAuthor())
                                .map(Member::getNickname)
                                .orElse(homework.getLecture().getTeacher().getNickname()))
                        .title(homework.getTitle())
                        .feedbackCompleted(homeworkSubmitRepository.countAllByHomework(homework) - feedbackRepository.countAllByHomeworkSubmit_Homework(homework) > 0)
                        .openTime(homework.getOpenTime())
                        .dueTime(homework.getDueTime())
                        .build())
                .toList();

        List<HomeworkDto> closedHomeworks = homeworks.stream()
                .filter(homework -> !homework.getDueTime().isAfter(now))
                .map(homework -> HomeworkDto.builder()
                        .id(homework.getId())
                        .author(Optional.ofNullable(homework.getAuthor())
                                .map(Member::getNickname)
                                .orElse(homework.getLecture().getTeacher().getNickname()))
                        .title(homework.getTitle())
                        .feedbackCompleted(homeworkSubmitRepository.countAllByHomework(homework) - feedbackRepository.countAllByHomeworkSubmit_Homework(homework) > 0)
                        .openTime(homework.getOpenTime())
                        .dueTime(homework.getDueTime())
                        .build())
                .toList();

        return HomeworkListDto.builder()
                .openedHomeworks(openedHomeworks)
                .closedHomeworks(closedHomeworks)
                .build();
    }

    public List<HomeworkDto> getDrafts(Long lectureId) {
        Lecture lecture = lectureRepository.findById(lectureId).orElseThrow(() -> new CustomException(ExceptionType.LECTURE_NOT_FOUND_EXCEPTION));
        List<Homework> homeworks = homeworkRepository.findAllByLectureAndIsDraftTrueOrderByCreatedAtDesc(lecture);

        return homeworks.stream().map(homework -> HomeworkDto.builder()
                .id(homework.getId())
                .author(Optional.ofNullable(homework.getAuthor())
                        .map(Member::getNickname)
                        .orElse(homework.getLecture().getTeacher().getNickname()))
                .title(homework.getTitle())
                .openTime(homework.getCreatedAt())
                .build()).toList();
    }

    public List<HomeworkDto> loadSavedHomeworks(Long lectureId) {
        Lecture lecture = lectureRepository.findById(lectureId).orElseThrow(() -> new CustomException(ExceptionType.LECTURE_NOT_FOUND_EXCEPTION));
        List<Homework> homeworks = homeworkRepository.findAllByLectureAndIsDraftFalseOrderByCreatedAtDesc(lecture);

        return homeworks.stream().map(homework -> HomeworkDto.builder()
                .id(homework.getId())
                .author(Optional.ofNullable(homework.getAuthor())
                        .map(Member::getNickname)
                        .orElse(homework.getLecture().getTeacher().getNickname()))
                .title(homework.getTitle())
                .openTime(homework.getCreatedAt())
                .build()).toList();
    }

    public HomeworkDto read(Long homeworkId) {
        Homework homework = homeworkRepository.findById(homeworkId).orElseThrow(() -> new CustomException(ExceptionType.HOMEWORK_NOT_FOUND_EXCEPTION));

        List<Attachment> attachments = Optional.ofNullable(homework.getAttachments()).orElse(List.of());

        return HomeworkDto.builder()
                .id(homework.getId())
                .author(Optional.ofNullable(homework.getAuthor())
                        .map(Member::getNickname)
                        .orElse(homework.getLecture().getTeacher().getNickname()))
                .title(homework.getTitle())
                .description(homework.getDescription())
                .fileIds(attachments.stream().map(Attachment::getId).toList())
                .fileNames(attachments.stream()
                        .map(att -> Objects.requireNonNullElse(att.getOriginalFileName(), att.getFileName()))
                        .toList())
                .filePaths(attachments.stream().map(Attachment::getFilePath).toList())
                .openTime(homework.getOpenTime())
                .dueTime(homework.getDueTime())
                .build();
    }

    public void update(Long homeworkId, HomeworkSaveDto homeworkSaveDto) throws Exception {
        Homework homework = homeworkRepository.findById(homeworkId)
                .orElseThrow(() -> new CustomException(ExceptionType.HOMEWORK_NOT_FOUND_EXCEPTION));

        if(homeworkSaveDto.getTitle() != null) {
            homework.setTitle(homeworkSaveDto.getTitle());
        }

        if(homeworkSaveDto.getDescription() != null) {
            homework.setDescription(homeworkSaveDto.getDescription());
        }

        List<Long> fileIds = homeworkSaveDto.getFileIds() != null ? homeworkSaveDto.getFileIds() : List.of();

        List<Attachment> existingAttachments = homework.getAttachments();

        List<Attachment> toRemove = existingAttachments.stream()
                .filter(att -> !fileIds.contains(att.getId()))
                .toList();

        for (Attachment att : toRemove) {
            existingAttachments.remove(att);
            attachmentService.delete(att.getId());
        }

        List<Long> existingIds = existingAttachments.stream()
                .map(Attachment::getId)
                .toList();

        for (Long fileId : fileIds) {
            if (!existingIds.contains(fileId)) {
                Attachment attachment = attachmentRepository.findById(fileId)
                        .orElseThrow(() -> new CustomException(ExceptionType.ATTACHMENT_NOT_FOUND_EXCEPTION));
                existingAttachments.add(attachment);
                attachment.setParentId(homework.getId().toString());
                attachmentRepository.save(attachment);
            }
        }

        if(homework.getIsDraft() && !homeworkSaveDto.getIsDraft()) {
            sendNotification(homework.getLecture(), homework, NotificationDetailType.REGISTER, "숙제가 등록되었어요.");
        }
        homework.setIsDraft(homeworkSaveDto.getIsDraft());

        homeworkRepository.save(homework);
    }

    public void delete(Long homeworkId) {
        Homework homework = homeworkRepository.findById(homeworkId).orElseThrow(() -> new CustomException(ExceptionType.HOMEWORK_NOT_FOUND_EXCEPTION));

        feedbackService.deleteAllByHomework(homework);
        homeworkSubmitService.deleteAllByHomework(homework);

        for (Attachment attachment : homework.getAttachments()) {
            attachmentService.delete(attachment.getId());
        }
        homeworkRepository.delete(homework);
    }

    public List<HomeworkDto> getHomeworks(Long lectureId, String username) {
        Lecture lecture = lectureRepository.findById(lectureId).orElseThrow(() -> new CustomException(ExceptionType.LECTURE_NOT_FOUND_EXCEPTION));
        Member student = memberRepository.findByUsername(username).orElseThrow(() -> new CustomException(ExceptionType.MEMBER_NOT_FOUND_EXCEPTION));

        List<Homework> homeworks = homeworkRepository.findAllByLectureAndIsDraftFalseOrderByCreatedAtDesc(lecture);

        return homeworks.stream()
                .map(homework -> HomeworkDto.builder()
                        .id(homework.getId())
                        .author(homework.getAuthor().getNickname())
                        .title(homework.getTitle())
                        .openTime(homework.getOpenTime())
                        .dueTime(homework.getDueTime())
                        .isSubmitted(homeworkSubmitRepository.existsByHomeworkAndStudent(homework, student))
                        .feedbackCompleted(feedbackRepository.existsByHomeworkSubmit_HomeworkAndHomeworkSubmit_Student(homework, student))
                        .build())
                .toList();
    }

    public HomeworkDto getHomework(Long homeworkId, String username) {
        Homework homework = homeworkRepository.findById(homeworkId).orElseThrow(() -> new CustomException(ExceptionType.HOMEWORK_NOT_FOUND_EXCEPTION));
        Member student = memberRepository.findByUsername(username).orElseThrow(() -> new CustomException(ExceptionType.MEMBER_NOT_FOUND_EXCEPTION));

        return HomeworkDto.builder()
                .id(homework.getId())
                .author(homework.getAuthor().getNickname())
                .authorImage(Optional.ofNullable(homework.getAuthor().getImage())
                        .map(Attachment::getFilePath)
                        .orElse(null))
                .title(homework.getTitle())
                .description(homework.getDescription())
                .fileNames(homework.getAttachments().stream()
                        .map(attachment -> Objects.requireNonNullElse(attachment.getOriginalFileName(), attachment.getFileName())).toList())
                .filePaths(homework.getAttachments().stream().map(Attachment::getFilePath).toList())
                .openTime(homework.getCreatedAt())
                .dueTime(homework.getDueTime())
                .isSubmitted(homeworkSubmitRepository.existsByHomeworkAndStudent(homework, student))
                .feedbackCompleted(feedbackRepository.existsByHomeworkSubmit_HomeworkAndHomeworkSubmit_Student(homework, student))
                .build();
    }

    public void sendNotification(Lecture lecture, Homework homework, NotificationDetailType detailType, String body) throws Exception {
        studentNotificationService.create(lecture.getStudentLectures().stream().map(studentLecture -> studentLecture.getStudent().getId()).toList(),
                StudentNotificationRequestDto.builder()
                        .parentId(homework.getId())
                        .type(NotificationType.HOMEWORK)
                        .detailType(detailType)
                        .header(lecture.getTitle())
                        .title(homework.getTitle())
                        .body(body)
                        .content(detailType.equals(NotificationDetailType.REGISTER)? homework.getDescription() : null)
                        .build());
    }

    public void deleteByLecture(Lecture lecture) {
        List<Homework> homeworks = homeworkRepository.findAllByLecture(lecture);
        homeworks.forEach(homework -> delete(homework.getId()));
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void deleteAllExpiredDrafts() {
        List<Homework> homeworks = homeworkRepository.findAllByIsDraftTrueAndCreatedAtBefore(LocalDateTime.now().minusDays(30));
        homeworks.forEach(homework -> delete(homework.getId()));
    }

    @Scheduled(cron = "0 0 17 * * *", zone = "Asia/Seoul")
    @Transactional
    public void notifyNotSubmittedStudentsAtFive() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfTomorrow = now.toLocalDate().plusDays(1).atStartOfDay();
        LocalDateTime startOfDayAfterTomorrow = startOfTomorrow.plusDays(1);

        List<Homework> ongoing = homeworkRepository.findAllByIsDraftFalseAndOpenTimeLessThanEqualAndDueTimeGreaterThanEqualAndDueTimeLessThan(now, startOfTomorrow, startOfDayAfterTomorrow);

        for (Homework homework : ongoing) {
            List<Long> allStudentIds = homework.getLecture().getStudentLectures().stream()
                    .map(studentLecture -> studentLecture.getStudent().getId())
                    .toList();
            if (allStudentIds.isEmpty()) continue;

            Set<Long> submittedIds = homeworkSubmitRepository.findSubmittedStudentIdsByHomeworkId(homework.getId());

            List<Long> targets = allStudentIds.stream()
                    .filter(id -> submittedIds == null || !submittedIds.contains(id))
                    .toList();
            if (targets.isEmpty()) continue;

            try {
                studentNotificationService.create(
                        targets,
                        StudentNotificationRequestDto.builder()
                                .parentId(homework.getId())
                                .type(NotificationType.HOMEWORK)
                                .detailType(NotificationDetailType.REMIND)
                                .header(homework.getLecture().getTitle())
                                .title(homework.getTitle())
                                .body("아직 제출하지 않은 숙제가 있어요!")
                                .content(null)
                                .build()
                );
            } catch (Exception e) {
                System.out.println("Notify failed for homeworkId " + homework.getId() + e);
            }
        }
    }
}
