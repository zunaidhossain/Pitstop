package com.pitstop.app.dto;

import com.pitstop.app.constants.BookingStatus;
import com.pitstop.app.model.Address;
import com.pitstop.app.model.Vehicle;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppUserBookingHistoryResponse {
    private String bookingId;
    private BookingStatus currentStatus;

    private String workShopId;
    private String workShopName;
    private Address workshopAddress;

    private double amount;
    private VehicleDetailsResponse vehicleDetails;

    private LocalDateTime time;
}
