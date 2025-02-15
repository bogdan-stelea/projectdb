package com.bogdan.projectdb.repository;

import com.bogdan.projectdb.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface StudentRepository extends JpaRepository<Student, Integer> {
    
    List<Student> findByFirstNameContainingOrLastNameContaining(
        String firstName, String lastName);
    
    List<Student> findByEmailContaining(String email);
    
    List<Student> findByFirstNameContainingOrLastNameContainingAndEmailContaining(
        String firstName, String lastName, String email);
} 