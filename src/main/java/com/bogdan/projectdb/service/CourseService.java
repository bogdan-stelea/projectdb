package com.bogdan.projectdb.service;

import com.bogdan.projectdb.model.Course;

import java.util.List;

public interface CourseService {
    Course getCourseById(Integer id);
    List<Course> getAllCourses();
    Course createCourse(Course course);
    Course updateCourse(Integer id, Course course);
    void deleteCourse(Integer id);
    List<Course> searchCourses(String query);
}