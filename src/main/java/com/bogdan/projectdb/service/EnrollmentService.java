package com.bogdan.projectdb.service;

import com.bogdan.projectdb.dto.EnrollmentDto;
import com.bogdan.projectdb.model.Enrollment;
import java.util.List;
import java.util.Optional;
import com.bogdan.projectdb.repository.EnrollmentRepository;
import com.bogdan.projectdb.audit.AuditService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public interface EnrollmentService {
    Enrollment createEnrollment(Enrollment enrollment);
    Enrollment updateEnrollment(Integer id, Enrollment enrollment);
    void deleteEnrollment(Integer id);
    Enrollment findEnrollmentById(Integer id);
    List<Enrollment> getAllEnrollments();
    EnrollmentDto enrollStudentInCourse(Integer studentId, Integer courseId);
    List<EnrollmentDto> getStudentEnrollments(Integer studentId);
    List<EnrollmentDto> getCourseEnrollments(Integer courseId);
    EnrollmentDto updateGrade(Integer enrollmentId, Integer instructorId, Integer grade);
    void cancelEnrollment(Integer enrollmentId);
}