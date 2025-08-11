package com.ormee.server.homework.repository;

import com.ormee.server.homework.domain.Homework;
import com.ormee.server.lecture.domain.Lecture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface HomeworkRepository extends JpaRepository<Homework, Long> {
    List<Homework> findAllByLectureAndIsDraftFalseOrderByCreatedAtDesc(Lecture lecture);
    List<Homework> findAllByLectureAndIsDraftTrueOrderByCreatedAtDesc(Lecture lecture);
    List<Homework> findAllByIsDraftFalseAndNotifiedFalseAndDueTimeBefore(LocalDateTime now);
    List<Homework> findAllByIsDraftTrueAndCreatedAtBefore(LocalDateTime localDateTime);
    List<Homework> findAllByIsDraftFalseAndDueTimeBetween(LocalDateTime start, LocalDateTime end);
    List<Homework> findAllByIsDraftFalseAndDueTimeAfterAndLectureInOrderByCreatedAtAsc(
            LocalDateTime now,
            List<Lecture> lectures
    );
    List<Homework> findAllByLecture(Lecture lecture);
}
