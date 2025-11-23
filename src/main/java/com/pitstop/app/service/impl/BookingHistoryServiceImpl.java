package com.pitstop.app.service.impl;

import com.pitstop.app.dto.AppUserBookingHistoryResponse;
import com.pitstop.app.dto.VehicleDetailsResponse;
import com.pitstop.app.dto.WorkShopUserBookingHistoryResponse;
import com.pitstop.app.model.AppUser;
import com.pitstop.app.model.Booking;
import com.pitstop.app.model.WorkshopUser;
import com.pitstop.app.repository.BookingRepository;
import com.pitstop.app.service.BookingHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingHistoryServiceImpl implements BookingHistoryService {

    private final BookingRepository bookingRepository;
    private final AppUserServiceImpl appUserService;
    private final WorkshopUserServiceImpl workshopUserService;

    @Override
    public List<AppUserBookingHistoryResponse> getBookingHistoryForAppUser() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        AppUser appUser = appUserService.getAppUserByUsername(username);
        String appUserId = appUser.getId();

        // Fetch bookings sorted by start time (descending)
        List<Booking> bookings = bookingRepository.findByAppUserIdOrderByBookingStartedTimeDesc(appUserId);

        // Map to DTO
        return bookings.stream()
                .map(this::mapToResponseAppUser)
                .collect(Collectors.toList());
    }

    @Override
    public List<WorkShopUserBookingHistoryResponse> getBookingHistoryForWorkShopUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        WorkshopUser workShopUser = workshopUserService.getWorkshopUserByUsername(username);
        String workshopUserId = workShopUser.getId();

        // Fetch bookings sorted by start time (descending)
        List<Booking> bookings = bookingRepository.findByWorkshopUserIdOrderByBookingStartedTimeDesc(workshopUserId);

        // Map to DTO
        return bookings.stream()
                .map(this::mapToResponseWorkShopUser)
                .collect(Collectors.toList());
    }

    @Override
    public AppUserBookingHistoryResponse mapToResponseAppUser(Booking b) {
        return AppUserBookingHistoryResponse.builder()
                .bookingId(b.getId())
                .currentStatus(b.getCurrentStatus())
                .workShopId(b.getWorkshopUserId())
                .workShopName(b.getWorkShopName())
                .workshopAddress(b.getWorkShopAddress())
                .amount(b.getAmount())
                .vehicleDetails(new VehicleDetailsResponse(b.getVehicle().getId(), b.getVehicle().getVehicleType(),
                        b.getVehicle().getBrand(), b.getVehicle().getModel(), b.getVehicle().getEngineCapacity()))
                .time(b.getBookingStartedTime())  // ← always original booking start time
                .build();
    }

    @Override
    public WorkShopUserBookingHistoryResponse mapToResponseWorkShopUser(Booking b) {
        return WorkShopUserBookingHistoryResponse.builder()
                .bookingId(b.getId())
                .currentStatus(b.getCurrentStatus())
                .appUserId(b.getAppUserId())
                .amount(b.getAmount())
                .vehicleDetails(new VehicleDetailsResponse(b.getVehicle().getId(), b.getVehicle().getVehicleType(),
                        b.getVehicle().getBrand(), b.getVehicle().getModel(), b.getVehicle().getEngineCapacity()))
                .time(b.getBookingStartedTime())  // ← always original booking start time
                .build();
    }
}
