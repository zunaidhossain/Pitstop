package com.pitstop.app.model;

import com.pitstop.app.constants.BookingStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Document(collection = "bookings")
@NoArgsConstructor
public class Booking {

    @Id
    private String id;
    private double amount;
    private String vehicleDetails;

    private String workShopName;
    private Address workShopAddress;
    private String otp;
    private LocalDateTime otpExpiry;

    private BookingStatus currentStatus = BookingStatus.STARTED;
    private LocalDateTime bookingStartedTime = LocalDateTime.now();
    private LocalDateTime bookingCompletedTime;

    private double ratingAppUserToWorkshop = 0.0;
    private double ratingWorkshopToAppUser = 0.0;

    //Lets you easily link Booking to an AppUser and WorkshopUser if you plan to display booking history for each.
//    @DBRef
//    private AppUser appUser;
//
//    @DBRef
//    private WorkshopUser workshopUser;

    private String appUserId;
    private String workshopUserId;

    List<BookingStatusWithTimeStamp> bookingStatusHistory;
    Boolean appUserEligibleForRefund = null;

    public Booking(double amount, String vehicleDetails, String appUserId) {
        this.amount = amount;
        this.vehicleDetails = vehicleDetails;
        this.appUserId = appUserId;
        this.bookingStatusHistory = new ArrayList<>();
        bookingStatusHistory.add(new BookingStatusWithTimeStamp(currentStatus, LocalDateTime.now()));
    }
}
