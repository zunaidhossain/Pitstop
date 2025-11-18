package com.pitstop.app.service;

import com.pitstop.app.dto.InitiatePaymentResponse;
import com.pitstop.app.dto.PaymentVerifyRequest;
import com.pitstop.app.model.Payment;

public interface PaymentService {
    InitiatePaymentResponse initiatePayment(String bookingId);
    void verifyAndUpdatePayment(PaymentVerifyRequest paymentVerifyRequest);
}
