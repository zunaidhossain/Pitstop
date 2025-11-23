package com.pitstop.app.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class AddVehicleResponse {
    private String vehicleId;
    private String status;
    // constructors, getters
}