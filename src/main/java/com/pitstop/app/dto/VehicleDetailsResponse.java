package com.pitstop.app.dto;

import com.pitstop.app.constants.VehicleType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class VehicleDetailsResponse {
    private String id;
    private VehicleType vehicleType;
    private String brand;
    private String model;
    private int engineCapacity;
}
