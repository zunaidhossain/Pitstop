package com.pitstop.app.dto;

import lombok.Data;

@Data
public class CreatePricingRuleRequest {
    private String vehicleType;
    private String workshopServiceType;
    private double amount;
    private double premiumAmount;
}
