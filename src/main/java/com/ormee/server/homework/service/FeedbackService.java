package com.ormee.server.homework.service;

import com.ormee.server.attachment.domain.Attachment;
import com.ormee.server.homework.domain.Feedback;
import com.ormee.server.homework.domain.HomeworkSubmit;
import com.ormee.server.homework.domain.StampType;
import com.ormee.server.homework.dto.FeedbackDto;
import com.ormee.server.homework.dto.FeedbackSaveDto;
import com.ormee.server.global.exception.CustomException;
import com.ormee.server.global.exception.ExceptionType;
import com.ormee.server.homework.repository.FeedbackRepository;
import com.ormee.server.homework.repository.HomeworkSubmitRepository;
import com.ormee.server.member.dto.AuthorDto;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FeedbackService {
    private final FeedbackRepository feedbackRepository;
    private final HomeworkSubmitRepository homeworkSubmitRepository;

    public FeedbackService(FeedbackRepository feedbackRepository, HomeworkSubmitRepository homeworkSubmitRepository) {
        this.feedbackRepository = feedbackRepository;
        this.homeworkSubmitRepository = homeworkSubmitRepository;
    }

    public void save(Long homeworkSubmitId, FeedbackSaveDto feedbackSaveDto) {
        HomeworkSubmit homeworkSubmit = homeworkSubmitRepository.findById(homeworkSubmitId).orElseThrow(() -> new CustomException(ExceptionType.SUBMIT_NOT_FOUND_EXCEPTION));
        Feedback feedback = Feedback.builder()
                .homeworkSubmit(homeworkSubmit)
                .stampType(feedbackSaveDto.getStamp() != null ? StampType.valueOf(feedbackSaveDto.getStamp()) :null)
                .content(feedbackSaveDto.getContent() != null ? feedbackSaveDto.getContent() : null)
                .build();
        feedbackRepository.save(feedback);
        homeworkSubmit.setIsFeedback(true);
        homeworkSubmitRepository.save(homeworkSubmit);
    }


    public List<FeedbackDto> get(Long homeworkSubmitId) {
        HomeworkSubmit homeworkSubmit = homeworkSubmitRepository.findById(homeworkSubmitId).orElseThrow(() -> new CustomException(ExceptionType.SUBMIT_NOT_FOUND_EXCEPTION));
        List<Feedback> feedbackList = feedbackRepository.findAllByHomeworkSubmit(homeworkSubmit);

        return feedbackList.stream()
                .map(feedback -> FeedbackDto.builder()
                        .id(feedback.getId())
                        .author(AuthorDto.builder()
                                .name(feedback.getAuthor().getNickname())
                                .image(Optional.ofNullable(feedback.getAuthor().getImage())
                                        .map(Attachment::getFilePath)
                                        .orElse(null))
                                .build())
                        .stamp(feedback.getStampType() != null? feedback.getStampType() :null)
                        .content(feedback.getContent() != null? feedback.getContent() : null)
                        .createdAt(feedback.getCreatedAt().toString())
                        .build())
                .toList();
    }

    public void update(Long feedbackId, FeedbackSaveDto feedbackSaveDto) {
        Feedback feedback = feedbackRepository.findById(feedbackId).orElseThrow(() -> new CustomException(ExceptionType.FEEDBACK_NOT_FOUND_EXCEPTION));
        feedback.setStampType(feedbackSaveDto.getStamp() != null ? StampType.valueOf(feedbackSaveDto.getStamp()) : null);
        feedback.setContent(feedbackSaveDto.getContent() != null ? feedbackSaveDto.getContent() : null);
        feedbackRepository.save(feedback);
    }


    public void delete(Long feedbackId) {
        Feedback feedback = feedbackRepository.findById(feedbackId).orElseThrow(() -> new CustomException(ExceptionType.FEEDBACK_NOT_FOUND_EXCEPTION));
        HomeworkSubmit homeworkSubmit = feedback.getHomeworkSubmit();

        feedbackRepository.delete(feedback);
        homeworkSubmit.setIsFeedback(false);
        homeworkSubmitRepository.save(homeworkSubmit);
    }
}
