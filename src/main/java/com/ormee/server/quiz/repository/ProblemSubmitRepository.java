package com.ormee.server.quiz.repository;

import com.ormee.server.member.domain.Member;
import com.ormee.server.quiz.domain.Problem;
import com.ormee.server.quiz.domain.ProblemSubmit;
import com.ormee.server.quiz.domain.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface ProblemSubmitRepository extends JpaRepository<ProblemSubmit, Long> {
    List<ProblemSubmit> findAllByProblem(Problem problem);
    Long countAllByProblemAndContentLike(Problem problem, String content);
    long countAllByProblem(Problem problem);
    boolean existsByProblemAndStudent(Problem problem, Member student);
    boolean existsByStudentAndProblem_Quiz(Member student, Quiz quiz);
    void deleteAllByProblem_Quiz(Quiz quiz);
    List<ProblemSubmit> findAllByProblem_QuizAndStudent(Quiz quiz, Member student);
    @Query("select distinct ps.student.id from ProblemSubmit ps where ps.problem.quiz.id = :quizId")
    Set<Long> findSubmittedStudentIdsByQuizId(Long quizId);
}
