package com.bogdan.projectdb.controller;

import com.bogdan.projectdb.dto.InstructorDto;
import com.bogdan.projectdb.mapper.InstructorMapper;
import com.bogdan.projectdb.model.Instructor;
import com.bogdan.projectdb.security.SqlSecurityConfig;
import com.bogdan.projectdb.service.InstructorService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/instructors")
public class InstructorController {

    private static final Pattern NAME_PATTERN = Pattern.compile("^[a-zA-Z\\s-]{2,50}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^[0-9+\\-\\s\\(\\)]{7,20}$");
    private static final Pattern DEPARTMENT_PATTERN = Pattern.compile("^[a-zA-Z\\s&-]{2,100}$");

    private final InstructorService instructorService;
    private final InstructorMapper instructorMapper;
    private final SqlSecurityConfig sqlSecurityConfig;

    public InstructorController(InstructorService instructorService, InstructorMapper instructorMapper, SqlSecurityConfig sqlSecurityConfig) {
        this.instructorService = instructorService;
        this.instructorMapper = instructorMapper;
        this.sqlSecurityConfig = sqlSecurityConfig;
    }

    @GetMapping("/{id}")
    public ResponseEntity<InstructorDto> getInstructor(@PathVariable Integer id) {
        Instructor instructor = instructorService.findInstructorById(id);
        return ResponseEntity.ok(instructorMapper.toDto(instructor));
    }


    @GetMapping
    public ResponseEntity<List<InstructorDto>> getAllInstructors() {
        List<Instructor> instructors = instructorService.getAllInstructors();
        return ResponseEntity.ok(instructorMapper.toDtoList(instructors));
    }

    @PostMapping
    public ResponseEntity<InstructorDto> createInstructor(@Valid @RequestBody InstructorDto instructorDto) {
        validateInstructor(instructorDto);
        Instructor instructor = instructorMapper.toEntity(instructorDto);
        Instructor savedInstructor = instructorService.createInstructor(instructor);
        return ResponseEntity.ok(instructorMapper.toDto(savedInstructor));
    }

    @PutMapping("/{id}")
    public ResponseEntity<InstructorDto> updateInstructor(@PathVariable Integer id, @Valid @RequestBody InstructorDto instructorDto) {
        validateInstructor(instructorDto);
        Instructor instructor = instructorMapper.toEntity(instructorDto);
        Instructor updatedInstructor = instructorService.updateInstructor(id, instructor);
        return ResponseEntity.ok(instructorMapper.toDto(updatedInstructor));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteInstructor(@PathVariable Integer id) {
        instructorService.deleteInstructor(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchInstructors(
            @RequestParam(required = false) String query) {
        
        if (query != null) {
            if (!sqlSecurityConfig.isSqlInjectionSafe(query)) {
                return ResponseEntity.badRequest().build();
            }
        }
        
        return ResponseEntity.ok(instructorService.searchInstructors(query));
    }

    private void validateInstructor(InstructorDto instructorDto) {
        if (!sqlSecurityConfig.isSqlInjectionSafe(instructorDto.getFirstName()) ||
            !NAME_PATTERN.matcher(instructorDto.getFirstName()).matches()) {
            throw new IllegalArgumentException("Invalid first name format");
        }

        if (!sqlSecurityConfig.isSqlInjectionSafe(instructorDto.getLastName()) ||
            !NAME_PATTERN.matcher(instructorDto.getLastName()).matches()) {
            throw new IllegalArgumentException("Invalid last name format");
        }

        if (!sqlSecurityConfig.isSqlInjectionSafe(instructorDto.getEmail()) ||
            !EMAIL_PATTERN.matcher(instructorDto.getEmail()).matches()) {
            throw new IllegalArgumentException("Invalid email format");
        }

        if (!sqlSecurityConfig.isSqlInjectionSafe(instructorDto.getPhoneNumber()) ||
            !PHONE_PATTERN.matcher(instructorDto.getPhoneNumber()).matches()) {
            throw new IllegalArgumentException("Invalid phone number format");
        }

        if (!sqlSecurityConfig.isSqlInjectionSafe(instructorDto.getDepartment()) ||
            !DEPARTMENT_PATTERN.matcher(instructorDto.getDepartment()).matches()) {
            throw new IllegalArgumentException("Invalid department format");
        }
    }
} 