package com.ormee.server.service;

import com.ormee.server.dto.feedback.FeedbackDto;
import com.ormee.server.dto.feedback.FeedbackSaveDto;
import com.ormee.server.exception.CustomException;
import com.ormee.server.exception.ExceptionType;
import com.ormee.server.model.AssignmentSubmit;
import com.ormee.server.model.Feedback;
import com.ormee.server.repository.AssignmentSubmitRepository;
import com.ormee.server.repository.FeedbackRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FeedbackService {
    private final FeedbackRepository feedbackRepository;
    private final AssignmentSubmitRepository assignmentSubmitRepository;

    public FeedbackService(FeedbackRepository feedbackRepository, AssignmentSubmitRepository assignmentSubmitRepository) {
        this.feedbackRepository = feedbackRepository;
        this.assignmentSubmitRepository = assignmentSubmitRepository;
    }

    public void save(Long assignmentSubmitId, FeedbackSaveDto feedbackSaveDto) {
        AssignmentSubmit assignmentSubmit = assignmentSubmitRepository.findById(assignmentSubmitId).orElseThrow(() -> new CustomException(ExceptionType.SUBMIT_NOT_FOUND_EXCEPTION));
        Feedback feedback = Feedback.builder()
                .assignmentSubmit(assignmentSubmit)
                .content(feedbackSaveDto.getContent())
                .build();
        feedbackRepository.save(feedback);
        assignmentSubmit.setIsFeedback(true);
        assignmentSubmitRepository.save(assignmentSubmit);
    }


    public List<FeedbackDto> get(Long assignmentSubmitId) {
        AssignmentSubmit assignmentSubmit = assignmentSubmitRepository.findById(assignmentSubmitId).orElseThrow(() -> new CustomException(ExceptionType.SUBMIT_NOT_FOUND_EXCEPTION));
        List<Feedback> feedbackList = feedbackRepository.findAllByAssignmentSubmit(assignmentSubmit);

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
        AssignmentSubmit assignmentSubmit = feedback.getAssignmentSubmit();

        feedbackRepository.delete(feedback);
        assignmentSubmit.setIsFeedback(false);
        assignmentSubmitRepository.save(assignmentSubmit);
    }
}
