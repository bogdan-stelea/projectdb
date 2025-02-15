package com.bogdan.projectdb.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.bogdan.projectdb.encryption.StringEncryptionConverter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.bogdan.projectdb.security.DataMaskingContext;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "students")
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dateOfBirth;

    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    @Column(nullable = false)
    private String address;

    @JoinColumn(name = "student_id")
    @OneToMany
    private List<Enrollment> enrollmentIds = new LinkedList<>();

    @ManyToMany
    private List<Course> courses = new LinkedList<>();

    @JsonProperty("email")
    public String getMaskedEmail() {
        return DataMaskingContext.getMaskingUtil().maskEmail(email);
    }

    @JsonProperty("phoneNumber")
    public String getMaskedPhoneNumber() {
        return DataMaskingContext.getMaskingUtil().maskPhoneNumber(phoneNumber);
    }

    @JsonProperty("address")
    public String getMaskedAddress() {
        return DataMaskingContext.getMaskingUtil().maskAddress(address);
    }

    @Override
    public String toString() {
        return "Student{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", dateOfBirth=" + dateOfBirth +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", address='" + address + '\'' +
                '}';
    }
}
