package com.ormee.server.question.service;

import com.ormee.server.member.domain.Member;
import com.ormee.server.member.repository.MemberRepository;
import com.ormee.server.question.domain.Answer;
import com.ormee.server.question.repository.AnswerRepository;
import com.ormee.server.question.domain.Question;
import com.ormee.server.question.repository.QuestionRepository;
import com.ormee.server.question.dto.AnswerDto;
import com.ormee.server.question.dto.AnswerSaveDto;
import com.ormee.server.global.exception.CustomException;
import com.ormee.server.global.exception.ExceptionType;
import com.ormee.server.attachment.domain.Attachment;
import com.ormee.server.attachment.domain.AttachmentType;
import com.ormee.server.attachment.service.AttachmentService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AnswerService {
    private final AnswerRepository answerRepository;
    private final MemberRepository memberRepository;
    private final QuestionRepository questionRepository;
    private final AttachmentService attachmentService;

    public AnswerService(AnswerRepository answerRepository, MemberRepository memberRepository, QuestionRepository questionRepository, AttachmentService attachmentService) {
        this.answerRepository = answerRepository;
        this.memberRepository = memberRepository;
        this.questionRepository = questionRepository;
        this.attachmentService = attachmentService;
    }

    public void writeAnswer(Long questionId, AnswerSaveDto answerSaveDto, String username) throws IOException {
        Question question = questionRepository.findById(questionId).orElseThrow(() -> new CustomException(ExceptionType.QUESTION_NOT_FOUND_EXCEPTION));
        Member author = memberRepository.findByUsername(username).orElseThrow(() -> new CustomException(ExceptionType.MEMBER_NOT_FOUND_EXCEPTION));

        Answer answer = Answer.builder()
                .author(author)
                .question(question)
                .content(answerSaveDto.getContent())
                .build();

        answer = answerRepository.save(answer);

        List<Attachment> attachments = new ArrayList<>();
        if (answerSaveDto.getFiles() != null) {
            for (MultipartFile multipartFile : answerSaveDto.getFiles()) {
                attachments.add(attachmentService.save(AttachmentType.ANSWER, answer.getId(), multipartFile));
            }
        }
        answer.setAttachments(attachments);

        question.setIsAnswered(true);
        questionRepository.save(question);

        answerRepository.save(answer);
    }

    public void modifyAnswer(Long answerId, AnswerSaveDto answerSaveDto) throws IOException {
        Answer answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new CustomException(ExceptionType.ANSWER_NOT_FOUND_EXCEPTION));
        if (answerSaveDto.getContent() != null) {
            answer.setContent(answerSaveDto.getContent());
        }
        List<Attachment> existingAttachments = answer.getAttachments();
        if (existingAttachments != null) {
            existingAttachments.clear();
        }
        if(answerSaveDto.getFiles() != null) {
            for (MultipartFile multipartFile : answerSaveDto.getFiles()) {
                Attachment newAttachment = attachmentService.save(AttachmentType.ANSWER, answer.getId(), multipartFile);
                existingAttachments.add(newAttachment);
            }
        }
        answerRepository.save(answer);
    }


    public void deleteAnswer(Long answerId) {
        Answer answer = answerRepository.findById(answerId).orElseThrow(()->new CustomException(ExceptionType.ANSWER_NOT_FOUND_EXCEPTION));
        Question question = answer.getQuestion();
        answerRepository.delete(answer);
        question.setIsAnswered(false);
        questionRepository.save(question);
    }

    public AnswerDto getByQuestion(Long questionId) {
        Question question = questionRepository.findById(questionId).orElseThrow(() -> new CustomException(ExceptionType.QUESTION_NOT_FOUND_EXCEPTION));
        Answer answer = answerRepository.findByQuestion(question).orElseThrow(() -> new CustomException(ExceptionType.ANSWER_NOT_FOUND_EXCEPTION));

        return AnswerDto.builder()
                    .teacherName(Optional.ofNullable(answer.getAuthor())
                            .map(Member::getNickname)
                            .orElse(answer.getQuestion().getLecture().getTeacher().getNickname()))
                .teacherImage(Optional.ofNullable(answer.getAuthor().getImage())
                        .map(Attachment::getFilePath)
                        .orElse(null))
                    .content(answer.getContent())
                    .createdAt(answer.getCreatedAt().toString())
                    .filePaths(answer.getAttachments().stream().map(Attachment::getFilePath).toList())
                    .build();
    }
}
