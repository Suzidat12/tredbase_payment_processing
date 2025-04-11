package com.task.payment.service;

import com.task.payment.model.Parent;
import com.task.payment.model.Student;
import com.task.payment.repository.ParentRepository;
import com.task.payment.repository.StudentRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ParentStudentService {
    private final ParentRepository parentRepository;
    private final StudentRepository studentRepository;

    public ParentStudentService(ParentRepository parentRepository, StudentRepository studentRepository) {
        this.parentRepository = parentRepository;
        this.studentRepository = studentRepository;
    }

    @PostConstruct
    public void createDataForParentAndStudent() {
        // Create students
        Student student1 = new Student();
        student1.setBalance(0.0);

        Student student2 = new Student();
        student2.setBalance(0.0);

        Student student3 = new Student();
        student3.setBalance(0.0);

        studentRepository.save(student1);
        studentRepository.save(student2);
        studentRepository.save(student3);

// Create Parents
        Parent parentA = new Parent();
        parentA.setBalance(1000.0);

        Parent parentB = new Parent();
        parentB.setBalance(1000.0);

        parentRepository.save(parentA);
        parentRepository.save(parentB);

        // Associate Parents with Students (Shared and Unique)
        parentA.setStudents(List.of(student1, student2));
        parentB.setStudents(List.of(student1, student3));

        student1.setAssignedParents(List.of(parentA, parentB));
        student2.setAssignedParents(List.of(parentA));
        student3.setAssignedParents(List.of(parentB));

        parentRepository.save(parentA);
        parentRepository.save(parentB);

    }

}
