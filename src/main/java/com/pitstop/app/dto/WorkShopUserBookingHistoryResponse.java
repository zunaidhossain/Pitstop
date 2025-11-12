package com.pitstop.app.dto;

import com.pitstop.app.constants.BookingStatus;
import com.pitstop.app.model.Address;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkShopUserBookingHistoryResponse {
    private String bookingId;
    private BookingStatus currentStatus;

    private String appUserId;

    private double amount;
    private String vehicleDetails;

    private LocalDateTime time;
}
