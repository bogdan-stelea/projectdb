package com.bogdan.projectdb.controller;

import com.bogdan.projectdb.model.Course;
import com.bogdan.projectdb.security.SqlSecurityConfig;
import com.bogdan.projectdb.service.CourseService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/courses")
@Validated
public class CourseController {
    
    private static final Pattern COURSE_CODE_PATTERN = Pattern.compile("^[A-Z]{2,4}\\d{3,4}$");
    private static final Pattern COURSE_NAME_PATTERN = Pattern.compile("^[a-zA-Z0-9\\s-]{3,100}$");
    
    private final CourseService courseService;
    private final SqlSecurityConfig sqlSecurityConfig;

    public CourseController(CourseService courseService, SqlSecurityConfig sqlSecurityConfig) {
        this.courseService = courseService;
        this.sqlSecurityConfig = sqlSecurityConfig;
    }

    @PostMapping
    public ResponseEntity<Course> createCourse(@Valid @RequestBody Course course) {
        validateCourse(course);
        return ResponseEntity.ok(courseService.createCourse(course));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Course> getCourse(@PathVariable Integer id) {
        return ResponseEntity.ok(courseService.getCourseById(id));
    }

    @GetMapping
    public ResponseEntity<?> getAllCourses() {
        return ResponseEntity.ok(courseService.getAllCourses());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Course> updateCourse(
            @PathVariable Integer id, 
            @Valid @RequestBody Course course) {
        validateCourse(course);
        return ResponseEntity.ok(courseService.updateCourse(id, course));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCourse(@PathVariable Integer id) {
        courseService.deleteCourse(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchCourses(
            @RequestParam(required = false) String query) {
        
        if (query != null) {
            if (!sqlSecurityConfig.isSqlInjectionSafe(query)) {
                return ResponseEntity.badRequest().build();
            }
            if (!COURSE_NAME_PATTERN.matcher(query).matches()) {
                return ResponseEntity.badRequest().build();
            }
        }
        
        return ResponseEntity.ok(courseService.searchCourses(query));
    }

    private void validateCourse(Course course) {
        if (!sqlSecurityConfig.isSqlInjectionSafe(course.getCourseCode()) || 
            !COURSE_CODE_PATTERN.matcher(course.getCourseCode()).matches()) {
            throw new IllegalArgumentException("Invalid course code format");
        }

        if (!sqlSecurityConfig.isSqlInjectionSafe(course.getCourseName()) || 
            !COURSE_NAME_PATTERN.matcher(course.getCourseName()).matches()) {
            throw new IllegalArgumentException("Invalid course name format");
        }

        if (course.getCredits() < 0 || course.getCredits() > 6) {
            throw new IllegalArgumentException("Credits must be between 0 and 6");
        }

        if (course.getDescription() != null && 
            !sqlSecurityConfig.isSqlInjectionSafe(course.getDescription())) {
            throw new IllegalArgumentException("Invalid description format");
        }
    }
} 