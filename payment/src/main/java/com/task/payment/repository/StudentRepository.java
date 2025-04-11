package com.task.payment.repository;

import com.task.payment.model.Parent;
import com.task.payment.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<Student,Long> {
}
