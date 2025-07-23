package com.ormee.server.homework.service;

import com.ormee.server.attachment.domain.Attachment;
import com.ormee.server.attachment.repository.AttachmentRepository;
import com.ormee.server.attachment.service.AttachmentService;
import com.ormee.server.homework.domain.Homework;
import com.ormee.server.homework.domain.HomeworkSubmit;
import com.ormee.server.homework.dto.HomeworkSubmitDto;
import com.ormee.server.homework.dto.HomeworkSubmitSaveDto;
import com.ormee.server.homework.dto.HomeworkSubmitStudentDto;
import com.ormee.server.global.exception.CustomException;
import com.ormee.server.global.exception.ExceptionType;
import com.ormee.server.homework.repository.HomeworkRepository;
import com.ormee.server.homework.repository.HomeworkSubmitRepository;
import com.ormee.server.lecture.domain.StudentLecture;
import com.ormee.server.member.domain.Member;
import com.ormee.server.member.domain.Role;
import com.ormee.server.member.repository.MemberRepository;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class HomeworkSubmitService {
    private final HomeworkSubmitRepository homeworkSubmitRepository;
    private final HomeworkRepository homeworkRepository;
    private final MemberRepository memberRepository;
    private final AttachmentRepository attachmentRepository;
    private final AttachmentService attachmentService;

    public HomeworkSubmitService(HomeworkSubmitRepository homeworkSubmitRepository, HomeworkRepository homeworkRepository, MemberRepository memberRepository, AttachmentRepository attachmentRepository, AttachmentService attachmentService) {
        this.homeworkSubmitRepository = homeworkSubmitRepository;
        this.homeworkRepository = homeworkRepository;
        this.memberRepository = memberRepository;
        this.attachmentRepository = attachmentRepository;
        this.attachmentService = attachmentService;
    }

    public void submit(Long homeworkId, HomeworkSubmitSaveDto homeworkSubmitSaveDto, String username) {
        Homework homework = homeworkRepository.findById(homeworkId).orElseThrow(() -> new CustomException(ExceptionType.HOMEWORK_NOT_FOUND_EXCEPTION));
        Member student = memberRepository.findByUsername(username).orElseThrow(() -> new CustomException(ExceptionType.MEMBER_NOT_FOUND_EXCEPTION));

        if(homeworkSubmitRepository.existsByHomeworkAndStudent(homework, student)) {
            throw new CustomException(ExceptionType.SUBMISSION_ALREADY_EXIST_EXCEPTION);
        }

        HomeworkSubmit homeworkSubmit = HomeworkSubmit.builder()
                .homework(homework)
                .student(student)
                .content(homeworkSubmitSaveDto.getContent())
                .isChecked(false)
                .isFeedback(false)
                .build();

        homeworkSubmit = homeworkSubmitRepository.save(homeworkSubmit);

        List<Attachment> attachments = homeworkSubmitSaveDto.getFileIds().stream()
                .map(id -> attachmentRepository.findById(id)
                        .orElseThrow(() -> new CustomException(ExceptionType.ATTACHMENT_NOT_FOUND_EXCEPTION)))
                .collect(Collectors.toList());

        for (Attachment attachment : attachments) {
            attachment.setParentId(String.valueOf(homeworkSubmit.getId()));
            attachmentRepository.save(attachment);
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
                            .studentName(student.getName() + student.getPhoneNumber().substring(student.getPhoneNumber().length() - 4))
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
                        .studentName(student.getName() + student.getPhoneNumber().substring(student.getPhoneNumber().length() - 4))
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
                        .studentName(submit.getStudent().getName() + submit.getStudent().getPhoneNumber().substring(submit.getStudent().getPhoneNumber().length() - 4))
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
                .id(homeworkSubmitId)
                .name(homeworkSubmit.getStudent().getName() + homeworkSubmit.getStudent().getPhoneNumber().substring(homeworkSubmit.getStudent().getPhoneNumber().length() - 4))
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
                        .studentName(submit.getStudent().getName() + submit.getStudent().getPhoneNumber().substring(submit.getStudent().getPhoneNumber().length() - 4))
                        .isFeedback(submit.getIsFeedback() != null && submit.getIsFeedback())
                        .createdAt(submit.getCreatedAt().toString())
                        .build()
                )
                .toList();
    }

    public HomeworkSubmitDto findByStudentAndHomework(Long homeworkId, String username) {
        Homework homework = homeworkRepository.findById(homeworkId).orElseThrow(() -> new CustomException(ExceptionType.HOMEWORK_NOT_FOUND_EXCEPTION));
        Member student = memberRepository.findByUsername(username).orElseThrow(() -> new CustomException(ExceptionType.MEMBER_NOT_FOUND_EXCEPTION));

        HomeworkSubmit homeworkSubmit = homeworkSubmitRepository.findByHomeworkAndStudent(homework, student).orElseThrow(() -> new CustomException(ExceptionType.SUBMIT_NOT_FOUND_EXCEPTION));

        return get(homeworkSubmit.getId(), username);
    }

    public void deleteAllByHomework(Homework homework) {
        List<HomeworkSubmit> submissions = homeworkSubmitRepository.findAllByHomework(homework);
        submissions.forEach(this::delete);
    }

    public void delete(HomeworkSubmit homeworkSubmit) {
        for (Attachment attachment : homeworkSubmit.getAttachments()) {
            attachmentService.delete(attachment.getId());
        }

        homeworkSubmitRepository.delete(homeworkSubmit);
    }
}
