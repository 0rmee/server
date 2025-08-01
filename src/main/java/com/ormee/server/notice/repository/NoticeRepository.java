package com.ormee.server.notice.repository;

import com.ormee.server.lecture.domain.Lecture;
import com.ormee.server.notice.domain.Notice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NoticeRepository extends JpaRepository<Notice, Long> {
    Page<Notice> findAllByLectureAndIsDraftFalseOrderByCreatedAtDesc(Lecture lecture, Pageable pageable);
    List<Notice> findAllByLectureAndIsPinnedTrueOrderByCreatedAtDesc(Lecture lecture);
    List<Notice> findAllByLectureAndIsDraftTrueOrderByCreatedAtDesc(Lecture lecture);
    List<Notice> findAllByLectureAndIsDraftFalseOrderByCreatedAtDesc(Lecture lecture);
    List<Notice> findAllByIsDraftTrueAndCreatedAtBefore(LocalDateTime localDateTime);
    @Query("""
    SELECT n FROM Notice n
    WHERE n.lecture = :lecture
      AND n.isDraft = false
      AND (
            LOWER(n.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
            LOWER(n.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
            LOWER(n.author.nickname) LIKE LOWER(CONCAT('%', :keyword, '%'))
          )
    ORDER BY n.createdAt DESC
    """)
    Page<Notice> searchByLectureAndKeyword(@Param("lecture") Lecture lecture,
                                           @Param("keyword") String keyword,
                                           Pageable pageable);
    @Query("SELECT n FROM Notice n WHERE n.lecture = :lecture AND n.isDraft = false AND (UPPER(n.title) LIKE UPPER(CONCAT('%', :keyword, '%')) OR UPPER(n.description) LIKE UPPER(CONCAT('%', :keyword, '%'))) ORDER BY n.createdAt DESC")
    List<Notice> searchByLectureAndKeyword(@Param("lecture") Lecture lecture,
                                           @Param("keyword") String keyword);
    long countAllByLectureAndIsPinnedTrue(Lecture lecture);
    List<Notice> findAllByLecture(Lecture lecture);
}
