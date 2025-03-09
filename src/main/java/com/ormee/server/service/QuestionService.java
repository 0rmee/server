package com.ormee.server.service;

import com.ormee.server.dto.question.QuestionListDto;
import com.ormee.server.dto.question.QuestionSaveDto;
import com.ormee.server.exception.CustomException;
import com.ormee.server.exception.ExceptionType;
import com.ormee.server.model.Lecture;
import com.ormee.server.model.Question;
import com.ormee.server.repository.LectureRepository;
import com.ormee.server.repository.QuestionRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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
                .author(questionSaveDto.getAuthor() != null ? questionSaveDto.getAuthor() : null)
                .content(questionSaveDto.getContent() != null ? questionSaveDto.getContent() : null)
                .isAnswered(questionSaveDto.getIsAnswered() != null ? questionSaveDto.getIsAnswered() : null)
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

    public List<QuestionListDto> findAllByLecture(UUID lectureId) {
        Lecture lecture = lectureRepository.findById(lectureId).orElseThrow(() -> new CustomException(ExceptionType.LECTURE_NOT_FOUND_EXCEPTION));
        List<Question> questions = questionRepository.findAllByLecture(lecture, Sort.by(Sort.Direction.DESC, "createdAt"));

        return questions.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    public List<QuestionListDto> findAllByLectureAndIsAnswered(UUID lectureId, Boolean isAnswered) {
        Lecture lecture = lectureRepository.findById(lectureId).orElseThrow(() -> new CustomException(ExceptionType.LECTURE_NOT_FOUND_EXCEPTION));
        List<Question> questionList = questionRepository.findAllByLectureAndIsAnswered(lecture, isAnswered, Sort.by(Sort.Direction.DESC, "createdAt"));
        return questionList.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    private QuestionListDto convertToDto(Question question) {
        QuestionListDto dto = new QuestionListDto();
        dto.setId(question.getId());
        dto.setTitle(question.getTitle() != null ? question.getTitle() : "제목 없음");
        dto.setIsAnswered(question.getIsAnswered() != null ? question.getIsAnswered() : false);
        dto.setAuthor(question.getAuthor() != null ? question.getAuthor() : "작성자 없음");
        dto.setCreatedAt(question.getCreatedAt());
        return dto;
    }

    public Question findById(Long questionId) {
        Question question = questionRepository.findById(questionId).orElseThrow(() -> new CustomException(ExceptionType.QUESTION_NOT_FOUND_EXCEPTION));
        return question;
    }
}