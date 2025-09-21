package com.pitstop.app.controller;

import com.pitstop.app.constants.BookingStatus;
import com.pitstop.app.dto.BookingId;
import com.pitstop.app.dto.RequestBooking;
import com.pitstop.app.model.Booking;
import com.pitstop.app.model.WorkshopUser;
import com.pitstop.app.service.impl.BookingServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.awt.print.Book;
import java.util.List;

@RestController
@RequestMapping("/api/booking")
@RequiredArgsConstructor
public class BookingController {

    private final BookingServiceImpl bookingService;

    // Role should be NORMAL_APP_USER
    @GetMapping("/openWorkshops")
    public ResponseEntity<List<WorkshopUser>> getAllOpenWorkshops() {
        return new ResponseEntity<>(bookingService.getAllOpenWorkshops(), HttpStatus.OK);
    }

    // Role should be NORMAL_APP_USER
    @PostMapping
    public ResponseEntity<String> requestBooking(@RequestBody RequestBooking requestBooking) {
        String bookingId = bookingService.requestBooking(requestBooking.getAppUserId(),
                requestBooking.getWorkShopUserId(), requestBooking.getAmount(),
                requestBooking.getVehicleDetails());

        return new ResponseEntity<>(bookingId, HttpStatus.OK);
    }

    // Role should be NORMAL_APP_USER
    @GetMapping("/{bookingId}")
    public ResponseEntity<Booking> checkBookingStatusForAppUser(@PathVariable String bookingId) {
        return new ResponseEntity<>(bookingService.checkBookingStatus(bookingId), HttpStatus.OK);
    }

    // Role should be NORMAL_WORKSHOP_USER
    // Remove {workshopUserId} part from path variable after auth is implemented
    // Directly pull workshopUser details from Request Object
    @GetMapping("/check/{workshopUserId}")
    public ResponseEntity<List<Booking>> checkForStartedBookingsForWorkshopUser(@PathVariable String workshopUserId) {
        return new ResponseEntity<>(bookingService.getStartedBookings(workshopUserId), HttpStatus.OK);
    }

    // Role should be NORMAL_WORKSHOP_USER
    @GetMapping("/acceptBooking/{bookingId}")
    public ResponseEntity<Booking> acceptBooking(@PathVariable String bookingId) {
        return new ResponseEntity<>(bookingService.acceptBooking(bookingId), HttpStatus.OK);
    }
}
