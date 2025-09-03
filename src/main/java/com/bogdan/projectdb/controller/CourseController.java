package com.bogdan.projectdb.controller;

import com.bogdan.projectdb.dto.CourseDto;
import com.bogdan.projectdb.mapper.CourseMapper;
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
    private final CourseMapper courseMapper;
    private final SqlSecurityConfig sqlSecurityConfig;

    public CourseController(CourseService courseService, CourseMapper courseMapper, SqlSecurityConfig sqlSecurityConfig) {
        this.courseService = courseService;
        this.courseMapper = courseMapper;
        this.sqlSecurityConfig = sqlSecurityConfig;
    }

    @PostMapping
    public ResponseEntity<CourseDto> createCourse(@Valid @RequestBody CourseDto courseDto) {
        validateCourseDto(courseDto);
        Course course = courseMapper.toEntity(courseDto);
        Course savedCourse = courseService.createCourse(course);
        return ResponseEntity.ok(courseMapper.toDto(savedCourse));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CourseDto> getCourse(@PathVariable Integer id) {
        Course course = courseService.getCourseById(id);
        return ResponseEntity.ok(courseMapper.toDto(course));
    }

    @GetMapping
    public ResponseEntity<?> getAllCourses() {
        return ResponseEntity.ok(courseMapper.toDtoList(courseService.getAllCourses()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CourseDto> updateCourse(
            @PathVariable Integer id, 
            @Valid @RequestBody CourseDto courseDto) {
        validateCourseDto(courseDto);
        Course course = courseMapper.toEntity(courseDto);
        Course updatedCourse = courseService.updateCourse(id, course);
        return ResponseEntity.ok(courseMapper.toDto(updatedCourse));
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
        
        return ResponseEntity.ok(courseMapper.toDtoList(courseService.searchCourses(query)));
    }

    private void validateCourseDto(CourseDto courseDto) {
        if (!sqlSecurityConfig.isSqlInjectionSafe(courseDto.getCourseCode()) || 
            !COURSE_CODE_PATTERN.matcher(courseDto.getCourseCode()).matches()) {
            throw new IllegalArgumentException("Invalid course code format");
        }

        if (!sqlSecurityConfig.isSqlInjectionSafe(courseDto.getCourseName()) || 
            !COURSE_NAME_PATTERN.matcher(courseDto.getCourseName()).matches()) {
            throw new IllegalArgumentException("Invalid course name format");
        }

        if (courseDto.getCredits() < 0 || courseDto.getCredits() > 6) {
            throw new IllegalArgumentException("Credits must be between 0 and 6");
        }

        if (courseDto.getDescription() != null && 
            !sqlSecurityConfig.isSqlInjectionSafe(courseDto.getDescription())) {
            throw new IllegalArgumentException("Invalid description format");
        }
    }
} 