package com.ormee.server.repository;

import com.ormee.server.model.Lecture;
import com.ormee.server.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
    List<Question> findAllByLectureOrderByCreatedAtDesc(Lecture lecture);
    List<Question> findAllByLectureAndIsAnsweredOrderByCreatedAtDesc(Lecture lecture, Boolean isAnswered);
}
