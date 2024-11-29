package com.ormee.server.repository;

import com.ormee.server.model.Problem;
import com.ormee.server.model.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProblemRepository extends JpaRepository<Problem, Long> {
    List<Problem> findAllByQuiz(Quiz quiz);
}
