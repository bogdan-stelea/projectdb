package com.bogdan.projectdb.service.impl;

import com.bogdan.projectdb.model.Student;
import com.bogdan.projectdb.repository.StudentRepository;
import com.bogdan.projectdb.audit.AuditService;
import com.bogdan.projectdb.service.StudentService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional
public class StudentServiceImpl implements StudentService {
    private final StudentRepository studentRepository;
    private final AuditService auditService;

    public StudentServiceImpl(StudentRepository studentRepository, AuditService auditService) {
        this.studentRepository = studentRepository;
        this.auditService = auditService;
    }

    public Student findStudentById(Integer id) {
        return studentRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Student not found with id: " + id));
    }

    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    public List<Student> searchStudents(String query) {
        if (query == null || query.trim().isEmpty()) {
            return List.of();
        }
        
        String searchTerm = query.trim().toLowerCase();
        return studentRepository.findByFirstNameContainingOrLastNameContaining(
            searchTerm, searchTerm);
    }

    @Transactional
    public Student createStudent(Student student) {
        Student savedStudent = studentRepository.save(student);
        auditService.logActivity(
                "Student",
                savedStudent.getId(),
                "CREATE",
                null,
                savedStudent,
                "system"
        );
        return savedStudent;
    }

    @Transactional
    public Student updateStudent(Integer id, Student student) {
        Student existingStudent = studentRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Student not found"));
        
        Student oldValue = new Student();
        
        existingStudent.setFirstName(student.getFirstName());
        existingStudent.setLastName(student.getLastName());
        existingStudent.setEmail(student.getEmail());
        existingStudent.setPhoneNumber(student.getPhoneNumber());
        existingStudent.setDateOfBirth(student.getDateOfBirth());
        existingStudent.setAddress(student.getAddress());
        
        Student updatedStudent = studentRepository.save(existingStudent);
        
        auditService.logActivity(
            "Student",
            updatedStudent.getId(),
            "UPDATE",
            oldValue,
            updatedStudent,
            "system"
        );
        return updatedStudent;
    }

    @Transactional
    public void deleteStudent(Integer id) {
        Student student = studentRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Student not found"));
            
        auditService.logActivity(
            "Student",
            id,
            "DELETE",
            student,
            null,
            "system"
        );
        studentRepository.deleteById(id);
    }

}
