package com.bogdan.projectdb.service;

import com.bogdan.projectdb.model.Student;
import java.util.List;

public interface StudentService {
    Student createStudent(Student student);
    Student findStudentById(Integer id);
    List<Student> getAllStudents();
    Student updateStudent(Integer id, Student student);
    void deleteStudent(Integer id);
    List<Student> searchStudents(String query);
}
