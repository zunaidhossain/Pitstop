package com.pitstop.app.dto;

import com.pitstop.app.constants.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BookingStatusResponse {
    private String id;
    private BookingStatus currentStatus;
    private String otp;
}
