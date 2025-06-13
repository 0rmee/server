package com.ormee.server.repository;

import com.ormee.server.model.HomeworkSubmit;
import com.ormee.server.model.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    List<Feedback> findAllByHomeworkSubmit(HomeworkSubmit homeworkSubmit);
}
