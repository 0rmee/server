package com.ormee.server.question.repository;

import com.ormee.server.lecture.domain.Lecture;
import com.ormee.server.member.domain.Member;
import com.ormee.server.question.domain.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
    Page<Question> findAllByLectureOrderByCreatedAtDesc(Lecture lecture, Pageable pageable);
    Page<Question> findAllByLectureAndIsAnsweredOrderByCreatedAtDesc(Lecture lecture, Boolean isAnswered, Pageable pageable);
    List<Question> findAllByLectureOrderByCreatedAtDesc(Lecture lecture);
    List<Question> findAllByStudentOrderByCreatedAtDesc(Member student);
}
