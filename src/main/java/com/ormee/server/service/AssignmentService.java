package com.ormee.server.service;

import com.ormee.server.dto.assignment.AssignmentDto;
import com.ormee.server.dto.assignment.AssignmentSaveDto;
import com.ormee.server.exception.CustomException;
import com.ormee.server.exception.ExceptionType;
import com.ormee.server.model.Assignment;
import com.ormee.server.model.Lecture;
import com.ormee.server.repository.AssignmentRepository;
import com.ormee.server.repository.LectureRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AssignmentService {
    private final AssignmentRepository assignmentRepository;
    private final LectureRepository lectureRepository;

    public AssignmentService(AssignmentRepository assignmentRepository, LectureRepository lectureRepository) {
        this.assignmentRepository = assignmentRepository;
        this.lectureRepository = lectureRepository;
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
