package com.ormee.server.service;

import com.ormee.server.dto.assignment.AssignmentSubmitDto;
import com.ormee.server.dto.assignment.AssignmentSubmitSaveDto;
import com.ormee.server.dto.assignment.AssignmentSubmitStudentDto;
import com.ormee.server.exception.CustomException;
import com.ormee.server.exception.ExceptionType;
import com.ormee.server.model.*;
import com.ormee.server.model.member.Member;
import com.ormee.server.model.member.Role;
import com.ormee.server.repository.AssignmentRepository;
import com.ormee.server.repository.AssignmentSubmitRepository;
import com.ormee.server.repository.MemberRepository;
import com.ormee.server.service.attachment.AttachmentService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class AssignmentSubmitService {
    private final AssignmentSubmitRepository assignmentSubmitRepository;
    private final AssignmentRepository assignmentRepository;
    private final MemberRepository memberRepository;
    private final AttachmentService attachmentService;

    public AssignmentSubmitService(AssignmentSubmitRepository assignmentSubmitRepository, AssignmentRepository assignmentRepository, MemberRepository memberRepository, AttachmentService attachmentService) {
        this.assignmentSubmitRepository = assignmentSubmitRepository;
        this.assignmentRepository = assignmentRepository;
        this.memberRepository = memberRepository;
        this.attachmentService = attachmentService;
    }

    public void create(Long assignmentId, AssignmentSubmitSaveDto assignmentSubmitSaveDto, String username) throws IOException {
        Assignment assignment = assignmentRepository.findById(assignmentId).orElseThrow(() -> new CustomException(ExceptionType.ASSIGNMENT_NOT_FOUND_EXCEPTION));
        Member student = memberRepository.findByUsername(username).orElseThrow(() -> new CustomException(ExceptionType.MEMBER_NOT_FOUND_EXCEPTION));

        AssignmentSubmit assignmentSubmit = AssignmentSubmit.builder()
                .assignment(assignment)
                .student(student)
                .content(assignmentSubmitSaveDto.getContent())
                .isChecked(false)
                .isFeedback(false)
                .build();

        Long parentId = assignmentSubmitRepository.save(assignmentSubmit).getId();

        List<Attachment> attachments = new ArrayList<>();

        if (assignmentSubmitSaveDto.getFiles() != null) {
            for (MultipartFile multipartFile : assignmentSubmitSaveDto.getFiles()) {
                attachments.add(attachmentService.save(AttachmentType.ASSIGNMENT_SUBMIT, parentId, multipartFile));
            }
        }

        assignmentSubmit.setAttachments(attachments);

        assignmentSubmitRepository.save(assignmentSubmit);
    }

    public List<AssignmentSubmitStudentDto> getStudents(Long assignmentId) {
        Assignment assignment = assignmentRepository.findById(assignmentId).orElseThrow(() -> new CustomException(ExceptionType.ASSIGNMENT_NOT_FOUND_EXCEPTION));
        List<Member> students = assignment.getLecture().getStudentLectures().stream().map(StudentLecture::getStudent).sorted(Comparator.comparing(Member::getName)).toList();

        List<AssignmentSubmit> submits = assignmentSubmitRepository.findAllByAssignmentOrderByStudent_Name(assignment);

        return students.stream()
                .map(student -> {
                    AssignmentSubmit submit = submits.stream()
                            .filter(s -> s.getStudent().getId().equals(student.getId()))
                            .findFirst()
                            .orElse(null);

                    return AssignmentSubmitStudentDto.builder()
                            .assignmentSubmitId(submit != null ? submit.getId() : null)
                            .studentName(student.getName())
                            .isSubmitted(submit != null)
                            .isChecked(submit != null ? submit.getIsChecked() : null)
                            .createdAt(submit != null ? submit.getCreatedAt().toString() : null)
                            .build();
                })
                .toList();
    }

    public List<AssignmentSubmitStudentDto> getNotSubmittedStudents(Long assignmentId) {
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new CustomException(ExceptionType.ASSIGNMENT_NOT_FOUND_EXCEPTION));

        List<Member> students = assignment.getLecture()
                .getStudentLectures()
                .stream()
                .map(StudentLecture::getStudent)
                .toList();

        List<Long> submittedStudentIds = assignmentSubmitRepository
                .findAllByAssignment(assignment)
                .stream()
                .map(submit -> submit.getStudent().getId())
                .toList();

        return students.stream()
                .filter(student -> !submittedStudentIds.contains(student.getId()))
                .sorted(Comparator.comparing(Member::getName))
                .map(student -> AssignmentSubmitStudentDto.builder()
                        .studentName(student.getName())
                        .isSubmitted(false)
                        .isChecked(null)
                        .createdAt(null)
                        .build()
                )
                .toList();
    }

    public List<AssignmentSubmitStudentDto> getNotCheckedStudents(Long assignmentId) {
        Assignment assignment = assignmentRepository.findById(assignmentId).orElseThrow(() -> new CustomException(ExceptionType.ASSIGNMENT_NOT_FOUND_EXCEPTION));

        return assignmentSubmitRepository.findAllByAssignmentAndIsCheckedFalse(assignment)
                .stream()
                .map(submit -> AssignmentSubmitStudentDto.builder()
                        .assignmentSubmitId(submit.getId())
                        .studentName(submit.getStudent().getName())
                        .isSubmitted(true)
                        .isChecked(false)
                        .createdAt(submit.getCreatedAt().toString())
                        .build()
                )
                .sorted(Comparator.comparing(AssignmentSubmitStudentDto::getStudentName))
                .toList();
    }


    public AssignmentSubmitDto get(Long assignmentSubmitId, String username) {
        AssignmentSubmit assignmentSubmit = assignmentSubmitRepository.findById(assignmentSubmitId).orElseThrow(() -> new CustomException(ExceptionType.SUBMIT_NOT_FOUND_EXCEPTION));
        Member member = memberRepository.findByUsername(username).orElseThrow(() -> new CustomException(ExceptionType.MEMBER_NOT_FOUND_EXCEPTION));

        checkedByTeacher(member, assignmentSubmit);

        return AssignmentSubmitDto.builder()
                .name(assignmentSubmit.getStudent().getName())
                .content(assignmentSubmit.getContent())
                .filePaths(assignmentSubmit.getAttachments().stream().map(Attachment::getFilePath).toList())
                .createdAt(String.valueOf(assignmentSubmit.getCreatedAt()))
                .build();
    }

    public void checkedByTeacher(Member member, AssignmentSubmit assignmentSubmit) {
        if(member.getRole() == Role.TEACHER) {
            assignmentSubmit.setIsChecked(true);
            assignmentSubmitRepository.save(assignmentSubmit);
        }
    }

    public List<AssignmentSubmitStudentDto> getSubmitStudents(Long assignmentId) {
        Assignment assignment = assignmentRepository.findById(assignmentId).orElseThrow(() -> new CustomException(ExceptionType.ASSIGNMENT_NOT_FOUND_EXCEPTION));

        return assignmentSubmitRepository.findAllByAssignmentOrderByCreatedAtDesc(assignment)
                .stream()
                .map(submit -> AssignmentSubmitStudentDto.builder()
                        .assignmentSubmitId(submit.getId())
                        .studentName(submit.getStudent().getName())
                        .isFeedback(submit.getIsFeedback() != null && submit.getIsFeedback())
                        .createdAt(submit.getCreatedAt().toString())
                        .build()
                )
                .toList();
    }
}
