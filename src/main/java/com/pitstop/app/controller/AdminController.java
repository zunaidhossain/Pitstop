package com.pitstop.app.controller;

import com.pitstop.app.dto.AdminUserLoginRequest;
import com.pitstop.app.dto.AdminUserRegisterRequest;
import com.pitstop.app.dto.CreatePricingRuleRequest;
import com.pitstop.app.dto.UpdatePricingRuleRequest;
import com.pitstop.app.model.AdminUser;
import com.pitstop.app.model.AppUser;
import com.pitstop.app.model.WorkshopUser;
import com.pitstop.app.service.impl.AdminPricingServiceImpl;
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
    private final AdminPricingServiceImpl adminPricingService;

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
    @PostMapping("/addPricingRule")
    public ResponseEntity<?> addPricingRule(@RequestBody CreatePricingRuleRequest request) {
        try {
            return new ResponseEntity<>(adminPricingService.createPricingRule(request), HttpStatus.CREATED);
        }
        catch (Exception e) {
            return new ResponseEntity<>("Error creating pricing rule : "+ e.getMessage(),HttpStatus.BAD_REQUEST);
        }
    }
    @GetMapping("/getAllPricingRules")
    public ResponseEntity<?> getAllPricingRules() {
        try {
            return new ResponseEntity<>(adminPricingService.getAllPricingRules(),HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error getting all pricing rules", HttpStatus.BAD_REQUEST);
        }
    }
    @PutMapping("/updatePricingRule/{id}")
    public ResponseEntity<?> updatePricingRule(@RequestBody UpdatePricingRuleRequest request, @PathVariable String id) {
        try {
            return new ResponseEntity<>(adminPricingService.updatePricingRule(request,id), HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>("Error updating pricing rule",HttpStatus.BAD_REQUEST);
        }
    }
    @DeleteMapping("/deletePricingRule/{id}")
    public ResponseEntity<?> deletePricingRule(@PathVariable String id) {
        try{
            adminPricingService.deletePricingRule(id);
            return new ResponseEntity<>("Successfully deleted pricing rule",HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>("Error deleting pricing rule",HttpStatus.BAD_REQUEST);
        }
    }
    @PutMapping("/setPremium/{id}")
    public ResponseEntity<?> setPremium(@PathVariable String id) {
        try{
            return new ResponseEntity<>(adminUserService.setPremium(id), HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>("Error updating premium",HttpStatus.BAD_REQUEST);
        }
    }
}

