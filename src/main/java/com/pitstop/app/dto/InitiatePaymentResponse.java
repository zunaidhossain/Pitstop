package com.pitstop.app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

//DTO returned to frontend
@Data
@AllArgsConstructor
public class InitiatePaymentResponse {
    private String paymentId;  // internal payment id
    private String razorpayOrderId;
    private double amount;   //in paise
    private String currency;
    private String key; //frontend public key
}
