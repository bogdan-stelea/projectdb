package com.bogdan.projectdb.service.impl;

import com.bogdan.projectdb.model.Instructor;
import com.bogdan.projectdb.repository.InstructorRepository;
import com.bogdan.projectdb.service.InstructorService;
import com.bogdan.projectdb.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class InstructorServiceImpl implements InstructorService {
    private final InstructorRepository instructorRepository;

    public InstructorServiceImpl(InstructorRepository instructorRepository) {
        this.instructorRepository = instructorRepository;
    }

    public Instructor findInstructorById(Integer id) {
        return instructorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Instructor", "id", id));
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
        return instructorRepository.save(instructor);
    }

    @Transactional
    public Instructor updateInstructor(Integer id, Instructor instructor) {
        Instructor existingInstructor = instructorRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Instructor", "id", id));
        
        Instructor oldValue = new Instructor();
        
        existingInstructor.setFirstName(instructor.getFirstName());
        existingInstructor.setLastName(instructor.getLastName());
        existingInstructor.setEmail(instructor.getEmail());
        existingInstructor.setPhoneNumber(instructor.getPhoneNumber());
        existingInstructor.setDepartment(instructor.getDepartment());
        
        return instructorRepository.save(existingInstructor);
    }

    @Transactional
    public void deleteInstructor(Integer id) {
        if (!instructorRepository.existsById(id)) {
            throw new ResourceNotFoundException("Instructor", "id", id);
        }
        
        instructorRepository.deleteById(id);
    }
} 