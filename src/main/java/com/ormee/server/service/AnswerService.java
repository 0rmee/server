package com.ormee.server.service;

import com.ormee.server.dto.answer.AnswerSaveDto;
import com.ormee.server.exception.CustomException;
import com.ormee.server.exception.ExceptionType;
import com.ormee.server.model.Answer;
import com.ormee.server.model.Attachment;
import com.ormee.server.model.AttachmentType;
import com.ormee.server.model.Question;
import com.ormee.server.repository.AnswerRepository;
import com.ormee.server.repository.QuestionRepository;
import com.ormee.server.service.attachment.AttachmentService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class AnswerService {
    private final AnswerRepository answerRepository;
    private final QuestionRepository questionRepository;
    private final AttachmentService attachmentService;

    public AnswerService(AnswerRepository answerRepository, QuestionRepository questionRepository, AttachmentService attachmentService) {
        this.answerRepository = answerRepository;
        this.questionRepository = questionRepository;
        this.attachmentService = attachmentService;
    }

    public void writeAnswer(Long questionId, AnswerSaveDto answerSaveDto) throws IOException {
        Question question = questionRepository.findById(questionId).orElseThrow(() -> new CustomException(ExceptionType.QUESTION_NOT_FOUND_EXCEPTION));

        Answer answer = Answer.builder()
                .question(question)
                .content(answerSaveDto.getContent())
                .build();

        answer = answerRepository.save(answer);

        List<Attachment> attachments = new ArrayList<>();
        for(MultipartFile multipartFile : answerSaveDto.getFiles()) {
            attachments.add(attachmentService.save(AttachmentType.ANSWER, answer.getId(), multipartFile));
        }
        answer.setAttachments(attachments);

        question.setIsAnswered(true);
        questionRepository.save(question);

        answerRepository.save(answer);
    }

    public void modifyAnswer(Long answerId, AnswerSaveDto answerSaveDto) throws IOException {
        Answer answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new CustomException(ExceptionType.ANSWER_NOT_FOUND_EXCEPTION));
        answer.setContent(answerSaveDto.getContent());
        List<Attachment> existingAttachments = answer.getAttachments();
        if (existingAttachments != null) {
            existingAttachments.clear();
        }
        for (MultipartFile multipartFile : answerSaveDto.getFiles()) {
            Attachment newAttachment = attachmentService.save(AttachmentType.ANSWER, answer.getId(), multipartFile);
            existingAttachments.add(newAttachment);
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
}
