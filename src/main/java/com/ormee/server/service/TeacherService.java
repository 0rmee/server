package com.ormee.server.service;

import com.ormee.server.dto.TeacherDto;
import com.ormee.server.model.Teacher;
import com.ormee.server.repository.TeacherRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class TeacherService {

    private final TeacherRepository teacherRepository;

    public TeacherService(TeacherRepository teacherRepository) {
        this.teacherRepository = teacherRepository;
    }

    public TeacherDto getTeacherById(UUID id) {
        Teacher teacher = teacherRepository.findById(id).orElse(null);
        if(teacher == null) {return null;}
        return TeacherDto.builder()
                .name(teacher != null ? teacher.getName() : null)
                .email(teacher != null ? teacher.getEmail() : null)
                .phoneNumber(teacher != null ? teacher.getPhoneNumber() : null)
                .build();
    }
}
