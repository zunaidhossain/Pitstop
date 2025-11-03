package com.pitstop.app.dto;

import lombok.Data;

@Data
public class AddressRequest {
    private Double latitude;
    private Double longitude;
    private String formattedAddress;
}
