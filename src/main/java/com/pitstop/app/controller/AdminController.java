package com.pitstop.app.controller;

import com.pitstop.app.dto.AdminUserLoginRequest;
import com.pitstop.app.model.AdminUser;
import com.pitstop.app.model.AppUser;
import com.pitstop.app.model.WorkshopUser;
import com.pitstop.app.service.impl.AdminUserServiceImpl;
import com.pitstop.app.service.impl.AppUserServiceImpl;
import com.pitstop.app.service.impl.WorkshopUserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {
    private final AppUserServiceImpl appUserService;
    private final WorkshopUserServiceImpl workshopService;
    private final AdminUserServiceImpl adminUserService;
    @Value("${ADMIN_CREATION_KEY}")
    private String adminCreationKey;

    @GetMapping("/appUsers")
    public ResponseEntity<List<AppUser>> getAllAppUser() {
        List<AppUser> appUserList =  appUserService.getAllAppUser();
        return new ResponseEntity<>(appUserList, HttpStatus.OK);
    }
    @GetMapping("/appUsers/{userId}")
    public ResponseEntity<?> getAppUserById(@PathVariable String userId){
        AppUser appUser = appUserService.getAppUserById(userId);
        if (appUser == null){
            return new ResponseEntity<>("AppUser not found",HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(appUser,HttpStatus.OK);
    }
    @GetMapping("/workshops")
    public ResponseEntity<List<WorkshopUser>> getAllWorkshopUser() {
        List<WorkshopUser> workshopUserList =  workshopService.getAllWorkshopUser();
        return new ResponseEntity<>(workshopUserList, HttpStatus.OK);
    }
    @GetMapping("/workshops/{workshopId}")
    public ResponseEntity<?> getWorkshopById(@PathVariable String workshopId){
        WorkshopUser workshopUser = workshopService.getWorkshopUserById(workshopId);
        if (workshopUser == null){
            return new ResponseEntity<>("Workshop with workshopId not found",HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(workshopUser,HttpStatus.OK);
    }
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/change-role/{username}")
    public ResponseEntity<String> changeUserRole(
            @PathVariable String username,
            @RequestParam String newRole) {
        String result = adminUserService.changeUserRole(username, newRole);
        return ResponseEntity.ok(result);
    }
    @PostMapping("/registerAdminUser")
    public ResponseEntity<String> createNewAdminUser(@RequestBody AdminUser adminUser,@RequestParam String key) {
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
    @GetMapping("/bookingHistory/AppUser/{appUserId}")
    public ResponseEntity<?> getBookingHistoryAppUser(@PathVariable String appUserId) {
        try {
            return new ResponseEntity<>(adminUserService.getBookingHistoryAppUser(appUserId), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Invalid AppUser Id, AppUser not found", HttpStatus.NOT_FOUND);
        }
    }
    @GetMapping("/bookingHistory/WorkshopUser/{workshopUserId}")
    public ResponseEntity<?> getBookingHistoryWorkShopUser(@PathVariable String workshopUserId) {
        try {
            return new ResponseEntity<>(adminUserService.getBookingHistoryWorkShopUser(workshopUserId), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Invalid WorkShopUser Id, WorkShopUser not found", HttpStatus.NOT_FOUND);
        }
    }
    @GetMapping("/booking/{bookingId}")
    public ResponseEntity<?> getBookingDetailsById(@PathVariable String bookingId) {
        try {
            return new ResponseEntity<>(adminUserService.getBookingDetailsById(bookingId), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Invalid Booking Id, Booking not found", HttpStatus.NOT_FOUND);
        }
    }
}

