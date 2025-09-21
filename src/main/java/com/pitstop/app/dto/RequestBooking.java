package com.pitstop.app.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class RequestBooking {
    private String appUserId;
    private String workShopUserId;
    private double amount;
    private String vehicleDetails;
}
