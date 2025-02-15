package com.bogdan.projectdb.service;

import java.util.List;
import com.bogdan.projectdb.model.Instructor;

public interface InstructorService {
    Instructor createInstructor(Instructor instructor);
    Instructor updateInstructor(Integer id, Instructor instructor);
    void deleteInstructor(Integer id);
    Instructor findInstructorById(Integer id);
    List<Instructor> getAllInstructors();
    List<Instructor> searchInstructors(String query);
}