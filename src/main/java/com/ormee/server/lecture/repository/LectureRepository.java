package com.ormee.server.lecture.repository;

import com.ormee.server.lecture.domain.Lecture;
import com.ormee.server.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface LectureRepository extends JpaRepository<Lecture, Long> {
    List<Lecture> findAllByTeacherOrCollaboratorsIn(Member teacher, Collection<Member> collaborators);
}
