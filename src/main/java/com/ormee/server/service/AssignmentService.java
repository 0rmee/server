package com.ormee.server.service;

import com.ormee.server.dto.assignment.AssignmentDto;
import com.ormee.server.dto.assignment.AssignmentListDto;
import com.ormee.server.dto.assignment.AssignmentSaveDto;
import com.ormee.server.dto.assignment.FeedbackedAssignmentListDto;
import com.ormee.server.exception.CustomException;
import com.ormee.server.exception.ExceptionType;
import com.ormee.server.model.Assignment;
import com.ormee.server.model.Attachment;
import com.ormee.server.model.AttachmentType;
import com.ormee.server.model.Lecture;
import com.ormee.server.repository.AssignmentRepository;
import com.ormee.server.repository.AssignmentSubmitRepository;
import com.ormee.server.repository.LectureRepository;
import com.ormee.server.service.attachment.AttachmentService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class AssignmentService {
    private final AssignmentRepository assignmentRepository;
    private final LectureRepository lectureRepository;
    private final AssignmentSubmitRepository assignmentSubmitRepository;
    private final AttachmentService attachmentService;

    public AssignmentService(AssignmentRepository assignmentRepository, LectureRepository lectureRepository, AssignmentSubmitRepository assignmentSubmitRepository, AttachmentService attachmentService) {
        this.assignmentRepository = assignmentRepository;
        this.lectureRepository = lectureRepository;
        this.assignmentSubmitRepository = assignmentSubmitRepository;
        this.attachmentService = attachmentService;
    }

    public void create(Long lectureId, AssignmentSaveDto assignmentSaveDto) throws IOException {
        Lecture lecture = lectureRepository.findById(lectureId).orElseThrow(() -> new CustomException(ExceptionType.LECTURE_NOT_FOUND_EXCEPTION));
        Assignment assignment = Assignment.builder()
                .lecture(lecture)
                .title(assignmentSaveDto.getTitle())
                .description(assignmentSaveDto.getDescription())
                .isDraft(assignmentSaveDto.getIsDraft())
                .notified(false)
                .openTime(assignmentSaveDto.getOpenTime())
                .dueTime(assignmentSaveDto.getDueTime())
                .build();

        Long parentId = assignmentRepository.save(assignment).getId();

        List<Attachment> attachments = new ArrayList<>();
        if (assignmentSaveDto.getFiles() != null) {
            for (MultipartFile multipartFile : assignmentSaveDto.getFiles()) {
                attachments.add(attachmentService.save(AttachmentType.ASSIGNMENT, parentId, multipartFile));
            }
        }
        assignment.setAttachments(attachments);

        assignmentRepository.save(assignment);
    }

    public AssignmentListDto getList(Long lectureId) {
        Lecture lecture = lectureRepository.findById(lectureId).orElseThrow(() -> new CustomException(ExceptionType.LECTURE_NOT_FOUND_EXCEPTION));
        List<Assignment> assignments = assignmentRepository.findAllByLectureAndIsDraftFalseOrderByCreatedAtDesc(lecture);

        List<AssignmentDto> openedAssignments = assignments.stream()
                .map(assignment -> AssignmentDto.builder()
                        .id(assignment.getId())
                        .title(assignment.getTitle())
                        .openTime(assignment.getOpenTime())
                        .dueTime(assignment.getDueTime())
                        .build()).toList();
        List<AssignmentDto> closedAssignments = assignments.stream()
                .map(assignment -> AssignmentDto.builder()
                        .id(assignment.getId())
                        .title(assignment.getTitle())
                        .openTime(assignment.getOpenTime())
                        .dueTime(assignment.getDueTime())
                        .build()).toList();

        return AssignmentListDto.builder()
                .openedAssignments(openedAssignments)
                .closedAssignments(closedAssignments)
                .build();
    }

    public List<AssignmentDto> getDrafts(Long lectureId) {
        Lecture lecture = lectureRepository.findById(lectureId).orElseThrow(() -> new CustomException(ExceptionType.LECTURE_NOT_FOUND_EXCEPTION));
        List<Assignment> assignments = assignmentRepository.findAllByLectureAndIsDraftTrueOrderByCreatedAtDesc(lecture);

        return assignments.stream().map(assignment -> AssignmentDto.builder()
                .id(assignment.getId())
                .title(assignment.getTitle())
                .openTime(assignment.getCreatedAt())
                .build()).toList();
    }

    public FeedbackedAssignmentListDto getFeedbackCompletedList(Long lectureId) {
        Lecture lecture = lectureRepository.findById(lectureId).orElseThrow(() -> new CustomException(ExceptionType.LECTURE_NOT_FOUND_EXCEPTION));
        List<Assignment> assignments = assignmentRepository.findAllByLectureAndIsDraftFalseOrderByCreatedAtDesc(lecture);
        List<AssignmentDto> feedbackCompletedAssignments = new ArrayList<>();
        List<AssignmentDto> feedbackNotCompletedAssignments = new ArrayList<>();

        for (Assignment assignment : assignments) {
            boolean hasFeedback = assignmentSubmitRepository.existsByAssignmentAndIsFeedback(assignment, true);
            AssignmentDto dto = AssignmentDto.builder()
                    .title(assignment.getTitle())
                    .openTime(assignment.getOpenTime())
                    .dueTime(assignment.getDueTime())
                    .build();
            if (hasFeedback) {
                feedbackCompletedAssignments.add(dto);
            } else {
                feedbackNotCompletedAssignments.add(dto);
            }
        }

        return FeedbackedAssignmentListDto.builder()
                .feedbackCompletedAssignments(feedbackCompletedAssignments)
                .feedbackNotCompletedAssignments(feedbackNotCompletedAssignments)
                .build();
    }

    public AssignmentDto get(Long assignmentId) {
        Assignment assignment = assignmentRepository.findById(assignmentId).orElseThrow(() -> new CustomException(ExceptionType.ASSIGNMENT_NOT_FOUND_EXCEPTION));

        return AssignmentDto.builder()
                .title(assignment.getTitle())
                .description(assignment.getDescription())
                .filePaths(assignment.getAttachments().stream().map(Attachment::getFilePath).toList())
                .openTime(assignment.getOpenTime())
                .dueTime(assignment.getDueTime())
                .build();
    }

    public void update(Long assignmentId, AssignmentSaveDto assignmentSaveDto) throws IOException {
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new CustomException(ExceptionType.ASSIGNMENT_NOT_FOUND_EXCEPTION));

        if (assignmentSaveDto.getTitle() != null) {
            assignment.setTitle(assignmentSaveDto.getTitle());
        }
        if (assignmentSaveDto.getDescription() != null) {
            assignment.setDescription(assignmentSaveDto.getDescription());
        }
        List<Attachment> existingAttachments = assignment.getAttachments();
        if (existingAttachments != null) {
            existingAttachments.clear();
        }
        if (assignmentSaveDto.getFiles() != null) {
            for (MultipartFile multipartFile : assignmentSaveDto.getFiles()) {
                Attachment newAttachment = attachmentService.save(AttachmentType.ASSIGNMENT, assignmentId, multipartFile);
                existingAttachments.add(newAttachment);
            }
        }
        assignment.setIsDraft(assignmentSaveDto.getIsDraft());

        assignmentRepository.save(assignment);
    }


    public void delete(Long assignmentId) {
        Assignment assignment = assignmentRepository.findById(assignmentId).orElseThrow(() -> new CustomException(ExceptionType.ASSIGNMENT_NOT_FOUND_EXCEPTION));
        assignmentRepository.delete(assignment);
    }
}
