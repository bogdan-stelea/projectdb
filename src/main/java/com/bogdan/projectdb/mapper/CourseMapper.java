package com.bogdan.projectdb.mapper;

import com.bogdan.projectdb.dto.CourseDto;
import com.bogdan.projectdb.model.Course;
import com.bogdan.projectdb.model.Instructor;
import com.bogdan.projectdb.repository.InstructorRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CourseMapper {
    
    private final InstructorRepository instructorRepository;
    
    public CourseMapper(InstructorRepository instructorRepository) {
        this.instructorRepository = instructorRepository;
    }
    
    public CourseDto toDto(Course course) {
        if (course == null) {
            return null;
        }
        
        CourseDto dto = new CourseDto();
        dto.setCourseCode(course.getCourseCode());
        dto.setCourseName(course.getCourseName());
        dto.setCredits(course.getCredits());
        dto.setDescription(course.getDescription());
        
        if (course.getInstructor() != null) {
            dto.setInstructorId(course.getInstructor().getId());
        }
        
        return dto;
    }
    
    public Course toEntity(CourseDto dto) {
        if (dto == null) {
            return null;
        }
        
        Course course = new Course();
        course.setCourseCode(dto.getCourseCode());
        course.setCourseName(dto.getCourseName());
        course.setCredits(dto.getCredits());
        course.setDescription(dto.getDescription());
        
        if (dto.getInstructorId() != null) {
            Instructor instructor = instructorRepository.findById(dto.getInstructorId())
                .orElse(null);
            course.setInstructor(instructor);
        }
        
        return course;
    }
    
    public List<CourseDto> toDtoList(List<Course> courses) {
        if (courses == null) {
            return null;
        }
        return courses.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
}