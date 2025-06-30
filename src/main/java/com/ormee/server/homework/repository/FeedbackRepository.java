package com.ormee.server.homework.repository;

import com.ormee.server.homework.domain.Homework;
import com.ormee.server.homework.domain.HomeworkSubmit;
import com.ormee.server.homework.domain.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    List<Feedback> findAllByHomeworkSubmit(HomeworkSubmit homeworkSubmit);
    long countAllByHomeworkSubmit_Homework(Homework homework);
}
