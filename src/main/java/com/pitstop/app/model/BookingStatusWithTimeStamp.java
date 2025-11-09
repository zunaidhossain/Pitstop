package com.pitstop.app.model;

import com.pitstop.app.constants.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
@Setter
public class BookingStatusWithTimeStamp {
    BookingStatus bookingStatus;
    LocalDateTime currentDateTime;
}
