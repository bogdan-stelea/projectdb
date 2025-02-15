package com.bogdan.projectdb.service.impl;

import com.bogdan.projectdb.model.Course;
import com.bogdan.projectdb.repository.CourseRepository;
import com.bogdan.projectdb.audit.AuditService;
import com.bogdan.projectdb.service.CourseService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class CourseServiceImpl implements CourseService {
    private final CourseRepository courseRepository;
    private final AuditService auditService;

    public CourseServiceImpl(CourseRepository courseRepository, AuditService auditService) {
        this.courseRepository = courseRepository;
        this.auditService = auditService;
    }

    public Course getCourseById(Integer id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Course not found"));
    }

    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    public List<Course> searchCourses(String query) {
        return courseRepository.findAll().stream()
                .filter(c -> query == null || c.getCourseName().toLowerCase().contains(query.toLowerCase()) || c.getCourseCode().equalsIgnoreCase(query))
                .toList();
    }

    @Transactional
    public Course createCourse(Course course) {
        Course savedCourse = courseRepository.save(course);
        auditService.logActivity(
            "Course",
            savedCourse.getId(),
            "CREATE",
            null,
            savedCourse,
            "system"
        );
        return savedCourse;
    }

    @Transactional
    public Course updateCourse(Integer id, Course course) {
        Course existingCourse = courseRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Course not found"));
        
        Course oldValue = new Course();

        existingCourse.setCourseName(course.getCourseName());
        existingCourse.setCourseCode(course.getCourseCode());
        existingCourse.setCredits(course.getCredits());
        existingCourse.setInstructor(course.getInstructor());
        
        Course updatedCourse = courseRepository.save(existingCourse);
        
        auditService.logActivity(
            "Course",
            updatedCourse.getId(),
            "UPDATE",
            oldValue,
            updatedCourse,
            "system"
        );
        
        return updatedCourse;
    }

    @Transactional
    public void deleteCourse(Integer id) {
        Course course = courseRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Course not found"));
            
        auditService.logActivity(
            "Course",
            id,
            "DELETE",
            course,
            null,
            "system"
        );
        
        courseRepository.deleteById(id);
    }
} 