package com.bogdan.projectdb.mapper;

import com.bogdan.projectdb.dto.InstructorDto;
import com.bogdan.projectdb.model.Instructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class InstructorMapper {

    public InstructorDto toDto(Instructor instructor) {
        if (instructor == null) {
            return null;
        }

        InstructorDto dto = new InstructorDto();
        dto.setFirstName(instructor.getFirstName());
        dto.setLastName(instructor.getLastName());
        dto.setEmail(instructor.getMaskedEmail());
        dto.setPhoneNumber(instructor.getMaskedPhoneNumber());
        dto.setDepartment(instructor.getDepartment());
        dto.setOfficeAddress(instructor.getMaskedOfficeAddress());
        return dto;
    }

    public Instructor toEntity(InstructorDto dto) {
        if (dto == null) {
            return null;
        }

        Instructor instructor = new Instructor();
        instructor.setFirstName(dto.getFirstName());
        instructor.setLastName(dto.getLastName());
        instructor.setEmail(dto.getEmail());
        instructor.setPhoneNumber(dto.getPhoneNumber());
        instructor.setDepartment(dto.getDepartment());
        instructor.setOfficeAddress(dto.getOfficeAddress());
        return instructor;
    }

    public List<InstructorDto> toDtoList(List<Instructor> instructors) {
        return instructors.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
} 