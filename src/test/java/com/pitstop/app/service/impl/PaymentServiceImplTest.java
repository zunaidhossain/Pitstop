package com.pitstop.app.service.impl;

import com.pitstop.app.constants.BookingStatus;
import com.pitstop.app.constants.PaymentStatus;
import com.pitstop.app.dto.InitiatePaymentResponse;
import com.pitstop.app.dto.PaymentVerifyRequest;
import com.pitstop.app.exception.BusinessException;
import com.pitstop.app.model.*;
import com.pitstop.app.repository.AppUserRepository;
import com.pitstop.app.repository.BookingRepository;
import com.pitstop.app.repository.PaymentRepository;
import com.pitstop.app.repository.WorkshopUserRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest
@DisplayName("End-to-End Payment Flow Tests (NO Razorpay)")
class PaymentServiceImplTest {
    @Autowired
    private AppUserServiceImpl appUserService;
    @Autowired
    private WorkshopUserServiceImpl workshopUserService;
    @Autowired
    private BookingServiceImpl bookingService;
    @Autowired
    private PaymentServiceImpl paymentService;
    @Autowired
    private OTPService otpService;

    @Autowired
    private AppUserRepository appUserRepository;
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private WorkshopUserRepository workshopUserRepository;

    private AppUser appUser;
    private WorkshopUser workshopUser;
    private String bookingId;
    private String paymentId;

    @BeforeAll
    public void setUp() {
        appUserRepository.deleteByUsername("xxxx_xxxx_app_user");
        workshopUserRepository.deleteByUsername("xxxx_xxxx_workshop_user");
        appUser = new AppUser();
        appUser.setName("AppUser Test Sample Name");
        appUser.setUsername("xxxx_xxxx_app_user");
        appUser.setEmail("xxxx_xxxx_app_user@xyz.com");
        appUser.setPassword("123456789");
        appUserRepository.save(appUser);

        {
            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(appUser.getUsername(), appUser.getPassword());
            SecurityContextHolder.getContext().setAuthentication(auth);
            SecurityContextHolder.clearContext();
        }

        workshopUser = new WorkshopUser();
        workshopUser.setName("WorkshopUser Test Sample Name");
        workshopUser.setUsername("xxxx_xxxx_workshop_user");
        workshopUser.setEmail("xxxx_xxxx_workshop_user@xyz.com");
        workshopUser.setPassword("123456789");
        workshopUserRepository.save(workshopUser);

        {
            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(workshopUser.getUsername(), appUser.getPassword());
            SecurityContextHolder.getContext().setAuthentication(auth);
            workshopUserService.openWorkshop(workshopUser.getUsername());
            SecurityContextHolder.clearContext();
        }
    }

    @Order(1)
    @Test
    @DisplayName("Should not initiate payment with booking status other than BOOKED or ON_THE_WAY")
    void checkInitiatePaymentForBookingsOtherThanBookedOrOnTheWay() {
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(appUser.getUsername(), appUser.getPassword());
        SecurityContextHolder.getContext().setAuthentication(auth);

        Booking invalidBooking = new Booking();
        invalidBooking.setId("INVALID_STATUS_BOOKING");
        invalidBooking.setAmount(500.0);
        invalidBooking.setCurrentStatus(BookingStatus.REPAIRING);
        invalidBooking.setCurrentPaymentStatus(PaymentStatus.NOT_PAID);
        bookingRepository.save(invalidBooking);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> paymentService.initiatePayment("INVALID_STATUS_BOOKING"));

        assertTrue(ex.getMessage().toLowerCase().contains("payment"));
        assertTrue(ex.getMessage().toLowerCase().contains("cannot")
                || ex.getMessage().toLowerCase().contains("only"));

        // Ensure no payment was created
        assertTrue(
                paymentRepository.findAll()
                        .stream()
                        .noneMatch(p -> p.getBookingId().equals("INVALID_STATUS_BOOKING"))
        );
    }
    @Order(10)
    @Test
    @DisplayName("Should NOT initiate payment when booking is NOT BOOKED or ON_THE_WAY")
    void shouldNotInitiatePaymentForInvalidBookingStatus() {

        // Create all statuses that should fail
        BookingStatus[] invalidStatuses = {
                BookingStatus.WAITING,
                BookingStatus.REPAIRING,
                BookingStatus.COMPLETED,
        };

        for (BookingStatus status : invalidStatuses) {

            Booking booking = new Booking();
            booking.setId("TEST_INVALID_" + status.name());
            booking.setAmount(200);
            booking.setCurrentStatus(status);
            booking.setCurrentPaymentStatus(PaymentStatus.NOT_PAID);
            booking.setBookingStatusHistory(new java.util.ArrayList<>());

            bookingRepository.save(booking);

            RuntimeException ex = assertThrows(
                    RuntimeException.class,
                    () -> paymentService.initiatePayment(booking.getId()),
                    "Expected failure for status: " + status
            );

            assertTrue(
                    ex.getMessage().toLowerCase().contains("booked") ||
                            ex.getMessage().toLowerCase().contains("on_the_way") ||
                            ex.getMessage().toLowerCase().contains("payment"),
                    "Message should indicate allowed statuses"
            );
        }
    }
}