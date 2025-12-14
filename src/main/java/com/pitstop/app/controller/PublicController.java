package com.pitstop.app.controller;

import com.pitstop.app.dto.*;
import com.pitstop.app.service.impl.AdminUserServiceImpl;
import com.pitstop.app.service.impl.AppUserServiceImpl;
import com.pitstop.app.service.impl.WorkshopUserServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/home")
@RequiredArgsConstructor
public class PublicController {
    private final WorkshopUserServiceImpl workshopService;
    private final AppUserServiceImpl appUserService;
    @Value("${ADMIN_CREATION_KEY}")
    private String adminCreationKey;
    private final AdminUserServiceImpl adminUserService;

    @GetMapping("/health-check")
    public String healthCheck() {
        return "OK";
    }

    @PostMapping("/registerWorkshop")
    public ResponseEntity<?> createNewWorkshopUser(@RequestBody @Valid WorkshopUserRegisterRequest workshopUser) {
        WorkshopUserRegisterResponse response = workshopService.saveWorkshopUserDetails(workshopUser);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/registerAppUser")
    public ResponseEntity<?> createNewAppUser(@RequestBody @Valid AppUserRegisterRequest appUser) {
        AppUserRegisterResponse response = appUserService.saveAppUserDetails(appUser);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /*
    Create Login API for WorkshopUser & AppUser
     */
    @PostMapping("/login/app-user")
    public ResponseEntity<?> loginAppUser(@RequestBody AppUserLoginRequest appUser){
        return ResponseEntity.ok(appUserService.loginAppUser(appUser));
    }
    @PostMapping("/login/workshop")
    public ResponseEntity<?> loginWorkshopUser(@RequestBody WorkshopLoginRequest workshopUser){
        return ResponseEntity.ok(workshopService.loginWorkshopUser(workshopUser));
    }
    @PostMapping("/registerAdminUser")
    public ResponseEntity<?> createNewAppUser(@RequestBody @Valid AdminUserRegisterRequest adminUser, @RequestParam String key) {
        try {
            if (!adminCreationKey.equals(key)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid key. Access denied.");
            }
            AdminUserRegisterResponse response = adminUserService.createAdmin(adminUser);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        }
        catch (Exception e){
            return new ResponseEntity<>("Error creating new admin user", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PostMapping("/login/admin-user")
    public ResponseEntity<?> loginAdminUser(@RequestBody AdminUserLoginRequest adminUserLoginRequest){
        return ResponseEntity.ok(adminUserService.loginAdminUser(adminUserLoginRequest));
    }
}
