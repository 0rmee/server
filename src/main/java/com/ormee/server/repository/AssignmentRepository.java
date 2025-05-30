package com.ormee.server.repository;

import com.ormee.server.model.Assignment;
import com.ormee.server.model.Lecture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, Long> {
    List<Assignment> findAllByLectureOrderByCreatedAtDesc(Lecture lecture);
    List<Assignment> findAllByLectureAndIsDraftTrueOrderByCreatedAtDesc(Lecture lecture);
}
