package com.ormee.server.service;

import com.ormee.server.config.CodeGenerator;
import com.ormee.server.dto.SignInDto;
import com.ormee.server.dto.SignUpDto;
import com.ormee.server.dto.TeacherDto;
import com.ormee.server.model.Teacher;
import com.ormee.server.repository.TeacherRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class TeacherService {

    private final TeacherRepository teacherRepository;
    private final CodeGenerator codeGenerator;

    public TeacherService(TeacherRepository teacherRepository, CodeGenerator codeGenerator) {
        this.teacherRepository = teacherRepository;
        this.codeGenerator = codeGenerator;
    }

    public TeacherDto getTeacherById(UUID id) {
        Teacher teacher = teacherRepository.findById(id).orElse(null);
        if(teacher == null) {return null;}
        return TeacherDto.builder()
                .name(teacher != null ? teacher.getName() : null)
                .email(teacher != null ? teacher.getEmail() : null)
                .phoneNumber(teacher != null ? teacher.getPhoneNumber() : null)
                .image(teacher != null ? teacher.getImage() : null)
                .build();
    }

    public void signUp(SignUpDto signUpDto) {
        teacherRepository.save(SignUpDto.toEntity(signUpDto, codeGenerator.generateCode()));
    }

    public Boolean checkTeacherPassword(SignInDto signInDto) {
        Teacher teacher = teacherRepository.findByCode(signInDto.getCode()).orElse(null);
        if (teacher == null) {
            return false;
        }
        return teacher.getPassword().equals(signInDto.getPassword());
    }

}
