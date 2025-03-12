package com.ormee.server.repository;

import com.ormee.server.model.Problem;
import com.ormee.server.model.ProblemSubmit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProblemSubmitRepository extends JpaRepository<ProblemSubmit, Long> {
    Optional<ProblemSubmit> findByProblemAndAuthorAndPassword(Problem problem, String author, String password);
    List<ProblemSubmit> findAllByProblem(Problem problem);
    boolean existsByProblemAndAuthorAndPassword(Problem problem, String author, String password);
    Long countAllByProblemAndContentLike(Problem problem, String content);

    long countAllByProblem(Problem problem);
}
