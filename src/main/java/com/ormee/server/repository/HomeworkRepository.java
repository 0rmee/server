package com.ormee.server.repository;

import com.ormee.server.model.Homework;
import com.ormee.server.model.Lecture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface HomeworkRepository extends JpaRepository<Homework, Long> {
    List<Homework> findAllByLectureAndIsDraftFalseOrderByCreatedAtDesc(Lecture lecture);
    List<Homework> findAllByLectureAndIsDraftTrueOrderByCreatedAtDesc(Lecture lecture);
    List<Homework> findAllByDueTimeBeforeAndNotifiedFalse(LocalDateTime now);
}
