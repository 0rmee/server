package com.ormee.server.lecture.repository;

import com.ormee.server.lecture.domain.Lecture;
import com.ormee.server.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface LectureRepository extends JpaRepository<Lecture, Long> {
    Optional<Lecture> findByCode(Integer code);
    boolean existsByCode(Integer code);
    List<Lecture> findAllByTeacherOrCollaboratorsIn(Member teacher, Collection<Member> collaborators);
}
