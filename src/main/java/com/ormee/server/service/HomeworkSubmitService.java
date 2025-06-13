package com.ormee.server.service;

import com.ormee.server.dto.homework.HomeworkSubmitDto;
import com.ormee.server.dto.homework.HomeworkSubmitSaveDto;
import com.ormee.server.dto.homework.HomeworkSubmitStudentDto;
import com.ormee.server.exception.CustomException;
import com.ormee.server.exception.ExceptionType;
import com.ormee.server.model.*;
import com.ormee.server.model.member.Member;
import com.ormee.server.model.member.Role;
import com.ormee.server.repository.HomeworkRepository;
import com.ormee.server.repository.HomeworkSubmitRepository;
import com.ormee.server.repository.MemberRepository;
import com.ormee.server.service.attachment.AttachmentService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class HomeworkSubmitService {
    private final HomeworkSubmitRepository homeworkSubmitRepository;
    private final HomeworkRepository homeworkRepository;
    private final MemberRepository memberRepository;
    private final AttachmentService attachmentService;

    public HomeworkSubmitService(HomeworkSubmitRepository homeworkSubmitRepository, HomeworkRepository homeworkRepository, MemberRepository memberRepository, AttachmentService attachmentService) {
        this.homeworkSubmitRepository = homeworkSubmitRepository;
        this.homeworkRepository = homeworkRepository;
        this.memberRepository = memberRepository;
        this.attachmentService = attachmentService;
    }

    public void create(Long homeworkId, HomeworkSubmitSaveDto homeworkSubmitSaveDto, String username) throws IOException {
        Homework homework = homeworkRepository.findById(homeworkId).orElseThrow(() -> new CustomException(ExceptionType.HOMEWORK_NOT_FOUND_EXCEPTION));
        Member student = memberRepository.findByUsername(username).orElseThrow(() -> new CustomException(ExceptionType.MEMBER_NOT_FOUND_EXCEPTION));

        HomeworkSubmit homeworkSubmit = HomeworkSubmit.builder()
                .homework(homework)
                .student(student)
                .content(homeworkSubmitSaveDto.getContent())
                .isChecked(false)
                .isFeedback(false)
                .build();

        Long parentId = homeworkSubmitRepository.save(homeworkSubmit).getId();

        List<Attachment> attachments = new ArrayList<>();

        if (homeworkSubmitSaveDto.getFiles() != null) {
            for (MultipartFile multipartFile : homeworkSubmitSaveDto.getFiles()) {
                attachments.add(attachmentService.save(AttachmentType.HOMEWORK_SUBMIT, parentId, multipartFile));
            }
        }

        homeworkSubmit.setAttachments(attachments);

        homeworkSubmitRepository.save(homeworkSubmit);
    }

    public List<HomeworkSubmitStudentDto> getStudents(Long homeworkId) {
        Homework homework = homeworkRepository.findById(homeworkId).orElseThrow(() -> new CustomException(ExceptionType.HOMEWORK_NOT_FOUND_EXCEPTION));
        List<Member> students = homework.getLecture().getStudentLectures().stream().map(StudentLecture::getStudent).sorted(Comparator.comparing(Member::getName)).toList();

        List<HomeworkSubmit> submits = homeworkSubmitRepository.findAllByHomeworkOrderByStudent_Name(homework);

        return students.stream()
                .map(student -> {
                    HomeworkSubmit submit = submits.stream()
                            .filter(s -> s.getStudent().getId().equals(student.getId()))
                            .findFirst()
                            .orElse(null);

                    return HomeworkSubmitStudentDto.builder()
                            .homeworkSubmitId(submit != null ? submit.getId() : null)
                            .studentName(student.getName())
                            .isSubmitted(submit != null)
                            .isChecked(submit != null ? submit.getIsChecked() : null)
                            .createdAt(submit != null ? submit.getCreatedAt().toString() : null)
                            .build();
                })
                .toList();
    }

    public List<HomeworkSubmitStudentDto> getNotSubmittedStudents(Long homeworkId) {
        Homework homework = homeworkRepository.findById(homeworkId)
                .orElseThrow(() -> new CustomException(ExceptionType.HOMEWORK_NOT_FOUND_EXCEPTION));

        List<Member> students = homework.getLecture()
                .getStudentLectures()
                .stream()
                .map(StudentLecture::getStudent)
                .toList();

        List<Long> submittedStudentIds = homeworkSubmitRepository
                .findAllByHomework(homework)
                .stream()
                .map(submit -> submit.getStudent().getId())
                .toList();

        return students.stream()
                .filter(student -> !submittedStudentIds.contains(student.getId()))
                .sorted(Comparator.comparing(Member::getName))
                .map(student -> HomeworkSubmitStudentDto.builder()
                        .studentName(student.getName())
                        .isSubmitted(false)
                        .isChecked(null)
                        .createdAt(null)
                        .build()
                )
                .toList();
    }

    public List<HomeworkSubmitStudentDto> getNotCheckedStudents(Long homeworkId) {
        Homework homework = homeworkRepository.findById(homeworkId).orElseThrow(() -> new CustomException(ExceptionType.HOMEWORK_NOT_FOUND_EXCEPTION));

        return homeworkSubmitRepository.findAllByHomeworkAndIsCheckedFalse(homework)
                .stream()
                .map(submit -> HomeworkSubmitStudentDto.builder()
                        .homeworkSubmitId(submit.getId())
                        .studentName(submit.getStudent().getName())
                        .isSubmitted(true)
                        .isChecked(false)
                        .createdAt(submit.getCreatedAt().toString())
                        .build()
                )
                .sorted(Comparator.comparing(HomeworkSubmitStudentDto::getStudentName))
                .toList();
    }


    public HomeworkSubmitDto get(Long homeworkSubmitId, String username) {
        HomeworkSubmit homeworkSubmit = homeworkSubmitRepository.findById(homeworkSubmitId).orElseThrow(() -> new CustomException(ExceptionType.SUBMIT_NOT_FOUND_EXCEPTION));
        Member member = memberRepository.findByUsername(username).orElseThrow(() -> new CustomException(ExceptionType.MEMBER_NOT_FOUND_EXCEPTION));

        checkedByTeacher(member, homeworkSubmit);

        return HomeworkSubmitDto.builder()
                .name(homeworkSubmit.getStudent().getName())
                .content(homeworkSubmit.getContent())
                .filePaths(homeworkSubmit.getAttachments().stream().map(Attachment::getFilePath).toList())
                .createdAt(String.valueOf(homeworkSubmit.getCreatedAt()))
                .build();
    }

    public void checkedByTeacher(Member member, HomeworkSubmit homeworkSubmit) {
        if(member.getRole() == Role.TEACHER) {
            homeworkSubmit.setIsChecked(true);
            homeworkSubmitRepository.save(homeworkSubmit);
        }
    }

    public List<HomeworkSubmitStudentDto> getSubmitStudents(Long homeworkId) {
        Homework homework = homeworkRepository.findById(homeworkId).orElseThrow(() -> new CustomException(ExceptionType.HOMEWORK_NOT_FOUND_EXCEPTION));

        return homeworkSubmitRepository.findAllByHomeworkOrderByCreatedAtDesc(homework)
                .stream()
                .map(submit -> HomeworkSubmitStudentDto.builder()
                        .homeworkSubmitId(submit.getId())
                        .studentName(submit.getStudent().getName())
                        .isFeedback(submit.getIsFeedback() != null && submit.getIsFeedback())
                        .createdAt(submit.getCreatedAt().toString())
                        .build()
                )
                .toList();
    }
}
