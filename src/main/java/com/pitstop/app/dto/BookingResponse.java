package com.pitstop.app.dto;

import com.pitstop.app.constants.BookingStatus;
import com.pitstop.app.model.Address;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class BookingResponse {
    private String id;
    private double amount;
    private String vehicleDetails;
    private BookingStatus currentStatus;
    private LocalDateTime bookingStartedTime;
    private LocalDateTime bookingCompletedTime;
    private String workShopId;
    private String workShopName;
    private Address workShopAddress;
}
