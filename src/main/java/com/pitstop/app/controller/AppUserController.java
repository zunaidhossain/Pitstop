package com.pitstop.app.controller;

import com.pitstop.app.dto.AddressRequest;
import com.pitstop.app.model.Address;
import com.pitstop.app.service.impl.AppUserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class AppUserController {

    private final AppUserServiceImpl appUserService;

    /*
    Create Secured endpoints / API for the below functionality:
    1. (PUT) Can add address (Add in the userAddress List)
    2. (PUT) Can add balance to wallet (Skip for now as payment will be involved)
    3. (GET) Fetch bookingHistory - Create relevant DTO to return
    4. (PUT) Update email
    5. (PUT) Update password
    6. (DELETE) Delete account
     */

    @PutMapping("/add-address")
    public ResponseEntity<String> addAddress(@RequestBody AddressRequest address){
        return new ResponseEntity<>(appUserService.addAddress(address),HttpStatus.OK);
    }
    @PutMapping("/change-default-address")
    public ResponseEntity<String> changeDefaultAddress(@RequestBody AddressRequest addressRequest) {
        return new ResponseEntity<>(appUserService.changeDefaultAddress(addressRequest),HttpStatus.OK);
    }

}
