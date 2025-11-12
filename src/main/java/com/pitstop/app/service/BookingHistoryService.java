package com.pitstop.app.service;

import com.pitstop.app.dto.AppUserBookingHistoryResponse;
import com.pitstop.app.dto.WorkShopUserBookingHistoryResponse;
import com.pitstop.app.model.Booking;

import java.util.List;

public interface BookingHistoryService {
    List<AppUserBookingHistoryResponse> getBookingHistoryForAppUser();
    List<WorkShopUserBookingHistoryResponse> getBookingHistoryForWorkShopUser();
    AppUserBookingHistoryResponse mapToResponseAppUser(Booking b);
    WorkShopUserBookingHistoryResponse mapToResponseWorkShopUser(Booking b);
}
