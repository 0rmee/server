package com.ormee.server.repository;

import com.ormee.server.model.Lecture;
import com.ormee.server.model.Notice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoticeRepository extends JpaRepository<Notice, Long> {
    Page<Notice> findAllByLectureOrderByCreatedAtDesc(Lecture lecture, Pageable pageable);
    List<Notice> findAllByLectureAndIsPinnedTrueOrderByCreatedAtDesc(Lecture lecture);
    List<Notice> findAllByLectureAndIsDraftTrueOrderByCreatedAtDesc(Lecture lecture);
    @Query("SELECT n FROM Notice n WHERE n.lecture = :lecture AND (n.title LIKE %:keyword% OR n.description LIKE %:keyword%) ORDER BY n.createdAt DESC")
    Page<Notice> searchByLectureAndKeyword(@Param("lecture") Lecture lecture,
                                           @Param("keyword") String keyword,
                                           Pageable pageable);

}
