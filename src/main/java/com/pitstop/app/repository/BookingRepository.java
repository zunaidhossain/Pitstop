package com.pitstop.app.repository;

import com.pitstop.app.model.Booking;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface BookingRepository extends MongoRepository<Booking, String> {
    List<Booking> findByAppUserIdOrderByBookingStartedTimeDesc(String appUserId);
    List<Booking> findByWorkshopUserIdOrderByBookingStartedTimeDesc(String workshopUserId);
}
