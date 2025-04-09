package com.ormee.server.service;

import com.ormee.server.dto.answer.AnswerSaveDto;
import com.ormee.server.exception.CustomException;
import com.ormee.server.exception.ExceptionType;
import com.ormee.server.model.Answer;
import com.ormee.server.model.Question;
import com.ormee.server.repository.AnswerRepository;
import com.ormee.server.repository.QuestionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AnswerService {
    private final AnswerRepository answerRepository;
    private final QuestionRepository questionRepository;
    public AnswerService(AnswerRepository answerRepository, QuestionRepository questionRepository) {
        this.answerRepository = answerRepository;
        this.questionRepository = questionRepository;
    }

    public void writeAnswer(Long questionId, AnswerSaveDto answerSaveDto) {
        Question question = questionRepository.findById(questionId).orElseThrow(() -> new CustomException(ExceptionType.QUESTION_NOT_FOUND_EXCEPTION));

        Answer answer = Answer.builder()
                .question(question)
                .content(answerSaveDto.getContent())
                .build();
        answerRepository.save(answer);

        question.setIsAnswered(true);
        questionRepository.save(question);
    }

    public void modifyAnswer(Long answerId, AnswerSaveDto answerSaveDto) {
        Answer answer = answerRepository.findById(answerId).orElseThrow(() -> new CustomException(ExceptionType.ANSWER_NOT_FOUND_EXCEPTION));
        Question question = answer.getQuestion();
        List<Answer> answers = question.getAnswers();
        if (answers != null) {
            answers.forEach(a -> deleteAnswer(a.getId()));
        }
        writeAnswer(question.getId(), answerSaveDto);
    }

    public void deleteAnswer(Long answerId) {
        Answer answer = answerRepository.findById(answerId).orElseThrow(()->new CustomException(ExceptionType.ANSWER_NOT_FOUND_EXCEPTION));
        Question question = answer.getQuestion();
        answerRepository.delete(answer);
        question.setIsAnswered(false);
        questionRepository.save(question);
    }
}
