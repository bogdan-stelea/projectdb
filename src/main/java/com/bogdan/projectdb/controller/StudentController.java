package com.bogdan.projectdb.controller;

import com.bogdan.projectdb.dto.StudentDto;
import com.bogdan.projectdb.mapper.StudentMapper;
import com.bogdan.projectdb.model.Student;
import com.bogdan.projectdb.security.ApplicationContext;
import com.bogdan.projectdb.security.SqlSecurityConfig;
import com.bogdan.projectdb.service.StudentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/students")
public class StudentController {

    private static final Pattern NAME_PATTERN = Pattern.compile("^[a-zA-Z\\s-]{2,50}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^[0-9+\\-\\s\\(\\)]{7,20}$");

    private final StudentService studentService;
    private final StudentMapper studentMapper;
    private final ApplicationContext applicationContext;
    private final SqlSecurityConfig sqlSecurityConfig;

    public StudentController(StudentService studentService, StudentMapper studentMapper,
                           ApplicationContext applicationContext, SqlSecurityConfig sqlSecurityConfig) {
        this.studentService = studentService;
        this.studentMapper = studentMapper;
        this.applicationContext = applicationContext;
        this.sqlSecurityConfig = sqlSecurityConfig;
    }

    @GetMapping("/{id}")
    public ResponseEntity<StudentDto> getStudent(@PathVariable Integer id) {
        Student student = studentService.findStudentById(id);
        return ResponseEntity.ok(studentMapper.toDto(student));
    }

    @GetMapping
    public ResponseEntity<List<StudentDto>> getAllStudents() {
        List<Student> students = studentService.getAllStudents();
        return ResponseEntity.ok(studentMapper.toDtoList(students));
    }

    @PostMapping
    public ResponseEntity<Object> createStudent(@Valid @RequestBody StudentDto studentDto) {
        try {
            validateStudent(studentDto);
            Student student = studentMapper.toEntity(studentDto);
            Student savedStudent = studentService.createStudent(student);
            return new ResponseEntity<>(studentMapper.toDto(savedStudent), HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Invalid student data");
            response.put("message", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Failed to create student");
            response.put("message", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> updateStudent(@PathVariable Integer id, @Valid @RequestBody StudentDto studentDto) {
        try {
            validateStudent(studentDto);
            Student student = studentMapper.toEntity(studentDto);
            Student updatedStudent = studentService.updateStudent(id, student);
            return new ResponseEntity<>(studentMapper.toDto(updatedStudent), HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Invalid student data");
            response.put("message", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Failed to create student");
            response.put("message", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteStudent(@PathVariable Integer id) {
        studentService.deleteStudent(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/search")
    public ResponseEntity<?> search(
            @RequestParam(required = false) String query) {

        if (query != null) {
            if (!sqlSecurityConfig.isSqlInjectionSafe(query)) {
                return ResponseEntity.badRequest().build();
            }
            if (!NAME_PATTERN.matcher(query).matches()) {
                return ResponseEntity.badRequest().build();
            }
        }

        return ResponseEntity.ok(studentService.searchStudents(query));
    }

    private void validateStudent(StudentDto student) {
        if (!sqlSecurityConfig.isSqlInjectionSafe(student.getFirstName()) ||
                !NAME_PATTERN.matcher(student.getFirstName()).matches()) {
            throw new IllegalArgumentException("Invalid first name format");
        }

        if (!sqlSecurityConfig.isSqlInjectionSafe(student.getLastName()) ||
                !NAME_PATTERN.matcher(student.getLastName()).matches()) {
            throw new IllegalArgumentException("Invalid last name format");
        }

        if (!sqlSecurityConfig.isSqlInjectionSafe(student.getEmail()) ||
                !EMAIL_PATTERN.matcher(student.getEmail()).matches()) {
            throw new IllegalArgumentException("Invalid email format");
        }

        if (!sqlSecurityConfig.isSqlInjectionSafe(student.getPhoneNumber()) ||
                !PHONE_PATTERN.matcher(student.getPhoneNumber()).matches()) {
            throw new IllegalArgumentException("Invalid phone number format");
        }
    }
}
