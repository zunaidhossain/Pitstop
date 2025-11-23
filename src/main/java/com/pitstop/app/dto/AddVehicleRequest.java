package com.pitstop.app.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AddVehicleRequest {
    private String brand;       // flexible (we will normalize and validate)
    private String model;
    private int engineCapacity;

    // getters + setters
}