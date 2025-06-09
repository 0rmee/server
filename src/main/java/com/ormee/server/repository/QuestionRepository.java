package com.ormee.server.repository;

import com.ormee.server.model.Lecture;
import com.ormee.server.model.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
    Page<Question> findAllByLectureOrderByCreatedAtDesc(Lecture lecture, Pageable pageable);
    Page<Question> findAllByLectureAndIsAnsweredOrderByCreatedAtDesc(Lecture lecture, Boolean isAnswered, Pageable pageable);
}
