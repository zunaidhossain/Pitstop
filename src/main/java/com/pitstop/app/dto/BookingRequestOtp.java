package com.pitstop.app.dto;

import com.pitstop.app.constants.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BookingRequestOtp {
    private String id;
    private String otp;
}
