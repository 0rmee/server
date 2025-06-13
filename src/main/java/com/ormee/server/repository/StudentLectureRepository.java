package com.ormee.server.repository;

import com.ormee.server.model.Lecture;
import com.ormee.server.model.StudentLecture;
import com.ormee.server.model.member.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentLectureRepository extends JpaRepository<StudentLecture, Long> {
    Optional<StudentLecture> findByStudentAndLecture(Member student, Lecture lecture);
    Page<StudentLecture> findAllByLectureOrderByStudent_Name(Lecture lecture, Pageable pageable);
    Page<StudentLecture> findAllByLectureOrderByCreatedAtDesc(Lecture lecture, Pageable pageable);
}
