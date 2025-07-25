package com.ormee.server.homework.repository;

import com.ormee.server.homework.domain.Homework;
import com.ormee.server.homework.domain.HomeworkSubmit;
import com.ormee.server.homework.domain.Feedback;
import com.ormee.server.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    List<Feedback> findAllByHomeworkSubmitOrderByCreatedAtAsc(HomeworkSubmit homeworkSubmit);
    boolean existsByHomeworkSubmit_HomeworkAndHomeworkSubmit_Student(Homework homework, Member student);
    Long countAllByHomeworkSubmit_Homework(Homework homework);
    List<Feedback> findAllByHomeworkSubmit_Homework(Homework homework);
}
