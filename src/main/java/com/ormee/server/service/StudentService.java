package com.ormee.server.service;

import com.ormee.server.dto.student.StudentSignInDto;
import com.ormee.server.dto.student.StudentInfoDto;
import com.ormee.server.dto.student.StudentTokenDto;
import com.ormee.server.exception.CustomException;
import com.ormee.server.exception.ExceptionType;
import com.ormee.server.model.Student;
import com.ormee.server.repository.StudentRepository;
import org.springframework.stereotype.Service;

@Service
public class StudentService {
    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public Student signUp(StudentInfoDto signUpDto) {
        Student student = Student.builder()
                .name(signUpDto.getName())
                .description(signUpDto.getDescription())
                .email(signUpDto.getEmail())
                .password(signUpDto.getPassword())
                .phoneNumber(signUpDto.getPhoneNumber())
                .build();
        return studentRepository.save(student);
    }

    public StudentTokenDto signIn(StudentSignInDto signInDto) {
        Student student = studentRepository.findByEmail(signInDto.getEmail()).orElseThrow(() -> new CustomException(ExceptionType.STUDENT_NOT_FOUND_EXCEPTION));

        if(!signInDto.getPassword().equals(student.getPassword())) {
            throw new CustomException(ExceptionType.PASSWORD_INVALID_EXCEPTION);
        }

        return new StudentTokenDto();
    }

    public StudentInfoDto getStudentInfo(String email) {
        Student student = studentRepository.findByEmail(email).orElseThrow(() -> new CustomException(ExceptionType.STUDENT_NOT_FOUND_EXCEPTION));

        return StudentInfoDto.builder()
                .name(student.getName())
                .description(student.getDescription())
                .email(student.getEmail())
                .phoneNumber(student.getPhoneNumber())
                .build();
    }

    // student update

    public void delete(String email) {
        Student student = studentRepository.findByEmail(email).orElseThrow(() -> new CustomException(ExceptionType.STUDENT_NOT_FOUND_EXCEPTION));
        studentRepository.delete(student);
    }
}
