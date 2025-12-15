package com.pitstop.app.dto;

import lombok.Data;

@Data
public class UpdatePricingRuleRequest {
    private double amount;
    private double premiumAmount;
}
