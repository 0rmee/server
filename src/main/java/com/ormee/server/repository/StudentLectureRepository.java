package com.ormee.server.repository;

import com.ormee.server.model.Lecture;
import com.ormee.server.model.StudentLecture;
import com.ormee.server.model.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentLectureRepository extends JpaRepository<StudentLecture, Long> {
    Optional<StudentLecture> findByStudentAndLecture(Member student, Lecture lecture);
    List<StudentLecture> findAllByLecture(Lecture lecture);
}
