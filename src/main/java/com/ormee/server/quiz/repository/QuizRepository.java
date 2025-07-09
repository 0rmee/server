package com.ormee.server.quiz.repository;

import com.ormee.server.lecture.domain.Lecture;
import com.ormee.server.quiz.domain.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, Long> {
    List<Quiz> findAllByLectureAndIsDraftOrderByCreatedAtDesc(Lecture lecture, Boolean isDraft);
    List<Quiz> findAllByLectureAndIsDraftAndIsOpenedOrderByCreatedAtDesc(Lecture lecture, Boolean isDraft, Boolean isOpened);
    List<Quiz> findAllByIsDraftFalseAndNotifiedFalseAndDueTimeBefore(LocalDateTime now);
    List<Quiz> findAllByLectureAndIsDraftFalseOrderByCreatedAtDesc(Lecture lecture);
}
