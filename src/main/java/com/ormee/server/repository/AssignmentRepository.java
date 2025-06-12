package com.ormee.server.repository;

import com.ormee.server.model.Assignment;
import com.ormee.server.model.Lecture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, Long> {
    List<Assignment> findAllByLectureAndIsDraftFalseOrderByCreatedAtDesc(Lecture lecture);
    List<Assignment> findAllByLectureAndIsDraftTrueOrderByCreatedAtDesc(Lecture lecture);
    List<Assignment> findAllByDueTimeBeforeAndNotifiedFalse(LocalDateTime now);
}
