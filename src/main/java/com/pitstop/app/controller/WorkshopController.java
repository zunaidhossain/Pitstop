package com.pitstop.app.controller;

import com.pitstop.app.constants.WorkshopStatus;
import com.pitstop.app.dto.*;
import com.pitstop.app.model.Address;
import com.pitstop.app.model.WorkshopUser;
import com.pitstop.app.service.impl.BookingHistoryServiceImpl;
import com.pitstop.app.service.impl.WorkshopUserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/workshops")
@RequiredArgsConstructor
public class WorkshopController {

    private final WorkshopUserServiceImpl workshopService;
    private final BookingHistoryServiceImpl bookingHistoryService;

    // Role should be NORMAL_WORKSHOP_USER
    // Remove {workshopUserId} part from path variable after auth is implemented
    // Directly pull workshopUser details from Request Object
    @PostMapping("/setWorkshopStatus")
    public ResponseEntity<WorkshopStatusResponse> openWorkshop() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        WorkshopStatusResponse response = workshopService.openWorkshop(username);
        return ResponseEntity.ok(response);
    }

     /*
    Create Secured endpoints / API for the below functionality:
    1. (PUT) Can update address
    2. (GET) Fetch bookingHistory - Create relevant DTO to return
    3. (PUT) Update email
    4. (PUT) Update password
    5. (DELETE) Delete account
     */
     @PutMapping("/update-address")
     public ResponseEntity<String> addAddress(@RequestBody AddressRequest address){
         return new ResponseEntity<>(workshopService.addAddress(address),HttpStatus.OK);
     }

    @GetMapping("/getBookingHistory")
    public ResponseEntity<?> getBookingHistoryForWorkShopUser() {
        return new ResponseEntity<>(bookingHistoryService.getBookingHistoryForWorkShopUser(), HttpStatus.OK);
     @GetMapping("/me")
     public ResponseEntity<WorkshopUserResponse> getCurrentWorkshopUser(){
         return new ResponseEntity<>(workshopService.getWorkshopUserDetails(),HttpStatus.OK);
     }
     @PutMapping("/me/update")
    public ResponseEntity<String> updateWorkshopUser(@RequestBody WorkshopUserRequest workshopUserRequest){
         return new ResponseEntity<>(workshopService.updateWorkshopUser(workshopUserRequest),HttpStatus.OK);
     }
    @PutMapping("/me/change-password")
    public ResponseEntity<?> changePassword(@RequestBody WorkshopUserRequest workshopUserRequest) {
        return new ResponseEntity<>(workshopService.changePassword(workshopUserRequest),HttpStatus.OK);
    }
    @DeleteMapping("/me/delete")
    public ResponseEntity<?> deleteAppUserDetails() {
        return new ResponseEntity<>(workshopService.deleteAppUser(),HttpStatus.OK);
    }
}
