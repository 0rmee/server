package com.ormee.server.question.repository;

import com.ormee.server.lecture.domain.Lecture;
import com.ormee.server.member.domain.Member;
import com.ormee.server.question.domain.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
    Page<Question> findAllByLectureOrderByCreatedAtDesc(Lecture lecture, Pageable pageable);
    Page<Question> findAllByLectureAndIsAnsweredOrderByCreatedAtDesc(Lecture lecture, Boolean isAnswered, Pageable pageable);
    @Query("""
    SELECT q FROM Question q
    WHERE q.lecture = :lecture
      AND (
            LOWER(q.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
            LOWER(q.content) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
            LOWER(q.student.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
          )
    ORDER BY q.createdAt DESC
    """)
    Page<Question> searchAll(@Param("lecture") Lecture lecture,
                             @Param("keyword") String keyword,
                             Pageable pageable);
    Page<Question> findAllByLectureAndTitleContainingOrderByCreatedAtDesc(Lecture lecture, String keyword, Pageable pageable);
    Page<Question> findAllByLectureAndContentContainingOrderByCreatedAtDesc(Lecture lecture, String keyword, Pageable pageable);
    Page<Question> findAllByLectureAndStudent_NameContainingOrderByCreatedAtDesc(Lecture lecture, String keyword, Pageable pageable);
    List<Question> findAllByLectureOrderByCreatedAtDesc(Lecture lecture);
    List<Question> findAllByStudentOrderByCreatedAtDesc(Member student);
}
