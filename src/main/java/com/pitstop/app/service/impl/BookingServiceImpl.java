package com.pitstop.app.service.impl;

import com.pitstop.app.constants.BookingStatus;
import com.pitstop.app.constants.WorkshopStatus;
import com.pitstop.app.model.AppUser;
import com.pitstop.app.model.Booking;
import com.pitstop.app.model.WorkshopUser;
import com.pitstop.app.repository.BookingRepository;
import com.pitstop.app.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final AppUserServiceImpl appUserService;
    private final WorkshopUserServiceImpl workshopUserService;

    @Override
    public Booking saveBookingDetails(Booking booking) {
        return bookingRepository.save(booking);
    }

    @Override
    public Booking getBookingById(String id) {
        return bookingRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("Booking not found with ID :"+id));
    }

    @Override
    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    public List<WorkshopUser> getAllOpenWorkshops() {
        List<WorkshopUser> allWorkShops = workshopUserService.getAllWorkshopUser();
        List<WorkshopUser> openWorkShops = new ArrayList<>();
        for(WorkshopUser workshopUser : allWorkShops) {
            if(workshopUser.getCurrentWorkshopStatus() == WorkshopStatus.OPEN) {
                openWorkShops.add(workshopUser);
            }
        }
        return openWorkShops;
    }

    public String requestBooking(String appUserId, String workShopUserId,
                               double amount, String vehicleDetails) {

        // 1. Change the logic here to track the current logged-in user without explicitly searching
        AppUser currentAppUser = appUserService.getAppUserById(appUserId);

        // 2. Throw exception if workshopUser doesn't exist and catch it in the controller layer
        // and return relevant response.
        WorkshopUser workshopUser = workshopUserService.getWorkshopUserById(workShopUserId);

        Booking booking = bookingRepository.save(new Booking(amount, vehicleDetails));

        currentAppUser.getBookingHistory().add(booking);
        workshopUser.getBookingHistory().add(booking);

        appUserService.saveAppUserDetails(currentAppUser);
        workshopUserService.saveWorkshopUserDetails(workshopUser);

        return booking.getId();
    }

    public Booking checkBookingStatus(String id) {
        return getBookingById(id);
    }

    public List<Booking> getStartedBookings(String workshopUserId) {
        WorkshopUser workshopUser = workshopUserService.getWorkshopUserById(workshopUserId);
        List<Booking> allBookings = workshopUser.getBookingHistory();
        List<Booking> startedBookings = new ArrayList<>();
        for(Booking booking : allBookings) {
            if(booking.getCurrentStatus() == BookingStatus.STARTED) {
                startedBookings.add(booking);
            }
        }
        return startedBookings;
    }

    public Booking acceptBooking(String bookingId) {
        Booking booking = getBookingById(bookingId);
        booking.setCurrentStatus(BookingStatus.BOOKED);
        return saveBookingDetails(booking);
    }
}
