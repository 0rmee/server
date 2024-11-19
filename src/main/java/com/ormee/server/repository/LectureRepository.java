package com.ormee.server.repository;

import com.ormee.server.model.Lecture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface LectureRepository extends JpaRepository<Lecture, UUID> {
    Optional<Lecture> findByCode(Integer code);
    boolean existsByCode(Integer code);
}
