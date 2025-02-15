package com.bogdan.projectdb.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseDto {

    @NotBlank(message = "Course code is required")
    private String courseCode;

    @NotBlank(message = "Course name is required")
    private String courseName;

    @NotNull(message = "Credits are required")
    private Integer credits;

    @NotBlank(message = "Description is required")
    private String description;

    @NotNull(message = "Instructor ID is required")
    private Integer instructorId;
} 