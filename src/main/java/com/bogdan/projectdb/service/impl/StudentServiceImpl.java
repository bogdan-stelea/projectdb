package com.bogdan.projectdb.service.impl;

import com.bogdan.projectdb.model.Student;
import com.bogdan.projectdb.repository.StudentRepository;
import com.bogdan.projectdb.service.StudentService;
import com.bogdan.projectdb.exception.ResourceNotFoundException;
import com.bogdan.projectdb.security.OracleApplicationContextService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional
public class StudentServiceImpl implements StudentService {
    private final StudentRepository studentRepository;
    private final OracleApplicationContextService contextService;
    
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public StudentServiceImpl(StudentRepository studentRepository, OracleApplicationContextService contextService) {
        this.studentRepository = studentRepository;
        this.contextService = contextService;
    }

    public Student findStudentById(Integer id) {
        return studentRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Student", "id", id));
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
        setOracleContext();
        return studentRepository.save(student);
    }
    
    private void setOracleContext() {
        try {
            var authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof UserDetails userDetails) {
                String username = userDetails.getUsername();
                String role = userDetails.getAuthorities().stream()
                        .findFirst()
                        .map(authority -> authority.getAuthority().replace("ROLE_", ""))
                        .orElse("USER");
                
                jdbcTemplate.execute("BEGIN DBMS_SESSION.SET_IDENTIFIER('" + username + "'); END;");
                
            }
        } catch (Exception e) {
        }
    }

    @Transactional
    public Student updateStudent(Integer id, Student student) {
        setOracleContext();
        Student existingStudent = studentRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Student", "id", id));
        
        Student oldValue = new Student();
        
        existingStudent.setFirstName(student.getFirstName());
        existingStudent.setLastName(student.getLastName());
        existingStudent.setEmail(student.getEmail());
        existingStudent.setPhoneNumber(student.getPhoneNumber());
        existingStudent.setDateOfBirth(student.getDateOfBirth());
        existingStudent.setAddress(student.getAddress());
        
        return studentRepository.save(existingStudent);
    }

    @Transactional
    public void deleteStudent(Integer id) {
        setOracleContext();
        if (!studentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Student", "id", id);
        }
        studentRepository.deleteById(id);
    }

}
