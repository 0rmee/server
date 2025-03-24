package com.ormee.server.repository;

import com.ormee.server.model.Assignment;
import com.ormee.server.model.AssignmentSubmit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssignmentSubmitRepository extends JpaRepository<AssignmentSubmit, Long> {
    List<AssignmentSubmit> findAllByAssignmentOrderByStudent_Name(Assignment assignment);
    boolean existsByAssignmentAndIsFeedback(Assignment assignment, boolean isFeedback);
}
