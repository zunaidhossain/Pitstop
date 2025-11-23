package com.pitstop.app.service.impl;

import com.pitstop.app.config.RazorpaySignatureVerifier;
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
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import java.time.Instant;


import static org.junit.jupiter.api.Assertions.*;
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest
@DisplayName("End-to-End Payment Flow Tests (NO Razorpay)")
class PaymentServiceImplTest {
    @Autowired
    private WorkshopUserServiceImpl workshopUserService;
    @Autowired
    private PaymentServiceImpl paymentService;

    @Autowired
    private AppUserRepository appUserRepository;
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private WorkshopUserRepository workshopUserRepository;
    @Autowired
    private PaymentRepository paymentRepository;

    private AppUser appUser;
    private WorkshopUser workshopUser;

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
    @BeforeEach
    void cleanDB() {
        paymentRepository.deleteAll();
        bookingRepository.deleteAll();
    }
    @Test
    void testInitiatePayment_success() {
        // Create booking in allowed status
        Booking booking = new Booking();
        booking.setAmount(500);
        booking.setAppUserId("user1");
        booking.setWorkshopUserId("work1");
        booking.setCurrentStatus(BookingStatus.BOOKED);
        booking = bookingRepository.save(booking);

        // Execute
        InitiatePaymentResponse response = paymentService.initiatePayment(booking.getId());

        assertNotNull(response);
        assertNotNull(response.getPaymentId());
        assertEquals("INR", response.getCurrency());
        assertEquals(500 * 100, response.getAmount());

        // Verify DB changes
        Payment saved = paymentRepository.findTopByBookingIdOrderByCreatedAtDesc(booking.getId())
                .orElseThrow();

        assertEquals(booking.getId(), saved.getBookingId());
        assertEquals(PaymentStatus.ORDER_CREATED, saved.getPaymentStatus());
        assertEquals(booking.getId(), saved.getBookingId());
    }
    @TestConfiguration
    static class TestOverrides {
        @Bean
        public RazorpaySignatureVerifier razorpaySignatureVerifier() {
            return new RazorpaySignatureVerifier("dummy") {
                @Override
                public boolean isSignatureValid(String orderId, String paymentId, String signature) {
                    return "VALID".equalsIgnoreCase(signature);
                }
            };
        }
    }
    @Test
    void testInitiatePayment_invalidStatus() {
        Booking booking = new Booking();
        booking.setAmount(500);
        booking.setAppUserId("user1");
        booking.setWorkshopUserId("work1");
        booking.setCurrentStatus(BookingStatus.COMPLETED);
        booking = bookingRepository.save(booking);

        Booking finalBooking = booking;
        BusinessException ex = assertThrows(
                BusinessException.class,
                () -> paymentService.initiatePayment(finalBooking.getId())
        );

        assertEquals("Payment can only be initiated when booking is BOOKED or ON THE WAY.", ex.getMessage());
    }
    @Test
    void testVerifyPayment_invalidSignature() {
        Booking booking = new Booking();
        booking.setAmount(500);
        booking.setAppUserId("user1");
        booking.setWorkshopUserId("work1");
        booking.setCurrentStatus(BookingStatus.BOOKED);
        booking = bookingRepository.save(booking);

        Payment payment = new Payment();
        payment.setBookingId(booking.getId());
        payment.setGatewayOrderId("order_123");
        payment.setPaymentStatus(PaymentStatus.INITIATED);
        payment.setCreatedAt(Instant.now());
        paymentRepository.save(payment);

        PaymentVerifyRequest req = new PaymentVerifyRequest();
        req.setGatewayOrderId("order_123");
        req.setGatewayPaymentId("pay_123");
        req.setGatewaySignature("INVALID"); // forces failure

        BusinessException ex = assertThrows(
                BusinessException.class,
                () -> paymentService.verifyAndUpdatePayment(req)
        );

        assertEquals("Invalid Razorpay signature. Payment tampering detected.", ex.getMessage());
    }

}