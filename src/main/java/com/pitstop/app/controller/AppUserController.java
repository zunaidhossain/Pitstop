package com.pitstop.app.controller;

import com.pitstop.app.model.AppUser;
import com.pitstop.app.model.WorkshopUser;
import com.pitstop.app.service.impl.AppUserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class AppUserController {

    private final AppUserServiceImpl appUserService;

    // Only ADMIN_USER role user can access this endpoint
    @GetMapping
    public ResponseEntity<List<AppUser>> getAllAppUser() {
        List<AppUser> appUserList =  appUserService.getAllAppUser();
        return new ResponseEntity<>(appUserList, HttpStatus.OK);
    }

    /*
    Create Secured endpoints / API for the below functionality:
    1. (PUT) Can add address (Add in the userAddress List)
    2. (PUT) Can add balance to wallet (Skip for now as payment will be involved)
    3. (GET) Fetch bookingHistory - Create relevant DTO to return
    4. (PUT) Update email
    5. (PUT) Update password
    6. (DELETE) Delete account
     */

}
