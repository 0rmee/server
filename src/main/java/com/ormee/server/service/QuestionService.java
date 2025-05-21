package com.ormee.server.service;

import com.ormee.server.dto.question.QuestionDto;
import com.ormee.server.dto.question.QuestionSaveDto;
import com.ormee.server.exception.CustomException;
import com.ormee.server.exception.ExceptionType;
import com.ormee.server.model.Attachment;
import com.ormee.server.model.AttachmentType;
import com.ormee.server.model.Lecture;
import com.ormee.server.model.Question;
import com.ormee.server.model.member.Member;
import com.ormee.server.repository.LectureRepository;
import com.ormee.server.repository.MemberRepository;
import com.ormee.server.repository.QuestionRepository;
import com.ormee.server.service.attachment.AttachmentService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class QuestionService {
    private final QuestionRepository questionRepository;
    private final LectureRepository lectureRepository;
    private final MemberRepository memberRepository;
    private final AttachmentService attachmentService;

    public QuestionService(QuestionRepository questionRepository, LectureRepository lectureRepository, MemberRepository memberRepository, AttachmentService attachmentService) {
        this.questionRepository = questionRepository;
        this.lectureRepository = lectureRepository;
        this.memberRepository = memberRepository;
        this.attachmentService = attachmentService;
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
        for(MultipartFile multipartFile : questionSaveDto.getFiles()) {
            attachments.add(attachmentService.save(AttachmentType.QUESTION, question.getId(), multipartFile));
        }
        question.setAttachments(attachments);

        questionRepository.save(question);
    }

    public void modifyQuestion(Long questionId, QuestionSaveDto questionSaveDto) throws IOException {
        Question question = questionRepository.findById(questionId).orElseThrow(()->new CustomException(ExceptionType.QUESTION_NOT_FOUND_EXCEPTION));

        question.setTitle(questionSaveDto.getTitle());
        question.setContent(questionSaveDto.getContent());

        List<Attachment> attachments = new ArrayList<>();
        for(MultipartFile multipartFile : questionSaveDto.getFiles()) {
            attachments.add(attachmentService.save(AttachmentType.QUESTION, question.getId(), multipartFile));
        }
        question.setAttachments(attachments);
    }

    public void deleteQuestion(Long questionId) {
        Question question = questionRepository.findById(questionId).orElseThrow(() -> new CustomException(ExceptionType.QUESTION_NOT_FOUND_EXCEPTION));
        questionRepository.delete(question);
    }

    private QuestionDto convertToDto(Question question) {
        return QuestionDto.builder()
                .id(question.getId())
                .title(question.getTitle())
                .content(question.getContent())
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

    public List<QuestionDto> getQuestions(Long lectureId) {
        Lecture lecture = lectureRepository.findById(lectureId).orElseThrow(() -> new CustomException(ExceptionType.LECTURE_NOT_FOUND_EXCEPTION));
        List<Question> questions = questionRepository.findAllByLectureOrderByCreatedAtDesc(lecture);

        return questions.stream().map(this::convertToDto).toList();
    }

    public List<QuestionDto> getAnsweredQuestions(Long lectureId) {
        Lecture lecture = lectureRepository.findById(lectureId).orElseThrow(() -> new CustomException(ExceptionType.LECTURE_NOT_FOUND_EXCEPTION));
        List<Question> questions = questionRepository.findAllByLectureAndIsAnsweredOrderByCreatedAtDesc(lecture, true);

        return questions.stream().map(this::convertToDto).toList();
    }

    public List<QuestionDto> getNotAnsweredQuestions(Long lectureId) {
        Lecture lecture = lectureRepository.findById(lectureId).orElseThrow(() -> new CustomException(ExceptionType.LECTURE_NOT_FOUND_EXCEPTION));
        List<Question> questions = questionRepository.findAllByLectureAndIsAnsweredOrderByCreatedAtDesc(lecture, false);

        return questions.stream().map(this::convertToDto).toList();
    }
}