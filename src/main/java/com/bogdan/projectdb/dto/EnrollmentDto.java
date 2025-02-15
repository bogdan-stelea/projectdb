package com.bogdan.projectdb.dto;

import com.bogdan.projectdb.enums.EnrollmentStatus;
import com.bogdan.projectdb.model.Course;
import com.bogdan.projectdb.model.Student;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EnrollmentDto {

    @NotNull(message = "Student ID is required")
    private Integer studentId;
    
    @NotNull(message = "Course ID is required")
    private Integer courseId;
    
    @NotNull(message = "Enrollment date is required")
    private LocalDate enrollmentDate;
    
    private String enrollmentNumber;

    private String status;

    @Min(value = 0, message = "Grade cannot be less than 0")
    @Max(value = 100, message = "Grade cannot be more than 100")
    private Integer grade;
} 