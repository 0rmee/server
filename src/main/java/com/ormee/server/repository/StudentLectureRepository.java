package com.ormee.server.repository;

import com.ormee.server.model.Lecture;
import com.ormee.server.model.Student;
import com.ormee.server.model.StudentLecture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentLectureRepository extends JpaRepository<StudentLecture, Long> {
    Optional<StudentLecture> findByStudentAndLecture(Student student, Lecture lecture);
    List<StudentLecture> findAllByLecture(Lecture lecture);
}
