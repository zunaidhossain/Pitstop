package com.pitstop.app.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GetPriceResponse {
    private double baseAmount;
    private double premiumAmount;
    private double finalAmount;
    private boolean premiumApplied;
    private String message;
}
