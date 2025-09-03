package com.bogdan.projectdb.mapper;

import com.bogdan.projectdb.dto.StudentDto;
import com.bogdan.projectdb.model.Student;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class StudentMapper {

    public StudentDto toDto(Student student) {
        if (student == null) {
            return null;
        }

        StudentDto dto = new StudentDto();
        dto.setFirstName(student.getFirstName());
        dto.setLastName(student.getLastName());
        dto.setEmail(student.getMaskedEmail());
        dto.setPhoneNumber(student.getMaskedPhoneNumber());
        dto.setDateOfBirth(student.getDateOfBirth());
        dto.setAddress(student.getMaskedAddress());
        return dto;
    }

    public Student toEntity(StudentDto dto) {
        if (dto == null) {
            return null;
        }

        Student student = new Student();
        student.setFirstName(dto.getFirstName());
        student.setLastName(dto.getLastName());
        student.setEmail(dto.getEmail());
        student.setPhoneNumber(dto.getPhoneNumber());
        student.setDateOfBirth(dto.getDateOfBirth());
        student.setAddress(dto.getAddress());
        return student;
    }

    public List<StudentDto> toDtoList(List<Student> students) {
        return students.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
} 