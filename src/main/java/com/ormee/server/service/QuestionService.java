package com.ormee.server.service;

import com.ormee.server.dto.question.QuestionSaveDto;
import com.ormee.server.exception.CustomException;
import com.ormee.server.exception.ExceptionType;
import com.ormee.server.model.Lecture;
import com.ormee.server.model.Question;
import com.ormee.server.repository.LectureRepository;
import com.ormee.server.repository.QuestionRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class QuestionService {
    private final QuestionRepository questionRepository;
    private final LectureRepository lectureRepository;

    public QuestionService(QuestionRepository questionRepository, LectureRepository lectureRepository) {
        this.questionRepository = questionRepository;
        this.lectureRepository = lectureRepository;
    }

    public void saveQuestion(UUID lectureId, QuestionSaveDto questionSaveDto) {
        Lecture lecture = lectureRepository.findById(lectureId).orElseThrow(() -> new CustomException(ExceptionType.LECTURE_NOT_FOUND_EXCEPTION));

        Question question = Question.builder()
                .lecture(lecture)
                .title(questionSaveDto.getTitle())
                .author(questionSaveDto.getAuthor())
                .content(questionSaveDto.getContent())
                .isAnswered(questionSaveDto.getIsAnswered())
                .build();
        questionRepository.save(question);
    }


    public void modifyQuestion(Long questionId, QuestionSaveDto questionSaveDto) {
        Question question = questionRepository.findById(questionId).orElseThrow(()->new CustomException(ExceptionType.QUESTION_NOT_FOUND_EXCEPTION));
        saveQuestion(question.getLecture().getId(), questionSaveDto);
        deleteQuestion(questionId);
    }

    public void deleteQuestion(Long questionId) {
        Question question = questionRepository.findById(questionId).orElseThrow(() -> new CustomException(ExceptionType.QUESTION_NOT_FOUND_EXCEPTION));
        questionRepository.delete(question);
    }

    public List<Question> findAllByLecture(UUID lectureId) {
        Lecture lecture = lectureRepository.findById(lectureId).orElseThrow(() -> new CustomException(ExceptionType.LECTURE_NOT_FOUND_EXCEPTION));
        List<Question> questions = questionRepository.findAllByLecture(lecture);

        return questions;
    }

    public List<Question> findAllByLectureAndIsAnswered(UUID lectureId, Boolean isAnswered) {
        Lecture lecture = lectureRepository.findById(lectureId).orElseThrow(() -> new CustomException(ExceptionType.LECTURE_NOT_FOUND_EXCEPTION));
        List<Question> questionList = questionRepository.findAllByLectureAndIsAnswered(lecture, isAnswered);
        return questionList;
    }

    public Question findById(Long questionId) {
        Question question = questionRepository.findById(questionId).orElseThrow(() -> new CustomException(ExceptionType.QUESTION_NOT_FOUND_EXCEPTION));
        return question;
    }
}