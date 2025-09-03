package com.bogdan.projectdb.service.impl;

import com.bogdan.projectdb.model.Course;
import com.bogdan.projectdb.repository.CourseRepository;
import com.bogdan.projectdb.service.CourseService;
import com.bogdan.projectdb.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class CourseServiceImpl implements CourseService {
    private final CourseRepository courseRepository;

    public CourseServiceImpl(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    public Course getCourseById(Integer id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course", "id", id));
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
        return courseRepository.save(course);
    }

    @Transactional
    public Course updateCourse(Integer id, Course course) {
        Course existingCourse = courseRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Course", "id", id));
        
        Course oldValue = new Course();

        existingCourse.setCourseName(course.getCourseName());
        existingCourse.setCourseCode(course.getCourseCode());
        existingCourse.setCredits(course.getCredits());
        existingCourse.setInstructor(course.getInstructor());
        
        return courseRepository.save(existingCourse);
    }

    @Transactional
    public void deleteCourse(Integer id) {
        if (!courseRepository.existsById(id)) {
            throw new ResourceNotFoundException("Course", "id", id);
        }
        courseRepository.deleteById(id);
    }
} 