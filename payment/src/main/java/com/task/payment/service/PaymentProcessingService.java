package com.task.payment.service;

import com.task.payment.model.Payment;
import com.task.payment.model.Parent;
import com.task.payment.model.Student;
import com.task.payment.repository.ParentRepository;
import com.task.payment.repository.PaymentRepository;
import com.task.payment.repository.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;

@Service
public class PaymentProcessingService {
    private final ParentRepository parentRepository;
    private final StudentRepository studentRepository;
    private final PaymentRepository paymentRepository;

    public PaymentProcessingService(ParentRepository parentRepository, StudentRepository studentRepository, PaymentRepository paymentRepository) {
        this.parentRepository = parentRepository;
        this.studentRepository = studentRepository;
        this.paymentRepository = paymentRepository;
    }

    @Transactional // will help with the atomic req.
    public void processPayment(Long parentId, Long studentId, Double paymentAmount) throws Exception {
        Parent parent = parentRepository.findById(parentId)
                .orElseThrow(() -> new IllegalArgumentException("Parent not found"));
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student not found"));
        double dynamicRate = 0.05;
        Double adjustedAmount = paymentAmount * (1 + dynamicRate);
        double half = adjustedAmount / 2;
        if (student.getAssignedParents().size() == 2) {
            for (Parent p : student.getAssignedParents()) {
                if (p.getBalance() == null || p.getBalance() < half) {
                    throw new Exception("Insufficient funds for parent with ID: " + p.getParentId());
                }
            }
        } else {
            if (parent.getBalance() == null || parent.getBalance() < adjustedAmount) {
                throw new Exception("Insufficient funds for parent with ID: " + parentId);
            }
        }
        student.setBalance((student.getBalance() == null ? 0.0 : student.getBalance()) + adjustedAmount);
        studentRepository.save(student);

        if (student.getAssignedParents().contains(parent)) {
            parent.setBalance(parent.getBalance() - adjustedAmount);
            parentRepository.save(parent);
        }
        if (student.getAssignedParents().size() == 2) {
            for (Parent p : student.getAssignedParents()) {
                p.setBalance(p.getBalance() - half);
                parentRepository.save(p);
            }
        }

        Payment payment = new Payment();
        payment.setParentId(parentId);
        payment.setStudentId(studentId);
        payment.setPaymentAmount(paymentAmount);
        payment.setAdjustedAmount(adjustedAmount);
        payment.setDynamicRate(dynamicRate);
        payment.setPaymentDate(new Timestamp(System.currentTimeMillis()));
        paymentRepository.save(payment);
    }
}
