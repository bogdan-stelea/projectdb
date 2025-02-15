package com.bogdan.projectdb.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.bogdan.projectdb.encryption.StringEncryptionConverter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.bogdan.projectdb.security.DataMaskingContext;

import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "instructors")
public class Instructor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    @Column(nullable = false)
    private String department;

    @Column(name = "office_address")
    private String officeAddress;

    @OneToMany
    @JoinColumn(name = "instructor_id")
    private Set<Course> courses = new HashSet<>();

    @JsonProperty("email")
    public String getMaskedEmail() {
        return DataMaskingContext.getMaskingUtil().maskEmail(email);
    }

    @JsonProperty("phoneNumber")
    public String getMaskedPhoneNumber() {
        return DataMaskingContext.getMaskingUtil().maskPhoneNumber(phoneNumber);
    }

    @JsonProperty("officeAddress")
    public String getMaskedOfficeAddress() {
        return DataMaskingContext.getMaskingUtil().maskAddress(officeAddress);
    }

    @Override
    public String toString() {
        return "Instructor{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", department='" + department + '\'' +
                '}';
    }
} 