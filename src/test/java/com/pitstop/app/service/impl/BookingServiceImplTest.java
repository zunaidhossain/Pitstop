package com.pitstop.app.service.impl;

import com.pitstop.app.constants.BookingStatus;
import com.pitstop.app.constants.WorkshopStatus;
import com.pitstop.app.dto.BookingResponse;
import com.pitstop.app.model.Address;
import com.pitstop.app.model.AppUser;
import com.pitstop.app.model.Booking;
import com.pitstop.app.model.WorkshopUser;
import com.pitstop.app.repository.AppUserRepository;
import com.pitstop.app.repository.BookingRepository;
import com.pitstop.app.repository.WorkshopUserRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest
public class BookingServiceImplTest {

    @Autowired
    private BookingServiceImpl bookingService;

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
        /*
        Add address part later here once address
        functionality has been implemented with latitude & longitude
        */
        appUser.getUserAddress().add(new Address("Kolkata 71"));
        appUserRepository.save(appUser);

        // WorkshopUser Set-Up
        workshopUser = new WorkshopUser();
        workshopUser.setName("WorkshopUser Test Sample Name");
        workshopUser.setUsername("xxxx_xxxx_workshop_user");
        workshopUser.setEmail("xxxx_xxxx_workshop_user@xyz.com");
        workshopUser.setPassword("123456789");
        /*
        Add address part later here once address
        functionality has been implemented with latitude & longitude
        */
        workshopUser.setWorkshopAddress(new Address("Kolkata 71"));
        workshopUser.setCurrentWorkshopStatus(WorkshopStatus.OPEN);
        workshopUserRepository.save(workshopUser);
    }

    @Order(1)
    @Test
    @DisplayName("Checking booking creation")
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
    @DisplayName("Checking the bookingStatus")
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

    @AfterAll
    public void tearDownAll() {
        SecurityContextHolder.clearContext();
        workshopUserRepository.deleteById(workshopUser.getId());
        appUserRepository.deleteById(appUser.getId());
        bookingRepository.deleteById(bookingId);
    }
}
