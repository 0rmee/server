package com.ormee.server.repository;

import com.ormee.server.model.Lecture;
import com.ormee.server.model.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LectureRepository extends JpaRepository<Lecture, Long> {
    Optional<Lecture> findByCode(Integer code);
    boolean existsByCode(Integer code);
    List<Lecture> findAllByTeacher(Member teacher);
}
