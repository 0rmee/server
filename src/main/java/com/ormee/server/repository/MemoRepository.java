package com.ormee.server.repository;

import com.ormee.server.model.Lecture;
import com.ormee.server.model.Memo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemoRepository extends JpaRepository<Memo, Long> {
    Optional<Memo> findById(Long id);
    List<Memo> findAll();
    List<Memo> findAllByLecture(Lecture lecture);
    com.ormee.server.model.Memo save(com.ormee.server.model.Memo memo);
    boolean existsByLectureAndIsOpen(Lecture lecture, Boolean IsOpen);
}
