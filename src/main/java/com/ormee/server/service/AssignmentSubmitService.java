package com.ormee.server.service;

import com.ormee.server.dto.assignment.AssignmentSubmitDto;
import com.ormee.server.dto.assignment.AssignmentSubmitSaveDto;
import com.ormee.server.dto.assignment.AssignmentSubmitStudentDto;
import com.ormee.server.exception.CustomException;
import com.ormee.server.exception.ExceptionType;
import com.ormee.server.model.Assignment;
import com.ormee.server.model.AssignmentSubmit;
import com.ormee.server.model.Student;
import com.ormee.server.repository.AssignmentRepository;
import com.ormee.server.repository.AssignmentSubmitRepository;
import com.ormee.server.repository.StudentRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AssignmentSubmitService {
    private final AssignmentSubmitRepository assignmentSubmitRepository;
    private final AssignmentRepository assignmentRepository;
    private final StudentRepository studentRepository;

    public AssignmentSubmitService(AssignmentSubmitRepository assignmentSubmitRepository, AssignmentRepository assignmentRepository, StudentRepository studentRepository) {
        this.assignmentSubmitRepository = assignmentSubmitRepository;
        this.assignmentRepository = assignmentRepository;
        this.studentRepository = studentRepository;
    }

    public void create(Long assignmentId, AssignmentSubmitSaveDto assignmentSubmitSaveDto, String email) {
        Assignment assignment = assignmentRepository.findById(assignmentId).orElseThrow(() -> new CustomException(ExceptionType.ASSIGNMENT_NOT_FOUND_EXCEPTION));
        Student student = studentRepository.findByEmail(email).orElseThrow(() -> new CustomException(ExceptionType.STUDENT_NOT_FOUND_EXCEPTION));

        AssignmentSubmit assignmentSubmit = AssignmentSubmit.builder()
                .assignment(assignment)
                .student(student)
                .content(assignmentSubmitSaveDto.getContent())
                .isFeedback(false)
                .build();

        assignmentSubmitRepository.save(assignmentSubmit);
    }

    public List<AssignmentSubmitStudentDto> getStudentList(Long assignmentId) {
        Assignment assignment = assignmentRepository.findById(assignmentId).orElseThrow(() -> new CustomException(ExceptionType.ASSIGNMENT_NOT_FOUND_EXCEPTION));
        List<AssignmentSubmit> assignmentSubmits = assignmentSubmitRepository.findAllByAssignmentOrderByStudent_Name(assignment);

        List<AssignmentSubmitStudentDto> assignmentSubmitStudentDtos = assignmentSubmits.stream()
                .map(assignmentSubmit -> AssignmentSubmitStudentDto.builder()
                        .assignmentSubmitId(assignmentSubmit.getId())
                        .studentName(assignmentSubmit.getStudent().getName())
                        .build())
                .collect(Collectors.toList());

        return assignmentSubmitStudentDtos;
    }

    public AssignmentSubmitDto get(Long assignmentSubmitId) {
        AssignmentSubmit assignmentSubmit = assignmentSubmitRepository.findById(assignmentSubmitId).orElseThrow(() -> new CustomException(ExceptionType.ASSIGNMENT_SUBMIT_NOT_FOUND_EXCEPTION));

        return AssignmentSubmitDto.builder()
                .name(assignmentSubmit.getStudent().getName())
                .content(assignmentSubmit.getContent())
                .createdAt(String.valueOf(assignmentSubmit.getCreatedAt()))
                .build();
    }
}
