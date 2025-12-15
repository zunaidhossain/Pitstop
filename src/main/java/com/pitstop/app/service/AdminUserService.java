package com.pitstop.app.service;

import com.pitstop.app.dto.AdminUserRegisterRequest;
import com.pitstop.app.dto.AdminUserRegisterResponse;
import com.pitstop.app.dto.WorkshopUserResponse;
import com.pitstop.app.model.BaseUser;
import com.pitstop.app.model.Booking;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Optional;

public interface AdminUserService {
    AdminUserRegisterResponse createAdmin(AdminUserRegisterRequest request);
    String changeUserRole(String id,String role);
    List<Booking> getBookingHistoryAppUser(String appUserId);
    List<Booking> getBookingHistoryWorkShopUser(String workshopUserId);
    Booking getBookingDetailsById(String bookingId);
    WorkshopUserResponse setPremium(String workshopUserId);
}
