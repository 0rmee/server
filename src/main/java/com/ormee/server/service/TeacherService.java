package com.ormee.server.service;

import com.ormee.server.config.CodeGenerator;
import com.ormee.server.dto.member.SignInDto;
import com.ormee.server.dto.member.SignUpDto;
import com.ormee.server.dto.member.TeacherDto;
import com.ormee.server.exception.CustomException;
import com.ormee.server.exception.ExceptionType;
import com.ormee.server.model.Teacher;
import com.ormee.server.repository.TeacherRepository;
import org.springframework.stereotype.Service;

@Service
public class TeacherService {

    private final TeacherRepository teacherRepository;
    private final CodeGenerator codeGenerator;

    public TeacherService(TeacherRepository teacherRepository, CodeGenerator codeGenerator) {
        this.teacherRepository = teacherRepository;
        this.codeGenerator = codeGenerator;
    }

    public TeacherDto getTeacherById(int code) {
        Teacher teacher = teacherRepository.findByCode(code).orElseThrow(() -> new CustomException(ExceptionType.TEACHER_NOT_FOUND_EXCEPTION));
        return TeacherDto.builder()
                .name(teacher != null ? teacher.getName() : null)
                .nameEng(teacher != null ? teacher.getNameEng() : null)
                .email(teacher != null ? teacher.getEmail() : null)
                .phoneNumber(teacher != null ? teacher.getPhoneNumber() : null)
                .introduce(teacher != null ? teacher.getIntroduce() : null)
                .image(teacher != null ? teacher.getImage() : null)
                .build();
    }

    public Teacher updateTeacherById(int code, TeacherDto teacherDto) {
        Teacher teacher = teacherRepository.findByCode(code).orElseThrow(() -> new CustomException(ExceptionType.TEACHER_NOT_FOUND_EXCEPTION));
        if (teacherDto.getName() != null) teacher.setName(teacherDto.getName());
        if (teacherDto.getNameEng() != null) teacher.setNameEng(teacherDto.getNameEng());
        if (teacherDto.getEmail() != null) teacher.setEmail(teacherDto.getEmail());
        if (teacherDto.getPhoneNumber() != null) teacher.setPhoneNumber(teacherDto.getPhoneNumber());
        if (teacherDto.getIntroduce() != null) teacher.setIntroduce(teacherDto.getIntroduce());
        if (teacherDto.getImage() != null) teacher.setImage(teacherDto.getImage());

        return teacherRepository.save(teacher);
    }

    public void signUp(SignUpDto signUpDto) {
        teacherRepository.save(SignUpDto.toEntity(signUpDto, codeGenerator.generateCode()));
    }

    public Boolean checkTeacherPassword(SignInDto signInDto) {
        Teacher teacher = teacherRepository.findByCode(signInDto.getCode()).orElseThrow(() -> new CustomException(ExceptionType.TEACHER_NOT_FOUND_EXCEPTION));
        return teacher.getPassword().equals(signInDto.getPassword());
    }

}
