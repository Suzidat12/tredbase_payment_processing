package com.task.payment.controller;

import com.task.payment.dto.PaymentProcessingRequest;
import com.task.payment.model.Payment;
import com.task.payment.service.PaymentProcessingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/")
public class PaymentProcessingController {
    private final PaymentProcessingService paymentProcessingService;

    public PaymentProcessingController(PaymentProcessingService paymentProcessingService) {
        this.paymentProcessingService = paymentProcessingService;
    }

    @PostMapping("payments")
    public ResponseEntity<String> processPayment(@RequestBody PaymentProcessingRequest paymentRequest) {
        try {
            paymentProcessingService.processPayment(paymentRequest.getParentId(), paymentRequest.getStudentId(), paymentRequest.getPaymentAmount());
            return new ResponseEntity<>("Payment processed successfully", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error processing payment: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
