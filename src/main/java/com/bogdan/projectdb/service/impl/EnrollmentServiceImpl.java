package com.bogdan.projectdb.service.impl;

import com.bogdan.projectdb.dto.EnrollmentDto;
import com.bogdan.projectdb.model.Course;
import com.bogdan.projectdb.model.Enrollment;
import com.bogdan.projectdb.enums.EnrollmentStatus;
import com.bogdan.projectdb.model.Student;
import com.bogdan.projectdb.repository.CourseRepository;
import com.bogdan.projectdb.repository.EnrollmentRepository;
import com.bogdan.projectdb.repository.StudentRepository;
import com.bogdan.projectdb.service.EnrollmentService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EnrollmentServiceImpl implements EnrollmentService {
    private final EnrollmentRepository enrollmentRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;


    public Enrollment findEnrollmentById(Integer id) {
        return enrollmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Enrollment not found"));
    }

    public List<EnrollmentDto> getStudentEnrollments(Integer studentId) {
        if (!studentRepository.existsById(studentId)) {
            throw new EntityNotFoundException("Student not found");
        }
        return enrollmentRepository.findByStudentId(studentId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<EnrollmentDto> getCourseEnrollments(Integer courseId) {
        if (!courseRepository.existsById(courseId)) {
            throw new EntityNotFoundException("Course not found");
        }
        return enrollmentRepository.findByCourseId(courseId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<Enrollment> getAllEnrollments() {
        return enrollmentRepository.findAll();
    }

    @Transactional
    public EnrollmentDto enrollStudentInCourse(Integer studentId, Integer courseId) {
        Student student = studentRepository.findById(studentId)
            .orElseThrow(() -> new EntityNotFoundException("Student not found"));
        Course course = courseRepository.findById(courseId)
            .orElseThrow(() -> new EntityNotFoundException("Course not found"));

        if (enrollmentRepository.existsByStudentIdAndCourseId(studentId, courseId)) {
            throw new IllegalStateException("Student is already enrolled in this course");
        }

        Enrollment enrollment = new Enrollment();
        enrollment.setStudent(student);
        enrollment.setCourse(course);
        enrollment.setEnrollmentDate(LocalDate.now());
        enrollment.setStatus(EnrollmentStatus.ACTIVE.toString());
        enrollment.setEnrollmentNumber(generateEnrollmentNumber(student, course));

        if (enrollmentRepository.existsByEnrollmentNumber(enrollment.getEnrollmentNumber())) {
            throw new IllegalStateException("Enrollment number already exists");
        }

        Enrollment savedEnrollment = enrollmentRepository.save(enrollment);
        return convertToDto(savedEnrollment);
    }

    @Transactional
    public EnrollmentDto updateGrade(Integer enrollmentId, Integer instructorId, Integer grade) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new EntityNotFoundException("Enrollment not found"));
        
        if (grade < 0 || grade > 100) {
            throw new IllegalArgumentException("Grade must be between 0 and 100");
        }
        
        enrollment.setGrade(grade);
        Enrollment updatedEnrollment = enrollmentRepository.save(enrollment);
        return convertToDto(updatedEnrollment);
    }

    @Transactional
    public Enrollment createEnrollment(Enrollment enrollment) {
        return enrollmentRepository.save(enrollment);
    }

    @Transactional
    public Enrollment updateEnrollment(Integer id, Enrollment enrollment) {
        Enrollment existingEnrollment = enrollmentRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Enrollment not found"));
        
        Enrollment oldValue = new Enrollment();
        
        existingEnrollment.setStudent(enrollment.getStudent());
        existingEnrollment.setCourse(enrollment.getCourse());
        existingEnrollment.setEnrollmentDate(enrollment.getEnrollmentDate());
        existingEnrollment.setGrade(enrollment.getGrade());
        existingEnrollment.setStatus(enrollment.getStatus());
        
        return enrollmentRepository.save(existingEnrollment);
    }

    @Transactional
    public void cancelEnrollment(Integer enrollmentId) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new EntityNotFoundException("Enrollment not found"));
        enrollment.setStatus(EnrollmentStatus.DROPPED.toString());
        enrollmentRepository.save(enrollment);
    }

    @Transactional
    public void deleteEnrollment(Integer id) {
        if (!enrollmentRepository.existsById(id)) {
            throw new EntityNotFoundException("Enrollment not found");
        }
        
        enrollmentRepository.deleteById(id);
    }

    private String generateEnrollmentNumber(Student student, Course course) {
        return String.format("ENR%d%d%d",
                student.getId(),
                course.getId(),
                System.currentTimeMillis() % 10000);
    }

    private EnrollmentDto convertToDto(Enrollment enrollment) {
        return new EnrollmentDto(
                enrollment.getStudent().getId(),
                enrollment.getCourse().getId(),
                enrollment.getEnrollmentDate(),
                enrollment.getEnrollmentNumber(),
                enrollment.getStatus(),
                enrollment.getGrade()
        );
    }
} 