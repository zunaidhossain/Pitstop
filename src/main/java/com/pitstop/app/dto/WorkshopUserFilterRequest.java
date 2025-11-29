package com.pitstop.app.dto;

import lombok.Data;

@Data
public class WorkshopUserFilterRequest {
    private String vehicleType;
    private String serviceType;
    private double maxDistanceKm = 5.0;
}
