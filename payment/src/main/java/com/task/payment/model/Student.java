package com.task.payment.model;

import jakarta.persistence.*;

import java.util.List;
@Entity

public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long studentId;

    private Double balance;

    @ManyToMany(mappedBy = "students")
    private List<Parent> assignedParents;

    public Student(Long studentId, Double balance, List<Parent> assignedParents) {
        this.studentId = studentId;
        this.balance = balance;
        this.assignedParents = assignedParents;
    }

    public Student() {

    }

    public List<Parent> getAssignedParents() {
        return assignedParents;
    }

    public void setAssignedParents(List<Parent> assignedParents) {
        this.assignedParents = assignedParents;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }
}
