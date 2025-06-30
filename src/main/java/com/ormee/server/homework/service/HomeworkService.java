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
import com.ormee.server.attachment.domain.AttachmentType;
import com.ormee.server.homework.repository.FeedbackRepository;
import com.ormee.server.homework.repository.HomeworkRepository;
import com.ormee.server.homework.repository.HomeworkSubmitRepository;
import com.ormee.server.lecture.domain.Lecture;
import com.ormee.server.lecture.repository.LectureRepository;
import com.ormee.server.attachment.service.AttachmentService;
import com.ormee.server.member.domain.Member;
import com.ormee.server.member.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class HomeworkService {
    private final HomeworkRepository homeworkRepository;
    private final MemberRepository memberRepository;
    private final LectureRepository lectureRepository;
    private final HomeworkSubmitRepository homeworkSubmitRepository;
    private final FeedbackRepository feedbackRepository;
    private final AttachmentRepository attachmentRepository;
    private final AttachmentService attachmentService;

    public HomeworkService(HomeworkRepository homeworkRepository, MemberRepository memberRepository, LectureRepository lectureRepository, HomeworkSubmitRepository homeworkSubmitRepository, FeedbackRepository feedbackRepository, AttachmentRepository attachmentRepository, AttachmentService attachmentService) {
        this.homeworkRepository = homeworkRepository;
        this.memberRepository = memberRepository;
        this.lectureRepository = lectureRepository;
        this.homeworkSubmitRepository = homeworkSubmitRepository;
        this.feedbackRepository = feedbackRepository;
        this.attachmentRepository = attachmentRepository;
        this.attachmentService = attachmentService;
    }

    public void create(Long lectureId, HomeworkSaveDto homeworkSaveDto, String username) {
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

    public FeedbackHomeworkListDto getFeedbackCompletedList(Long lectureId) {
        Lecture lecture = lectureRepository.findById(lectureId).orElseThrow(() -> new CustomException(ExceptionType.LECTURE_NOT_FOUND_EXCEPTION));
        List<Homework> homeworks = homeworkRepository.findAllByLectureAndIsDraftFalseOrderByCreatedAtDesc(lecture);
        List<HomeworkDto> feedbackCompletedHomeworks = new ArrayList<>();
        List<HomeworkDto> feedbackNotCompletedHomeworks = new ArrayList<>();

        for (Homework homework : homeworks) {
            boolean hasFeedback = homeworkSubmitRepository.existsByHomeworkAndIsFeedback(homework, true);
            HomeworkDto dto = HomeworkDto.builder()
                    .author(Optional.ofNullable(homework.getAuthor())
                            .map(Member::getNickname)
                            .orElse(homework.getLecture().getTeacher().getNickname()))
                    .title(homework.getTitle())
                    .openTime(homework.getOpenTime())
                    .dueTime(homework.getDueTime())
                    .build();
            if (hasFeedback) {
                feedbackCompletedHomeworks.add(dto);
            } else {
                feedbackNotCompletedHomeworks.add(dto);
            }
        }

        return FeedbackHomeworkListDto.builder()
                .feedbackCompletedHomeworks(feedbackCompletedHomeworks)
                .feedbackNotCompletedHomeworks(feedbackNotCompletedHomeworks)
                .build();
    }

    public HomeworkDto read(Long homeworkId) {
        Homework homework = homeworkRepository.findById(homeworkId).orElseThrow(() -> new CustomException(ExceptionType.HOMEWORK_NOT_FOUND_EXCEPTION));

        return HomeworkDto.builder()
                .id(homework.getId())
                .author(Optional.ofNullable(homework.getAuthor())
                        .map(Member::getNickname)
                        .orElse(homework.getLecture().getTeacher().getNickname()))
                .title(homework.getTitle())
                .description(homework.getDescription())
                .fileNames(homework.getAttachments().stream()
                        .map(attachment -> Objects.requireNonNullElse(attachment.getOriginalFileName(), attachment.getFileName())).toList())
                .filePaths(homework.getAttachments().stream().map(Attachment::getFilePath).toList())
                .openTime(homework.getOpenTime())
                .dueTime(homework.getDueTime())
                .build();
    }

    public void update(Long homeworkId, HomeworkSaveDto homeworkSaveDto) {
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

        homework.setIsDraft(homeworkSaveDto.getIsDraft());

        homeworkRepository.save(homework);
    }

    public void delete(Long homeworkId) {
        Homework homework = homeworkRepository.findById(homeworkId).orElseThrow(() -> new CustomException(ExceptionType.HOMEWORK_NOT_FOUND_EXCEPTION));
        homeworkRepository.delete(homework);
    }
}
