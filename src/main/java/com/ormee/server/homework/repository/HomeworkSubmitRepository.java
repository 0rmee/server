package com.ormee.server.homework.repository;

import com.ormee.server.homework.domain.Homework;
import com.ormee.server.homework.domain.HomeworkSubmit;
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
