package com.ecom.controller;

import com.ecom.service.PaymentService; // Interface import hoga yahan
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/payment")
@CrossOrigin(origins = "*")
public class PaymentController {

    
    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/create")
    public ResponseEntity<?> initPayment(@RequestBody Map<String, Object> request) {
        try {
            double amount = Double.parseDouble(request.get("amount").toString());
            String razorpayOrderId = paymentService.createTransaction(amount);
            
            return ResponseEntity.ok(Map.of("orderId", razorpayOrderId, "amount", amount));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Razorpay Integration Error: " + e.getMessage());
        }
    }
}