package com.pitstop.app.model;

import com.pitstop.app.constants.BookingStatus;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Getter
@Setter
@Document(collection = "bookings")
public class Booking {

    @Id
    private String id;

    private double amount;
    private String vehicleDetails;

    private BookingStatus currentStatus;
    private LocalDateTime bookingStartedTime;
    private LocalDateTime bookingCompletedTime;

    private double ratingAppUserToWorkshopUser;
    private double ratingWorkshopUserToAppUSer;


    public Booking(double amount, String vehicleDetails) {
        this.amount = amount;
        this.vehicleDetails = vehicleDetails;
        currentStatus = BookingStatus.STARTED;
        bookingStartedTime = LocalDateTime.now();
    }
}
