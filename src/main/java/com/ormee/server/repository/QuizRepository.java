package com.ormee.server.repository;

import com.ormee.server.model.Lecture;
import com.ormee.server.model.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, Long> {
    List<Quiz> findAllByLectureAndIsDraftOrderByCreatedAtDesc(Lecture lecture, Boolean isDraft);
    List<Quiz> findAllByLectureAndIsDraftAndIsOpenedOrderByDueTimeDesc(Lecture lecture, Boolean isDraft, Boolean isOpened);
    List<Quiz> findAllByDueTimeBeforeAndNotifiedFalse(LocalDateTime now);
}
