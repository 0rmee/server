package com.ormee.server.question.repository;

import com.ormee.server.question.domain.Answer;
import com.ormee.server.question.domain.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, Long> {
    Optional<Answer> findByQuestion(Question question);
    Optional<Answer> findByQuestion_Id(Long questionId);
    List<Answer> findAllByQuestion(Question question);
}
