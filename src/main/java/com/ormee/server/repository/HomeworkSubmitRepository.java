package com.ormee.server.repository;

import com.ormee.server.model.Homework;
import com.ormee.server.model.HomeworkSubmit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HomeworkSubmitRepository extends JpaRepository<HomeworkSubmit, Long> {
    List<HomeworkSubmit> findAllByHomeworkOrderByStudent_Name(Homework homework);
    boolean existsByHomeworkAndIsFeedback(Homework homework, boolean isFeedback);

    List<HomeworkSubmit> findAllByHomework(Homework homework);

    List<HomeworkSubmit> findAllByHomeworkAndIsCheckedFalse(Homework homework);
    List<HomeworkSubmit> findAllByHomeworkOrderByCreatedAtDesc(Homework homework);
    Long countAllByHomework(Homework homework);
}
