package com.ormee.server.question.service;

import com.ormee.server.attachment.domain.Attachment;
import com.ormee.server.attachment.repository.AttachmentRepository;
import com.ormee.server.member.domain.Role;
import com.ormee.server.question.domain.Question;
import com.ormee.server.question.repository.QuestionRepository;
import com.ormee.server.question.dto.QuestionDto;
import com.ormee.server.question.dto.QuestionSaveDto;
import com.ormee.server.global.response.PageResponseDto;
import com.ormee.server.global.exception.CustomException;
import com.ormee.server.global.exception.ExceptionType;
import com.ormee.server.lecture.domain.Lecture;
import com.ormee.server.member.domain.Member;
import com.ormee.server.notification.service.NotificationService;
import com.ormee.server.notification.domain.NotificationType;
import com.ormee.server.lecture.repository.LectureRepository;
import com.ormee.server.member.repository.MemberRepository;
import com.ormee.server.attachment.service.AttachmentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class QuestionService {
    private final QuestionRepository questionRepository;
    private final LectureRepository lectureRepository;
    private final MemberRepository memberRepository;
    private final AttachmentRepository attachmentRepository;
    private final AnswerService answerService;
    private final AttachmentService attachmentService;
    private final NotificationService notificationService;

    public QuestionService(QuestionRepository questionRepository, LectureRepository lectureRepository, MemberRepository memberRepository, AttachmentRepository attachmentRepository, AnswerService answerService, AttachmentService attachmentService, NotificationService notificationService) {
        this.questionRepository = questionRepository;
        this.lectureRepository = lectureRepository;
        this.memberRepository = memberRepository;
        this.attachmentRepository = attachmentRepository;
        this.answerService = answerService;
        this.attachmentService = attachmentService;
        this.notificationService = notificationService;
    }

    public void saveQuestion(Long lectureId, QuestionSaveDto questionSaveDto, String username) {
        Lecture lecture = lectureRepository.findById(lectureId).orElseThrow(() -> new CustomException(ExceptionType.LECTURE_NOT_FOUND_EXCEPTION));
        Member student = memberRepository.findByUsername(username).orElseThrow(() -> new CustomException(ExceptionType.MEMBER_NOT_FOUND_EXCEPTION));

        Question question = Question.builder()
                .lecture(lecture)
                .title(questionSaveDto.getTitle())
                .student(student)
                .content(questionSaveDto.getContent())
                .isLocked(questionSaveDto.getIsLocked())
                .isAnswered(false)
                .build();

        question = questionRepository.save(question);

        List<Attachment> attachments = questionSaveDto.getFileIds().stream()
                .map(id -> attachmentRepository.findById(id)
                        .orElseThrow(() -> new CustomException(ExceptionType.ATTACHMENT_NOT_FOUND_EXCEPTION)))
                .collect(Collectors.toList());

        for (Attachment attachment : attachments) {
            attachment.setParentId(question.getId().toString());
            question.addAttachment(attachment);
        }

        attachmentRepository.saveAll(attachments);
        questionRepository.save(question);
        notificationService.create(NotificationType.QUESTION, question);
    }

    public void modifyQuestion(Long questionId, QuestionSaveDto questionSaveDto) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new CustomException(ExceptionType.QUESTION_NOT_FOUND_EXCEPTION));

        if (questionSaveDto.getTitle() != null) {
            question.setTitle(questionSaveDto.getTitle());
        }
        if (questionSaveDto.getContent() != null) {
            question.setContent(questionSaveDto.getContent());
        }

        List<Long> fileIds = questionSaveDto.getFileIds() != null ? questionSaveDto.getFileIds() : List.of();

        List<Attachment> existingAttachments = question.getAttachments();

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
                attachment.setParentId(question.getId().toString());
                attachmentRepository.save(attachment);
            }
        }

        questionRepository.save(question);
    }

    public void deleteQuestion(Long questionId) {
        Question question = questionRepository.findById(questionId).orElseThrow(() -> new CustomException(ExceptionType.QUESTION_NOT_FOUND_EXCEPTION));

        answerService.deleteByQuestion(question);

        for (Attachment attachment : question.getAttachments()) {
            attachmentService.delete(attachment.getId());
        }
        questionRepository.delete(question);
    }

    private QuestionDto convertToDto(Question question) {
        Member student = question.getStudent();

        return QuestionDto.builder()
                .id(question.getId())
                .title(question.getTitle())
                .content(question.getContent())
                .isAnswered(question.getIsAnswered())
                .author(student.getName() + student.getPhoneNumber().substring(student.getPhoneNumber().length() - 4))
                .filePaths(question.getAttachments().stream().map(Attachment::getFilePath).toList())
                .createdAt(question.getCreatedAt().toString())
                .build();
    }

    private QuestionDto convertToDto(Question question, Member student) {
        return QuestionDto.builder()
                .id(question.getId())
                .title(question.getTitle())
                .content(question.getContent())
                .isMine(Objects.equals(student.getId(), question.getStudent().getId()))
                .isAnswered(question.getIsAnswered())
                .author(question.getStudent().getName())
                .filePaths(question.getAttachments().stream().map(Attachment::getFilePath).toList())
                .createdAt(question.getCreatedAt().toString())
                .build();
    }

    public QuestionDto findById(Long questionId) {
        Question question = questionRepository.findById(questionId).orElseThrow(() -> new CustomException(ExceptionType.QUESTION_NOT_FOUND_EXCEPTION));
        return convertToDto(question);
    }

    public QuestionDto findById(Long questionId, String username) {
        Question question = questionRepository.findById(questionId).orElseThrow(() -> new CustomException(ExceptionType.QUESTION_NOT_FOUND_EXCEPTION));
        Member student = memberRepository.findByUsernameAndRole(username, Role.STUDENT).orElseThrow(() -> new CustomException(ExceptionType.MEMBER_NOT_FOUND_EXCEPTION));
        return convertToDto(question, student);
    }

    public PageResponseDto<QuestionDto> getQuestions(Long lectureId, int page) {
        Lecture lecture = lectureRepository.findById(lectureId).orElseThrow(() -> new CustomException(ExceptionType.LECTURE_NOT_FOUND_EXCEPTION));

        Pageable pageable = PageRequest.of(page, 15, Sort.by("createdAt").descending());

        Page<Question> questionPage = questionRepository.findAllByLectureOrderByCreatedAtDesc(lecture, pageable);

        List<QuestionDto> content = questionPage.stream().map(this::convertToDto).toList();

        return PageResponseDto.<QuestionDto>builder()
                .content(content)
                .totalPages(questionPage.getTotalPages())
                .totalElements(questionPage.getTotalElements())
                .currentPage(questionPage.getNumber() + 1)
                .build();
    }

    public List<QuestionDto> getQuestions(Long lectureId, String username) {
        Lecture lecture = lectureRepository.findById(lectureId).orElseThrow(() -> new CustomException(ExceptionType.LECTURE_NOT_FOUND_EXCEPTION));
        Member student = memberRepository.findByUsername(username).orElseThrow(() -> new CustomException(ExceptionType.MEMBER_NOT_FOUND_EXCEPTION));

        List<Question> questions = questionRepository.findAllByLectureOrderByCreatedAtDesc(lecture);

        return questions.stream()
                .map(question -> QuestionDto.builder()
                        .id(question.getId())
                        .isLocked(question.getIsLocked())
                        .isAnswered(question.getIsAnswered())
                        .isMine(Objects.equals(question.getStudent().getId(), student.getId()))
                        .title(question.getTitle())
                        .author(question.getStudent().getName())
                        .createdAt(question.getCreatedAt().toString())
                        .build())
                .toList();
    }

    public PageResponseDto<QuestionDto> getAnsweredQuestions(Long lectureId, int page) {
        Lecture lecture = lectureRepository.findById(lectureId).orElseThrow(() -> new CustomException(ExceptionType.LECTURE_NOT_FOUND_EXCEPTION));

        Pageable pageable = PageRequest.of(page, 15, Sort.by("createdAt").descending());

        Page<Question> questionPage = questionRepository.findAllByLectureAndIsAnsweredOrderByCreatedAtDesc(lecture, true, pageable);

        List<QuestionDto> content = questionPage.stream().map(this::convertToDto).toList();

        return PageResponseDto.<QuestionDto>builder()
                .content(content)
                .totalPages(questionPage.getTotalPages())
                .totalElements(questionPage.getTotalElements())
                .currentPage(questionPage.getNumber() + 1)
                .build();
    }

    public PageResponseDto<QuestionDto> getNotAnsweredQuestions(Long lectureId, int page) {
        Lecture lecture = lectureRepository.findById(lectureId).orElseThrow(() -> new CustomException(ExceptionType.LECTURE_NOT_FOUND_EXCEPTION));

        Pageable pageable = PageRequest.of(page, 15, Sort.by("createdAt").descending());

        Page<Question> questionPage = questionRepository.findAllByLectureAndIsAnsweredOrderByCreatedAtDesc(lecture, false, pageable);

        List<QuestionDto> content = questionPage.stream().map(this::convertToDto).toList();

        return PageResponseDto.<QuestionDto>builder()
                .content(content)
                .totalPages(questionPage.getTotalPages())
                .totalElements(questionPage.getTotalElements())
                .currentPage(questionPage.getNumber() + 1)
                .build();
    }

    public PageResponseDto<QuestionDto> searchByFilterAndKeyword(Long lectureId, String filter, String keyword, int page) {
        Lecture lecture = lectureRepository.findById(lectureId).orElseThrow(() -> new CustomException(ExceptionType.LECTURE_NOT_FOUND_EXCEPTION));

        Pageable pageable = PageRequest.of(page, 15, Sort.by("createdAt").descending());
        Page<Question> questionPage;
        switch (filter) {
            case "전체" -> questionPage = questionRepository.searchAll(lecture, keyword, pageable);
            case "제목" -> questionPage = questionRepository.findAllByLectureAndTitleContainingOrderByCreatedAtDesc(lecture, keyword, pageable);
            case "내용" -> questionPage = questionRepository.findAllByLectureAndContentContainingOrderByCreatedAtDesc(lecture, keyword, pageable);
            case "작성자" -> questionPage = questionRepository.findAllByLectureAndStudent_NameContainingOrderByCreatedAtDesc(lecture, keyword, pageable);
            default -> throw new CustomException(ExceptionType.FILTER_INVALID_EXCEPTION);
        }

        List<QuestionDto> content = questionPage.stream().map(this::convertToDto).toList();

        return PageResponseDto.<QuestionDto>builder()
                .content(content)
                .totalPages(questionPage.getTotalPages())
                .totalElements(questionPage.getTotalElements())
                .currentPage(questionPage.getNumber() + 1)
                .build();
    }

    public List<QuestionDto> getMyQuestions(String username) {
        Member student = memberRepository.findByUsername(username).orElseThrow(() -> new CustomException(ExceptionType.MEMBER_NOT_FOUND_EXCEPTION));
        List<Question> questions = questionRepository.findAllByStudentOrderByCreatedAtDesc(student);

        return questions.stream()
                .map(question -> QuestionDto.builder()
                .id(question.getId())
                .isLocked(question.getIsLocked())
                .isAnswered(question.getIsAnswered())
                .title(question.getTitle())
                .createdAt(question.getCreatedAt().toString())
                .build())
                .toList();
    }

    public void deleteByLecture(Lecture lecture) {
        List<Question> questions = questionRepository.findAllByLecture(lecture);
        questions.forEach(question -> deleteQuestion(question.getId()));
    }
}