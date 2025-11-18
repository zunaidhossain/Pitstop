package com.pitstop.app.repository;

import com.pitstop.app.model.Payment;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends MongoRepository<Payment,String> {

    //finds the single most recent payment for the passed bookingId
    Optional<Payment> findTopByBookingIdOrderByCreatedAtDesc(String bookingId);

    //finds the complete payment list for a specific bookingId sorted from new to old
    List<Payment> findAllByBookingIdOrderByCreatedAtDesc(String bookingId);

    Optional<Payment> findByGatewayOrderId(String gatewayOrderId);
}
