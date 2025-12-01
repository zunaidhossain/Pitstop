package com.pitstop.app.controller;

import com.pitstop.app.dto.*;
import com.pitstop.app.model.AdminUser;
import com.pitstop.app.model.AppUser;
import com.pitstop.app.model.WorkshopUser;
import com.pitstop.app.service.impl.AdminUserServiceImpl;
import com.pitstop.app.service.impl.AppUserServiceImpl;
import com.pitstop.app.service.impl.UserDetailsServiceImpl;
import com.pitstop.app.service.impl.WorkshopUserServiceImpl;
import com.pitstop.app.utils.JwtUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
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
    public ResponseEntity<String> createNewAppUser(@RequestBody AdminUser adminUser, @RequestParam String key) {
        if (!adminCreationKey.equals(key)){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid key. Access denied.");
        }
        adminUserService.createAdmin(adminUser);
        return new ResponseEntity<>("New Admin account created successfully", HttpStatus.CREATED);
    }
    @PostMapping("/login/admin-user")
    public ResponseEntity<?> loginAdminUser(@RequestBody AdminUserLoginRequest adminUserLoginRequest){
        return ResponseEntity.ok(adminUserService.loginAdminUser(adminUserLoginRequest));
    }
}
