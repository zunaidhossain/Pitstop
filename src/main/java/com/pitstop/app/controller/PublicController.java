package com.pitstop.app.controller;

import com.pitstop.app.model.AppUser;
import com.pitstop.app.model.WorkshopUser;
import com.pitstop.app.service.impl.AppUserServiceImpl;
import com.pitstop.app.service.impl.WorkshopUserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/home")
@RequiredArgsConstructor
public class PublicController {
    private final WorkshopUserServiceImpl workshopService;
    private final AppUserServiceImpl appUserService;

    @GetMapping("/heath-check")
    public String healthCheck() {
        return "OK";
    }

    @PostMapping("/registerWorkshop")
    public ResponseEntity<String> createNewWorkshopUser(@RequestBody WorkshopUser workshopUser) {
        workshopService.saveWorkshopUserDetails(workshopUser);
        return new ResponseEntity<>("New WorkshopUser account created successfully", HttpStatus.CREATED);
    }

    @PostMapping("/registerAppUser")
    public ResponseEntity<String> createNewAppUser(@RequestBody AppUser appUser) {
        appUserService.saveAppUserDetails(appUser);
        return new ResponseEntity<>("New AppUser account created successfully", HttpStatus.CREATED);
    }

    /*
    Create Login API for WorkshopUser & AppUser
     */
}
