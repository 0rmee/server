package com.ormee.server.service;

import com.ormee.server.dto.assignment.AssignmentSubmitDto;
import com.ormee.server.dto.assignment.AssignmentSubmitSaveDto;
import com.ormee.server.dto.assignment.AssignmentSubmitStudentDto;
import com.ormee.server.exception.CustomException;
import com.ormee.server.exception.ExceptionType;
import com.ormee.server.model.*;
import com.ormee.server.repository.AssignmentRepository;
import com.ormee.server.repository.AssignmentSubmitRepository;
import com.ormee.server.repository.StudentRepository;
import com.ormee.server.service.attachment.AttachmentService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AssignmentSubmitService {
    private final AssignmentSubmitRepository assignmentSubmitRepository;
    private final AssignmentRepository assignmentRepository;
    private final StudentRepository studentRepository;
    private AttachmentService attachmentService;

    public AssignmentSubmitService(AssignmentSubmitRepository assignmentSubmitRepository, AssignmentRepository assignmentRepository, StudentRepository studentRepository) {
        this.assignmentSubmitRepository = assignmentSubmitRepository;
        this.assignmentRepository = assignmentRepository;
        this.studentRepository = studentRepository;
    }

    public void create(Long assignmentId, AssignmentSubmitSaveDto assignmentSubmitSaveDto, String email) throws IOException {
        Assignment assignment = assignmentRepository.findById(assignmentId).orElseThrow(() -> new CustomException(ExceptionType.ASSIGNMENT_NOT_FOUND_EXCEPTION));
        Student student = studentRepository.findByEmail(email).orElseThrow(() -> new CustomException(ExceptionType.STUDENT_NOT_FOUND_EXCEPTION));

        AssignmentSubmit assignmentSubmit = AssignmentSubmit.builder()
                .assignment(assignment)
                .student(student)
                .content(assignmentSubmitSaveDto.getContent())
                .isFeedback(false)
                .build();

        Long parentId = assignmentSubmitRepository.save(assignmentSubmit).getId();

        List<Attachment> attachments = new ArrayList<>();
        for(MultipartFile multipartFile : assignmentSubmitSaveDto.getFiles()) {
            attachments.add(attachmentService.save(AttachmentType.assignment_submit, parentId, multipartFile));
        }
        assignmentSubmit.setAttachments(attachments);

        assignmentSubmitRepository.save(assignmentSubmit);
    }

    public List<AssignmentSubmitStudentDto> getStudentList(Long assignmentId) {
        Assignment assignment = assignmentRepository.findById(assignmentId).orElseThrow(() -> new CustomException(ExceptionType.ASSIGNMENT_NOT_FOUND_EXCEPTION));
        List<AssignmentSubmit> assignmentSubmits = assignmentSubmitRepository.findAllByAssignmentOrderByStudent_Name(assignment);

        return assignmentSubmits.stream()
                .map(assignmentSubmit -> AssignmentSubmitStudentDto.builder()
                        .assignmentSubmitId(assignmentSubmit.getId())
                        .studentName(assignmentSubmit.getStudent().getName())
                        .build())
                .collect(Collectors.toList());
    }

    public AssignmentSubmitDto get(Long assignmentSubmitId) {
        AssignmentSubmit assignmentSubmit = assignmentSubmitRepository.findById(assignmentSubmitId).orElseThrow(() -> new CustomException(ExceptionType.SUBMIT_NOT_FOUND_EXCEPTION));

        return AssignmentSubmitDto.builder()
                .name(assignmentSubmit.getStudent().getName())
                .content(assignmentSubmit.getContent())
                .filePaths(assignmentSubmit.getAttachments().stream().map(Attachment::getFilePath).toList())
                .createdAt(String.valueOf(assignmentSubmit.getCreatedAt()))
                .build();
    }
}
