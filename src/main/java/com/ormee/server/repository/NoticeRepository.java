package com.ormee.server.repository;

import com.ormee.server.model.Lecture;
import com.ormee.server.model.Notice;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoticeRepository extends JpaRepository<Notice, Long> {
    List<Notice> findAllByLecture(Lecture lecture, Sort sort);
}
