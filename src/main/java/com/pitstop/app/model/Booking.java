package com.pitstop.app.model;

import com.pitstop.app.constants.BookingStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Getter
@Setter
@Document(collection = "bookings")
@NoArgsConstructor
public class Booking {

    @Id
    private String id;
    private double amount;
    private String vehicleDetails;

    private BookingStatus currentStatus = BookingStatus.STARTED;
    private LocalDateTime bookingStartedTime = LocalDateTime.now();
    private LocalDateTime bookingCompletedTime;

    private double ratingAppUserToWorkshop = 0.0;
    private double ratingWorkshopToAppUser = 0.0;

    //Lets you easily link Booking to an AppUser and WorkshopUser if you plan to display booking history for each.
    private String appUserId;
    private String workshopUserId;

    public Booking(double amount, String vehicleDetails) {
        this.amount = amount;
        this.vehicleDetails = vehicleDetails;
    }
}
