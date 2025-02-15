package com.bogdan.projectdb.repository;

import com.bogdan.projectdb.model.Instructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InstructorRepository extends JpaRepository<Instructor, Integer> {
    boolean existsByEmail(String email);
} 