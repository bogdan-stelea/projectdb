package com.bogdan.projectdb.service.impl;

import com.bogdan.projectdb.model.Instructor;
import com.bogdan.projectdb.repository.InstructorRepository;
import com.bogdan.projectdb.audit.AuditService;
import com.bogdan.projectdb.service.InstructorService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class InstructorServiceImpl implements InstructorService {
    private final InstructorRepository instructorRepository;
    private final AuditService auditService;

    public InstructorServiceImpl(InstructorRepository instructorRepository, AuditService auditService) {
        this.instructorRepository = instructorRepository;
        this.auditService = auditService;
    }

    public Instructor findInstructorById(Integer id) {
        return instructorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Instructor not found"));
    }

    public List<Instructor> getAllInstructors() {
        return instructorRepository.findAll();
    }

    public List<Instructor> searchInstructors(String query) {
        return instructorRepository.findAll().stream()
                .filter(i -> query == null || (i.getFirstName() + " " + i.getLastName()).toLowerCase().contains(query.toLowerCase()) ||
                        (i.getDepartment().toLowerCase().contains(query.toLowerCase())))
                .toList();
    }

    @Transactional
    public Instructor createInstructor(Instructor instructor) {
        Instructor savedInstructor = instructorRepository.save(instructor);
        auditService.logActivity(
            "Instructor",
            savedInstructor.getId(),
            "CREATE",
            null,
            savedInstructor,
            "system"
        );
        return savedInstructor;
    }

    @Transactional
    public Instructor updateInstructor(Integer id, Instructor instructor) {
        Instructor existingInstructor = instructorRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Instructor not found"));
        
        Instructor oldValue = new Instructor();
        
        existingInstructor.setFirstName(instructor.getFirstName());
        existingInstructor.setLastName(instructor.getLastName());
        existingInstructor.setEmail(instructor.getEmail());
        existingInstructor.setPhoneNumber(instructor.getPhoneNumber());
        existingInstructor.setDepartment(instructor.getDepartment());
        
        Instructor updatedInstructor = instructorRepository.save(existingInstructor);
        
        auditService.logActivity(
            "Instructor",
            updatedInstructor.getId(),
            "UPDATE",
            oldValue,
            updatedInstructor,
            "system"
        );
        
        return updatedInstructor;
    }

    @Transactional
    public void deleteInstructor(Integer id) {
        Instructor instructor = instructorRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Instructor not found"));
            
        auditService.logActivity(
            "Instructor",
            id,
            "DELETE",
            instructor,
            null,
            "system"
        );
        
        instructorRepository.deleteById(id);
    }
} 