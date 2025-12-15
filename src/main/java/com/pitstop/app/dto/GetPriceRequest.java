package com.pitstop.app.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class GetPriceRequest {
    @NotNull
    private String vehicleType;
    @NotNull
    private String serviceType;
    private String workshopId; // optional
}
