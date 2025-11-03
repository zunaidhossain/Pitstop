package com.pitstop.app.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AddressResponse {
    private double latitude;
    private double longitude;
    private String formattedAddress;
}
