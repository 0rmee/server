package com.ormee.server.quiz.service;

import com.ormee.server.global.exception.CustomException;
import com.ormee.server.global.exception.ExceptionType;
import com.ormee.server.member.domain.Member;
import com.ormee.server.member.repository.MemberRepository;
import com.ormee.server.quiz.domain.Problem;
import com.ormee.server.quiz.domain.ProblemSubmit;
import com.ormee.server.quiz.domain.Quiz;
import com.ormee.server.quiz.dto.ProblemDto;
import com.ormee.server.quiz.dto.StudentQuizResultDto;
import com.ormee.server.quiz.dto.SubmitDto;
import com.ormee.server.quiz.repository.ProblemRepository;
import com.ormee.server.quiz.repository.ProblemSubmitRepository;
import com.ormee.server.quiz.repository.QuizRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ProblemSubmitService {
    private final ProblemSubmitRepository problemSubmitRepository;
    private final ProblemRepository problemRepository;
    private final QuizRepository quizRepository;
    private final MemberRepository studentRepository;

    public ProblemSubmitService(ProblemSubmitRepository problemSubmitRepository, ProblemRepository problemRepository, QuizRepository quizRepository, MemberRepository studentRepository) {
        this.problemSubmitRepository = problemSubmitRepository;
        this.problemRepository = problemRepository;
        this.quizRepository = quizRepository;
        this.studentRepository = studentRepository;
    }


    public void submit(List<SubmitDto> submissions, Authentication authentication) {
        Member student = studentRepository.findByUsername(authentication.getName()).orElseThrow(() -> new CustomException(ExceptionType.MEMBER_NOT_FOUND_EXCEPTION));

        for(SubmitDto submitDto : submissions) {
            Problem problem = problemRepository.findById(submitDto.getProblemId()).orElseThrow(() -> new CustomException(ExceptionType.PROBLEM_NOT_FOUND_EXCEPTION));

            if(problemSubmitRepository.existsByProblemAndStudent(problem, student))
                throw new CustomException(ExceptionType.SUBMIT_FORBIDDEN_EXCEPTION);

            ProblemSubmit problemSubmit = ProblemSubmit.builder()
                    .problem(problem)
                    .student(student)
                    .content(submitDto.getContent())
                    .build();
            problemSubmitRepository.save(problemSubmit);
        }
    }

    @Transactional(readOnly = true)
    public StudentQuizResultDto getStudentResult(Long quizId, Authentication authentication) {
        Quiz quiz = quizRepository.findById(quizId).orElseThrow(() -> new CustomException(ExceptionType.QUIZ_NOT_FOUND_EXCEPTION));
        Member student = studentRepository.findByUsername(authentication.getName()).orElseThrow(() -> new CustomException(ExceptionType.MEMBER_NOT_FOUND_EXCEPTION));

        List<Problem> problems = problemRepository.findAllByQuiz(quiz);
        List<ProblemSubmit> submissions = problemSubmitRepository.findAllByProblem_QuizAndStudent(quiz, student);
        Map<Long, ProblemSubmit> submitMap = submissions.stream()
                .collect(Collectors.toMap(problemSubmit -> problemSubmit.getProblem().getId(), Function.identity()));

        List<ProblemDto> problemDtos = new ArrayList<>();
        Integer correct = 0;
        for(Problem problem : problems) {
            ProblemSubmit problemSubmit = submitMap.get(problem.getId());
            if (problemSubmit == null) {
                throw new CustomException(ExceptionType.SUBMIT_NOT_FOUND_EXCEPTION);
            }
            ProblemDto problemDto = ProblemDto.builder()
                    .id(problem.getId())
                    .content(problem.getContent())
                    .type(problem.getType().toString())
                    .items(problem.getItems())
                    .answer(problem.getAnswer())
                    .submission(problemSubmit.getContent())
                    .isCorrect(problem.getAnswer().equalsIgnoreCase(problemSubmit.getContent()))
                    .build();
            if(problemDto.getIsCorrect()) {
                correct++;
            }
            problemDtos.add(problemDto);
        }

        return StudentQuizResultDto.builder()
                .correct(correct)
                .problemDtos(problemDtos)
                .build();
    }

    public void deleteAllByQuiz(Quiz quiz) {
        problemSubmitRepository.deleteAllByProblem_Quiz(quiz);
    }
}
