package com.pitstop.app.controller;

import com.pitstop.app.constants.WorkshopStatus;
import com.pitstop.app.dto.AddressRequest;
import com.pitstop.app.dto.WorkshopStatusResponse;
import com.pitstop.app.model.Address;
import com.pitstop.app.model.WorkshopUser;
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
}
