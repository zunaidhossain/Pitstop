package com.pitstop.app.model;

import com.pitstop.app.constants.PaymentStatus;
import com.pitstop.app.constants.PaymentType;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Document("payments")
public class Payment {
    @Id
    private String id;
    private String bookingId;
    private String userId;
    private String workshopId;
    private Double amount;
    private String currency = "INR";
    private PaymentType paymentType;
    private PaymentStatus  paymentStatus = PaymentStatus.INITIATED;
    private boolean isVerified = false;

    private String gatewayOrderId;
    private String gatewayPaymentId;
    private String gatewaySignature;

    private Integer attemptNo = 1;
    private Instant statusUpdatedAt;
    @CreatedDate
    private Instant createdAt;
    @LastModifiedDate
    private Instant updatedAt;
}
