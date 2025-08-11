package com.ormee.server.homework.repository;

import com.ormee.server.homework.domain.Homework;
import com.ormee.server.homework.domain.HomeworkSubmit;
import com.ormee.server.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface HomeworkSubmitRepository extends JpaRepository<HomeworkSubmit, Long> {
    Optional<HomeworkSubmit> findByHomeworkAndStudent(Homework homework, Member student);

    List<HomeworkSubmit> findAllByHomeworkOrderByStudent_Name(Homework homework);

    List<HomeworkSubmit> findAllByHomework(Homework homework);

    List<HomeworkSubmit> findAllByHomeworkAndIsCheckedFalse(Homework homework);

    List<HomeworkSubmit> findAllByHomeworkOrderByCreatedAtDesc(Homework homework);

    Long countAllByHomework(Homework homework);

    boolean existsByHomeworkAndStudent(Homework homework, Member student);

    @Query("""
              select distinct hs.student.id
              from HomeworkSubmit hs
              where hs.homework.id = :homeworkId
            """)
    Set<Long> findSubmittedStudentIdsByHomeworkId(Long homeworkId);
}
