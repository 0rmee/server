package com.ormee.server.lecture.repository;

import com.ormee.server.lecture.domain.Lecture;
import com.ormee.server.lecture.domain.StudentLecture;
import com.ormee.server.member.domain.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface StudentLectureRepository extends JpaRepository<StudentLecture, Long> {
    List<StudentLecture> findByStudentAndLecture(Member student, Lecture lecture);
    Page<StudentLecture> findAllByLectureAndBlockedFalseOrderByStudent_Name(Lecture lecture, Pageable pageable);
    Page<StudentLecture> findAllByLectureAndBlockedFalseOrderByCreatedAtDesc(Lecture lecture, Pageable pageable);
    List<StudentLecture> findAllByLectureAndBlockedTrueOrderByUpdatedAtDesc(Lecture lecture);
    List<StudentLecture> findAllByStudentAndLecture_StartDateBeforeAndLecture_DueDateAfter(Member student, LocalDateTime now1, LocalDateTime now2);
    List<StudentLecture> findAllByStudentOrderByLecture_StartDateDesc(Member student);

    List<StudentLecture> findAllByLecture(Lecture lecture);
    List<StudentLecture> findAllByStudent(Member student);
}
