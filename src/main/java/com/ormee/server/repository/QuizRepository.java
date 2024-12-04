package com.ormee.server.repository;

import com.ormee.server.model.Lecture;
import com.ormee.server.model.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, UUID> {
    List<Quiz> findAllByLecture(Lecture lecture);
    List<Quiz> findAllByLectureAndIsDraftOrderByCreatedAtDesc(Lecture lecture, Boolean isDraft);
    List<Quiz> findAllByLectureAndIsDraftAndIsOpenedOrderByOpenTimeDesc(Lecture lecture, Boolean isDraft, Boolean isOpened);
}
