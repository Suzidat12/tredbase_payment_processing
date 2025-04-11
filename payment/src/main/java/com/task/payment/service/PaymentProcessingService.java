package com.task.payment.service;

import com.task.payment.model.Payment;
import com.task.payment.model.Parent;
import com.task.payment.model.Student;
import com.task.payment.repository.ParentRepository;
import com.task.payment.repository.PaymentRepository;
import com.task.payment.repository.StudentRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

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

    @Transactional // Ensures atomicity of the entire method execution
    public void processPayment(Long parentId, Long studentId, Double paymentAmount) throws Exception {
        // Retrieve the parent from the database by ID, throw an exception if not found
        Parent parent = parentRepository.findById(parentId)
                .orElseThrow(() -> new IllegalArgumentException("Parent not found"));
        // Retrieve the student from the database by ID, throw an exception if not found
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student not found"));
        // Define dynamic rate (e.g., processing fee rate of 5%)
        double dynamicRate = 0.05;
        // Calculate adjusted amount (payment + processing fee)
        Double adjustedAmount = paymentAmount * (1 + dynamicRate);
        // Calculate half of the adjusted amount to split the payment if two parents are assigned
        double half = adjustedAmount / 2;
        // Check if the student has two assigned parents, and verify if the parents have enough balance
        if (student.getAssignedParents().size() == 2) {
            for (Parent p : student.getAssignedParents()) {
                if (p.getBalance() == null || p.getBalance() < half) {
                    throw new Exception("Insufficient funds for parent with ID: " + p.getParentId());
                }
            }
        } else {
            // Check if the single parent has enough balance to cover the entire adjusted amount
            if (parent.getBalance() == null || parent.getBalance() < adjustedAmount) {
                throw new Exception("Insufficient funds for parent with ID: " + parentId);
            }
        }
        // Update the student's balance by adding the payment amount (without the processing fee)
        student.setBalance((student.getBalance() == null ? 0.0 : student.getBalance()) + paymentAmount);
        studentRepository.save(student);
        // If there are two parents for the student, split the adjusted amount between them
        if (student.getAssignedParents().size() == 2) {
            for (Parent p : student.getAssignedParents()) {
                p.setBalance(p.getBalance() - half);
                parentRepository.save(p); // Save updated parent balance
            }
        } else {
            // If only one parent, deduct the full adjusted amount from that parent's balance
            parent.setBalance(parent.getBalance() - adjustedAmount);
            parentRepository.save(parent); // Save updated parent balance
        }
        // Create a new Payment object to record the transaction details
        Payment payment = new Payment();
        payment.setParentId(parentId);
        payment.setStudentId(studentId);
        payment.setPaymentAmount(paymentAmount);
        payment.setAdjustedAmount(adjustedAmount);
        payment.setDynamicRate(dynamicRate);
        // Calculate and set the processing fee (difference between adjusted and base payment)
        payment.setProcessingFee(payment.getAdjustedAmount()-payment.getPaymentAmount());
        payment.setPaymentDate(new Timestamp(System.currentTimeMillis()));
        paymentRepository.save(payment);
    }

    // Retrieves the list of all parents from the database
    public ResponseEntity<List<Parent>> getParentList() {
        List<Parent> parents = parentRepository.findAll();
        return ResponseEntity.ok(parents);
    }
    // Retrieves the list of all payments from the database
    public ResponseEntity<List<Payment>> getPaymentList() {
        List<Payment> payments= paymentRepository.findAll();
        return ResponseEntity.ok(payments);
    }

    // Retrieves a specific parent by ID
    public ResponseEntity<Parent> getParentById(@PathVariable Long id) {
        Optional<Parent> parent = parentRepository.findById(id);
        return parent.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

}
