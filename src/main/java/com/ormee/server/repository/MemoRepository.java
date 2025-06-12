package com.ormee.server.repository;

import com.ormee.server.model.Lecture;
import com.ormee.server.model.Memo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface MemoRepository extends JpaRepository<Memo, Long> {
    List<Memo> findAllByLecture(Lecture lecture);
    boolean existsByLectureAndIsOpen(Lecture lecture, Boolean IsOpen);
    Optional<Memo> findFirstByLectureAndIsOpenOrderByCreatedAtDesc(Lecture lecture, boolean IsOpen);

    List<Memo> findAllByDueTimeBeforeAndNotifiedFalse(LocalDateTime now);
}
