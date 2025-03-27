package com.ormee.server.service;

import com.ormee.server.dto.assignment.AssignmentDto;
import com.ormee.server.dto.assignment.AssignmentSaveDto;
import com.ormee.server.dto.assignment.FeedbackedAssignmentListDto;
import com.ormee.server.exception.CustomException;
import com.ormee.server.exception.ExceptionType;
import com.ormee.server.model.Assignment;
import com.ormee.server.model.Lecture;
import com.ormee.server.repository.AssignmentRepository;
import com.ormee.server.repository.AssignmentSubmitRepository;
import com.ormee.server.repository.LectureRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AssignmentService {
    private final AssignmentRepository assignmentRepository;
    private final LectureRepository lectureRepository;
    private final AssignmentSubmitRepository assignmentSubmitRepository;

    public AssignmentService(AssignmentRepository assignmentRepository, LectureRepository lectureRepository, AssignmentSubmitRepository assignmentSubmitRepository) {
        this.assignmentRepository = assignmentRepository;
        this.lectureRepository = lectureRepository;
        this.assignmentSubmitRepository = assignmentSubmitRepository;
    }

    public void create(UUID lectureId, AssignmentSaveDto assignmentSaveDto) {
        Lecture lecture = lectureRepository.findById(lectureId).orElseThrow(() -> new CustomException(ExceptionType.LECTURE_NOT_FOUND_EXCEPTION));
        Assignment assignment = Assignment.builder()
                .lecture(lecture)
                .title(assignmentSaveDto.getTitle())
                .description(assignmentSaveDto.getDescription())
                .isDraft(assignmentSaveDto.getIsDraft())
                .openTime(assignmentSaveDto.getOpenTime())
                .dueTime(assignmentSaveDto.getDueTime())
                .build();
        assignmentRepository.save(assignment);
    }

    public List<AssignmentDto> getList(UUID lectureId) {
        Lecture lecture = lectureRepository.findById(lectureId).orElseThrow(() -> new CustomException(ExceptionType.LECTURE_NOT_FOUND_EXCEPTION));
        List<Assignment> assignments = assignmentRepository.findAllByLectureOrderByCreatedAtDesc(lecture);
        return assignments.stream()
                .map(assignment -> AssignmentDto.builder()
                        .title(assignment.getTitle())
                        .openTime(assignment.getOpenTime())
                        .dueTime(assignment.getDueTime())
                        .build())
                .collect(Collectors.toList());
    }

    public FeedbackedAssignmentListDto getFeedbackCompletedList(UUID lectureId) {
        Lecture lecture = lectureRepository.findById(lectureId).orElseThrow(() -> new CustomException(ExceptionType.LECTURE_NOT_FOUND_EXCEPTION));
        List<Assignment> assignments = assignmentRepository.findAllByLectureOrderByCreatedAtDesc(lecture);
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
                .openTime(assignment.getOpenTime())
                .dueTime(assignment.getDueTime())
                .build();
    }

    public void update(Long assignmentId, AssignmentSaveDto assignmentSaveDto) {
        Assignment assignment = assignmentRepository.findById(assignmentId).orElseThrow(() -> new CustomException(ExceptionType.ASSIGNMENT_NOT_FOUND_EXCEPTION));

        assignment.setTitle(assignmentSaveDto.getTitle());
        assignment.setDescription(assignmentSaveDto.getDescription());

        assignmentRepository.save(assignment);
    }

    public void delete(Long assignmentId) {
        Assignment assignment = assignmentRepository.findById(assignmentId).orElseThrow(() -> new CustomException(ExceptionType.ASSIGNMENT_NOT_FOUND_EXCEPTION));
        assignmentRepository.delete(assignment);
    }
}
