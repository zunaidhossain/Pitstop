package com.pitstop.app.service.impl;

import com.pitstop.app.constants.BookingStatus;
import com.pitstop.app.constants.WorkshopStatus;
import com.pitstop.app.dto.BookingRequestOtp;
import com.pitstop.app.dto.BookingResponse;
import com.pitstop.app.dto.BookingStatusResponse;
import com.pitstop.app.model.Address;
import com.pitstop.app.model.AppUser;
import com.pitstop.app.model.WorkshopUser;
import com.pitstop.app.repository.AppUserRepository;
import com.pitstop.app.repository.BookingRepository;
import com.pitstop.app.repository.WorkshopUserRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest
public class BookingServiceImplTest {

    @Autowired
    private BookingServiceImpl bookingService;

    @Autowired
    private AppUserServiceImpl appUserService;

    @Autowired
    private WorkshopUserServiceImpl workshopUserService;

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private WorkshopUserRepository workshopUserRepository;

    @Autowired
    private BookingRepository bookingRepository;

    private AppUser appUser;
    private WorkshopUser workshopUser;
    private String bookingId;

    @BeforeAll
    public void setUpOnce() {

        // AppUser Set-Up
        appUser = new AppUser();
        appUser.setName("AppUser Test Sample Name");
        appUser.setUsername("xxxx_xxxx_app_user");
        appUser.setEmail("xxxx_xxxx_app_user@xyz.com");
        appUser.setPassword("123456789");
        appUserRepository.save(appUser);

        /*
        Add address part later here once address functionality has
        been implemented with latitude & longitude and replace below code.
        */
        {
            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(appUser.getUsername(), appUser.getPassword());
            SecurityContextHolder.getContext().setAuthentication(auth);
            appUserService.addAddress(new Address("Kolkata 71"));
            SecurityContextHolder.clearContext();
        }

        // WorkshopUser Set-Up
        workshopUser = new WorkshopUser();
        workshopUser.setName("WorkshopUser Test Sample Name");
        workshopUser.setUsername("xxxx_xxxx_workshop_user");
        workshopUser.setEmail("xxxx_xxxx_workshop_user@xyz.com");
        workshopUser.setPassword("123456789");
        workshopUserRepository.save(workshopUser);

        /*
        Add address part later here once address functionality has
        been implemented with latitude & longitude and replace below code.
        */
        {
            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(workshopUser.getUsername(), appUser.getPassword());
            SecurityContextHolder.getContext().setAuthentication(auth);
            workshopUserService.openWorkshop(workshopUser.getUsername());
            workshopUserService.addAddress(new Address("Kolkata 71"));
            SecurityContextHolder.clearContext();
        }

    }

    @Order(1)
    @Test
    @DisplayName("AppUser Requesting a booking")
    void requestBookingTest(){
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(appUser.getUsername(), appUser.getPassword());
        SecurityContextHolder.getContext().setAuthentication(auth);

        bookingId = bookingService.requestBooking(workshopUser.getId(), 1000, "4 wheeler");
        assertNotNull(bookingId); // Check booking_id is created

        // Check booking exists in the repo
        assertTrue(bookingRepository.existsById(bookingId));

        SecurityContextHolder.clearContext();
    }

    @Order(2)
    @Test
    @DisplayName("AppUser Checking the bookingStatus - STARTED")
    void toCheckStartedBookingStatus() {
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(appUser.getUsername(), appUser.getPassword());
        SecurityContextHolder.getContext().setAuthentication(auth);

        BookingResponse b = bookingService.checkBookingStatus(bookingId);
        assertEquals(BookingStatus.STARTED, b.getCurrentStatus());
        assertEquals(1000, b.getAmount());
        assertEquals("4 wheeler", b.getVehicleDetails());
        assertNotNull(b.getBookingStartedTime());

        SecurityContextHolder.clearContext();
    }

    @Order(3)
    @Test
    @DisplayName("WorkshopUser Checking the booking queue")
    void workShopCheckBookingsQueue() {
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(workshopUser.getUsername(), workshopUser.getPassword());
        SecurityContextHolder.getContext().setAuthentication(auth);

        List<BookingResponse> bookingResponseList = bookingService.getStartedBookings();
        assertEquals(1, bookingResponseList.size());
        assertEquals(bookingId, bookingResponseList.getFirst().getId());

        SecurityContextHolder.clearContext();
    }

    @Order(4)
    @Test
    @DisplayName("WorkshopUser Accept the Booking")
    void workShopAcceptBooking() {
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(workshopUser.getUsername(), workshopUser.getPassword());
        SecurityContextHolder.getContext().setAuthentication(auth);

        BookingResponse bookingResponse = bookingService.acceptBooking(bookingId);
        assertEquals(bookingId, bookingResponse.getId());
        assertEquals(BookingStatus.BOOKED, bookingResponse.getCurrentStatus());

        SecurityContextHolder.clearContext();
    }

    @Order(5)
    @Test
    @DisplayName("AppUser Checking the bookingStatus - BOOKED")
    void toCheckBookedBookingStatus() {
        defaultMethodToCheckBookingStatusForAppUser(BookingStatus.BOOKED);
    }

    @Order(6)
    @Test
    @DisplayName("AppUser Starts The Journey")
    void appUserStartJourney() {
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(appUser.getUsername(), appUser.getPassword());
        SecurityContextHolder.getContext().setAuthentication(auth);
        BookingResponse bookingResponse = bookingService.startJourney(bookingId);
        assertEquals(bookingId, bookingResponse.getId());
        assertEquals(BookingStatus.ON_THE_WAY, bookingResponse.getCurrentStatus());

        SecurityContextHolder.clearContext();
    }

    @Order(7)
    @Test
    @DisplayName("AppUser Checking the bookingStatus - ON_THE_WAY")
    void toCheckOnTheWayBookingStatus() {
        defaultMethodToCheckBookingStatusForAppUser(BookingStatus.ON_THE_WAY);
    }

    @Order(8)
    @Test
    @DisplayName("AppUser Requesting for OTP and WorkShopUser Updating Status to Waiting")
    void toStartWaiting() {
        BookingStatusResponse bookingStatusResponse = null;
        {
            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(appUser.getUsername(), appUser.getPassword());
            SecurityContextHolder.getContext().setAuthentication(auth);

            bookingStatusResponse = bookingService.generateBookingOtp(bookingId);
            SecurityContextHolder.clearContext();
        }
        {
            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(workshopUser.getUsername(), workshopUser.getPassword());
            SecurityContextHolder.getContext().setAuthentication(auth);

            bookingService.verifyOtpAndSetStatus(
                    new BookingRequestOtp(bookingId, bookingStatusResponse.getOtp()), BookingStatus.WAITING);

            SecurityContextHolder.clearContext();
        }
    }

    @Order(9)
    @Test
    @DisplayName("AppUser Checking the bookingStatus - WAITING")
    void toCheckWaitingBookingStatus() {
        defaultMethodToCheckBookingStatusForAppUser(BookingStatus.WAITING);
    }

    @Order(10)
    @Test
    @DisplayName("AppUser Requesting for OTP and WorkShopUser Updating Status to Repairing")
    void toStartRepairing() {
        BookingStatusResponse bookingStatusResponse = null;
        {
            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(appUser.getUsername(), appUser.getPassword());
            SecurityContextHolder.getContext().setAuthentication(auth);

            bookingStatusResponse = bookingService.generateBookingOtp(bookingId);

            SecurityContextHolder.clearContext();
        }
        {
            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(workshopUser.getUsername(), workshopUser.getPassword());
            SecurityContextHolder.getContext().setAuthentication(auth);

            bookingService.verifyOtpAndSetStatus(
                    new BookingRequestOtp(bookingId, bookingStatusResponse.getOtp()), BookingStatus.REPAIRING);

            SecurityContextHolder.clearContext();
        }
    }

    @Order(11)
    @Test
    @DisplayName("AppUser Checking the bookingStatus - REPAIRING")
    void toCheckRepairingBookingStatus() {
        defaultMethodToCheckBookingStatusForAppUser(BookingStatus.REPAIRING);
    }

    @Order(12)
    @Test
    @DisplayName("AppUser Requesting for OTP and WorkShopUser Updating Status to Completed")
    void toComplete() {
        BookingStatusResponse bookingStatusResponse = null;
        {
            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(appUser.getUsername(), appUser.getPassword());
            SecurityContextHolder.getContext().setAuthentication(auth);

            bookingStatusResponse = bookingService.generateBookingOtp(bookingId);

            SecurityContextHolder.clearContext();
        }
        {
            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(workshopUser.getUsername(), workshopUser.getPassword());
            SecurityContextHolder.getContext().setAuthentication(auth);

            bookingService.verifyOtpAndSetStatus(
                    new BookingRequestOtp(bookingId, bookingStatusResponse.getOtp()), BookingStatus.COMPLETED);

            SecurityContextHolder.clearContext();
        }
    }

    @Order(13)
    @Test
    @DisplayName("AppUser Checking the bookingStatus - COMPLETED")
    void toCheckCompletedBookingStatus() {
        defaultMethodToCheckBookingStatusForAppUser(BookingStatus.COMPLETED);
    }

    void defaultMethodToCheckBookingStatusForAppUser(BookingStatus bookingStatus) {
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(appUser.getUsername(), appUser.getPassword());
        SecurityContextHolder.getContext().setAuthentication(auth);

        BookingResponse b = bookingService.checkBookingStatus(bookingId);
        assertEquals(bookingStatus, b.getCurrentStatus());

        SecurityContextHolder.clearContext();
    }

    @AfterAll
    public void tearDownAll() {
        SecurityContextHolder.clearContext();
        workshopUserRepository.deleteById(workshopUser.getId());
        appUserRepository.deleteById(appUser.getId());
        bookingRepository.deleteById(bookingId);
    }
}
