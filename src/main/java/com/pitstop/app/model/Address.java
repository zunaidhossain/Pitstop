package com.pitstop.app.model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Address {
    private Double latitude;
    private Double longitude;
    private String formattedAddress;
    private boolean isDefault;
}
