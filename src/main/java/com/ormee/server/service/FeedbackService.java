package com.ormee.server.service;

import com.ormee.server.dto.feedback.FeedbackDto;
import com.ormee.server.dto.feedback.FeedbackSaveDto;
import com.ormee.server.exception.CustomException;
import com.ormee.server.exception.ExceptionType;
import com.ormee.server.model.HomeworkSubmit;
import com.ormee.server.model.Feedback;
import com.ormee.server.repository.HomeworkSubmitRepository;
import com.ormee.server.repository.FeedbackRepository;
import org.springframework.stereotype.Service;

import java.util.List;

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
                .content(feedbackSaveDto.getContent())
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
                        .content(feedback.getContent())
                        .createdAt(feedback.getCreatedAt().toString())
                        .build())
                .toList();
    }

    public void update(Long feedbackId, FeedbackSaveDto feedbackSaveDto) {
        Feedback feedback = feedbackRepository.findById(feedbackId).orElseThrow(() -> new CustomException(ExceptionType.FEEDBACK_NOT_FOUND_EXCEPTION));
        feedback.setContent(feedbackSaveDto.getContent());
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
