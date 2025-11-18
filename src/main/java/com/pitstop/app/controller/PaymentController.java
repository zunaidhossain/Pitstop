package com.pitstop.app.controller;

import com.pitstop.app.dto.InitiatePaymentResponse;
import com.pitstop.app.dto.PaymentVerifyRequest;
import com.pitstop.app.model.Payment;
import com.pitstop.app.service.impl.PaymentServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentServiceImpl paymentService;

    @PostMapping("/initiate/{bookingId}")
    public ResponseEntity<InitiatePaymentResponse> initiatePayment(@PathVariable String bookingId) {
        InitiatePaymentResponse response = paymentService.initiatePayment(bookingId);
        return ResponseEntity.ok(response);
    }
    @PostMapping("/verify")
    public ResponseEntity<Map<String,Object>> verifyPaymentRequest(@RequestBody PaymentVerifyRequest paymentVerifyRequest) {
        paymentService.verifyAndUpdatePayment(paymentVerifyRequest);

        Map<String,Object> map = new HashMap<>();
        map.put("status","success");
        map.put("message", "Payment Verified");
        return ResponseEntity.ok(map);
    }
}
