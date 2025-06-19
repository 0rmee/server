package com.ormee.server.repository;

import com.ormee.server.model.Problem;
import com.ormee.server.model.ProblemSubmit;
import com.ormee.server.model.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProblemSubmitRepository extends JpaRepository<ProblemSubmit, Long> {
    List<ProblemSubmit> findAllByProblem(Problem problem);
    Long countAllByProblemAndContentLike(Problem problem, String content);
    long countAllByProblem(Problem problem);
    Optional<ProblemSubmit> findByProblemAndStudent(Problem problem, Member student);
    boolean existsByProblemAndStudent(Problem problem, Member student);
}
