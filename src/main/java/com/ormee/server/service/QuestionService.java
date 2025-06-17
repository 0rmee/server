package com.ormee.server.service;

import com.ormee.server.dto.question.QuestionDto;
import com.ormee.server.dto.question.QuestionSaveDto;
import com.ormee.server.dto.response.PageResponseDto;
import com.ormee.server.exception.CustomException;
import com.ormee.server.exception.ExceptionType;
import com.ormee.server.model.*;
import com.ormee.server.model.member.Member;
import com.ormee.server.repository.LectureRepository;
import com.ormee.server.repository.MemberRepository;
import com.ormee.server.repository.QuestionRepository;
import com.ormee.server.service.attachment.AttachmentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class QuestionService {
    private final QuestionRepository questionRepository;
    private final LectureRepository lectureRepository;
    private final MemberRepository memberRepository;
    private final AttachmentService attachmentService;
    private final NotificationService notificationService;

    public QuestionService(QuestionRepository questionRepository, LectureRepository lectureRepository, MemberRepository memberRepository, AttachmentService attachmentService, NotificationService notificationService) {
        this.questionRepository = questionRepository;
        this.lectureRepository = lectureRepository;
        this.memberRepository = memberRepository;
        this.attachmentService = attachmentService;
        this.notificationService = notificationService;
    }

    public void saveQuestion(Long lectureId, QuestionSaveDto questionSaveDto, String username) throws IOException {
        Lecture lecture = lectureRepository.findById(lectureId).orElseThrow(() -> new CustomException(ExceptionType.LECTURE_NOT_FOUND_EXCEPTION));
        Member student = memberRepository.findByUsername(username).orElseThrow(() -> new CustomException(ExceptionType.MEMBER_NOT_FOUND_EXCEPTION));

        Question question = Question.builder()
                .lecture(lecture)
                .title(questionSaveDto.getTitle())
                .student(student)
                .content(questionSaveDto.getContent() != null ? questionSaveDto.getContent() : null)
                .isAnswered(false)
                .build();

        question = questionRepository.save(question);

        List<Attachment> attachments = new ArrayList<>();
        if (questionSaveDto.getFiles() != null) {
            for (MultipartFile multipartFile : questionSaveDto.getFiles()) {
                attachments.add(attachmentService.save(AttachmentType.QUESTION, question.getId(), multipartFile));
            }
        }
        question.setAttachments(attachments);

        Question savedQuestion = questionRepository.save(question);

        notificationService.create(NotificationType.QUESTION, savedQuestion);
    }

    public void modifyQuestion(Long questionId, QuestionSaveDto questionSaveDto) throws IOException {
        Question question = questionRepository.findById(questionId).orElseThrow(()->new CustomException(ExceptionType.QUESTION_NOT_FOUND_EXCEPTION));

        if (questionSaveDto.getTitle() != null) {
            question.setTitle(questionSaveDto.getTitle());
        }
        if (questionSaveDto.getContent() != null) {
            question.setContent(questionSaveDto.getContent());
        }
        List<Attachment> attachments = new ArrayList<>();
        if (questionSaveDto.getFiles() != null) {
            for (MultipartFile multipartFile : questionSaveDto.getFiles()) {
                attachments.add(attachmentService.save(AttachmentType.QUESTION, question.getId(), multipartFile));
            }
        }
        question.setAttachments(attachments);
    }

    public void deleteQuestion(Long questionId) {
        Question question = questionRepository.findById(questionId).orElseThrow(() -> new CustomException(ExceptionType.QUESTION_NOT_FOUND_EXCEPTION));
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

    public QuestionDto findById(Long questionId) {
        Question question = questionRepository.findById(questionId).orElseThrow(() -> new CustomException(ExceptionType.QUESTION_NOT_FOUND_EXCEPTION));
        return convertToDto(question);
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
}