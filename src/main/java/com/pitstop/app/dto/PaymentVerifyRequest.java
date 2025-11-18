package com.pitstop.app.dto;

import lombok.Data;

@Data
public class PaymentVerifyRequest {
    private String gatewayOrderId;
    private String gatewayPaymentId;
    private String gatewaySignature;
}
