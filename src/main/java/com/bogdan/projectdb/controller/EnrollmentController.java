package com.bogdan.projectdb.controller;

import com.bogdan.projectdb.dto.EnrollmentDto;
import com.bogdan.projectdb.model.Enrollment;
import com.bogdan.projectdb.service.EnrollmentService;
import com.bogdan.projectdb.security.SqlSecurityConfig;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/enrollments")
@Validated
public class EnrollmentController {
    
    private static final Pattern GRADE_PATTERN = 
        Pattern.compile("^(100|[1-9]?[0-9])$");
    private static final Pattern STATUS_PATTERN = 
        Pattern.compile("^(ACTIVE|COMPLETED|DROPPED|PENDING)$");
    
    private final EnrollmentService enrollmentService;
    private final SqlSecurityConfig sqlSecurityConfig;

    public EnrollmentController(EnrollmentService enrollmentService, 
                              SqlSecurityConfig sqlSecurityConfig) {
        this.enrollmentService = enrollmentService;
        this.sqlSecurityConfig = sqlSecurityConfig;
    }

    @PostMapping("/students/{studentId}/courses/{courseId}")
    public ResponseEntity<EnrollmentDto> enrollStudentInCourse(
            @PathVariable Integer studentId,
            @PathVariable Integer courseId) {
        return ResponseEntity.ok(enrollmentService.enrollStudentInCourse(studentId, courseId));
    }

    @GetMapping("/students/{studentId}")
    public ResponseEntity<List<EnrollmentDto>> getStudentEnrollments(@PathVariable Integer studentId) {
        return ResponseEntity.ok(enrollmentService.getStudentEnrollments(studentId));
    }

    @DeleteMapping("/{enrollmentId}")
    public ResponseEntity<Void> cancelEnrollment(@PathVariable Integer enrollmentId) {
        enrollmentService.cancelEnrollment(enrollmentId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Enrollment> getEnrollment(@PathVariable Integer id) {
        return ResponseEntity.ok(enrollmentService.findEnrollmentById(id));
    }

    @GetMapping
    public ResponseEntity<?> getAllEnrollments() {
        return ResponseEntity.ok(enrollmentService.getAllEnrollments());
    }

    @PutMapping("/{enrollmentId}/{grade}/instructor/{instructorId}")
    public ResponseEntity<EnrollmentDto> updateGrade(
            @PathVariable Integer enrollmentId,
            @PathVariable Integer instructorId,
            @PathVariable Integer grade) {
        return ResponseEntity.ok(enrollmentService.updateGrade(enrollmentId, instructorId, grade));
    }

    @GetMapping("/courses/{courseId}/students")
    public ResponseEntity<List<EnrollmentDto>> getCourseEnrollments(@PathVariable Integer courseId) {
        return ResponseEntity.ok(enrollmentService.getCourseEnrollments(courseId));
    }

    @PostMapping
    public ResponseEntity<Enrollment> createEnrollment(@Valid @RequestBody Enrollment enrollment) {
        validateEnrollment(enrollment);
        return ResponseEntity.ok(enrollmentService.createEnrollment(enrollment));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Enrollment> updateEnrollment(
            @PathVariable Integer id,
            @Valid @RequestBody Enrollment enrollment) {
        validateEnrollment(enrollment);
        return ResponseEntity.ok(enrollmentService.updateEnrollment(id, enrollment));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEnrollment(@PathVariable Integer id) {
        enrollmentService.deleteEnrollment(id);
        return ResponseEntity.noContent().build();
    }

    private void validateEnrollment(Enrollment enrollment) {
        if (enrollment.getEnrollmentNumber() != null &&
            !sqlSecurityConfig.isSqlInjectionSafe(enrollment.getEnrollmentNumber())) {
            throw new IllegalArgumentException("Invalid enrollment number format");
        }

        if (enrollment.getStatus() != null) {
            String status = enrollment.getStatus().toString();
            if (!sqlSecurityConfig.isSqlInjectionSafe(status) || 
                !STATUS_PATTERN.matcher(status).matches()) {
                throw new IllegalArgumentException("Invalid enrollment status");
            }
        }

        if (enrollment.getGrade() != null) {
            String grade = String.valueOf(enrollment.getGrade());
            if (!sqlSecurityConfig.isSqlInjectionSafe(grade) || 
                !GRADE_PATTERN.matcher(grade).matches()) {
                throw new IllegalArgumentException("Invalid grade format");
            }
        }
    }
} 