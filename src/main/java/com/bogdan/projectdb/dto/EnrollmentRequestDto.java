package com.bogdan.projectdb.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class EnrollmentRequestDto {
    @NotNull
    private Integer studentId;
    
    @NotNull
    private Integer courseId;
    
    private String status = "ACTIVE";
}