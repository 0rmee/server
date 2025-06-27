package com.ormee.server.service;

import com.ormee.server.dto.homework.HomeworkDto;
import com.ormee.server.dto.homework.HomeworkListDto;
import com.ormee.server.dto.homework.HomeworkSaveDto;
import com.ormee.server.dto.homework.FeedbackHomeworkListDto;
import com.ormee.server.exception.CustomException;
import com.ormee.server.exception.ExceptionType;
import com.ormee.server.model.Homework;
import com.ormee.server.model.Attachment;
import com.ormee.server.model.AttachmentType;
import com.ormee.server.model.Lecture;
import com.ormee.server.repository.FeedbackRepository;
import com.ormee.server.repository.HomeworkRepository;
import com.ormee.server.repository.HomeworkSubmitRepository;
import com.ormee.server.repository.LectureRepository;
import com.ormee.server.service.attachment.AttachmentService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class HomeworkService {
    private final HomeworkRepository homeworkRepository;
    private final LectureRepository lectureRepository;
    private final HomeworkSubmitRepository homeworkSubmitRepository;
    private final FeedbackRepository feedbackRepository;
    private final AttachmentService attachmentService;

    public HomeworkService(HomeworkRepository homeworkRepository, LectureRepository lectureRepository, HomeworkSubmitRepository homeworkSubmitRepository, FeedbackRepository feedbackRepository, AttachmentService attachmentService) {
        this.homeworkRepository = homeworkRepository;
        this.lectureRepository = lectureRepository;
        this.homeworkSubmitRepository = homeworkSubmitRepository;
        this.feedbackRepository = feedbackRepository;
        this.attachmentService = attachmentService;
    }

    public void create(Long lectureId, HomeworkSaveDto homeworkSaveDto) throws IOException {
        Lecture lecture = lectureRepository.findById(lectureId).orElseThrow(() -> new CustomException(ExceptionType.LECTURE_NOT_FOUND_EXCEPTION));
        Homework homework = Homework.builder()
                .lecture(lecture)
                .title(homeworkSaveDto.getTitle())
                .description(homeworkSaveDto.getDescription())
                .isDraft(homeworkSaveDto.getIsDraft())
                .notified(false)
                .openTime(homeworkSaveDto.getOpenTime())
                .dueTime(homeworkSaveDto.getDueTime())
                .build();

        Long parentId = homeworkRepository.save(homework).getId();

        List<Attachment> attachments = new ArrayList<>();
        if (homeworkSaveDto.getFiles() != null) {
            for (MultipartFile multipartFile : homeworkSaveDto.getFiles()) {
                attachments.add(attachmentService.save(AttachmentType.HOMEWORK, parentId, multipartFile));
            }
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
                .title(homework.getTitle())
                .description(homework.getDescription())
                .fileNames(homework.getAttachments().stream().map(Attachment::getFileName).toList())
                .filePaths(homework.getAttachments().stream().map(Attachment::getFilePath).toList())
                .openTime(homework.getOpenTime())
                .dueTime(homework.getDueTime())
                .build();
    }

    public void update(Long homeworkId, HomeworkSaveDto homeworkSaveDto) throws IOException {
        Homework homework = homeworkRepository.findById(homeworkId)
                .orElseThrow(() -> new CustomException(ExceptionType.HOMEWORK_NOT_FOUND_EXCEPTION));

        if (homeworkSaveDto.getTitle() != null) {
            homework.setTitle(homeworkSaveDto.getTitle());
        }
        if (homeworkSaveDto.getDescription() != null) {
            homework.setDescription(homeworkSaveDto.getDescription());
        }
        List<Attachment> existingAttachments = homework.getAttachments();
        if (existingAttachments != null) {
            existingAttachments.clear();
        }
        if (homeworkSaveDto.getFiles() != null) {
            for (MultipartFile multipartFile : homeworkSaveDto.getFiles()) {
                Attachment newAttachment = attachmentService.save(AttachmentType.HOMEWORK, homeworkId, multipartFile);
                existingAttachments.add(newAttachment);
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
