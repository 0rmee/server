package com.ormee.server.repository;

import com.ormee.server.model.Problem;
import com.ormee.server.model.Submit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubmitRepository extends JpaRepository<Submit, Long> {
    Optional<Submit> findByProblemAndAuthorAndPassword(Problem problem, String author, String password);
    List<Submit> findAllByProblem(Problem problem);
    boolean existsByProblemAndAuthorAndPassword(Problem problem, String author, String password);
    Long countAllByProblemAndContentLike(Problem problem, String content);

    long countAllByProblem(Problem problem);
}
