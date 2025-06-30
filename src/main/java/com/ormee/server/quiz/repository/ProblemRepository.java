package com.ormee.server.quiz.repository;

import com.ormee.server.quiz.domain.Problem;
import com.ormee.server.quiz.domain.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProblemRepository extends JpaRepository<Problem, Long> {
    List<Problem> findAllByQuiz(Quiz quiz);
    Problem findFirstByQuiz(Quiz quiz);
}
