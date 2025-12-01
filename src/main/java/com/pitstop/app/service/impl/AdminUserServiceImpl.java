package com.pitstop.app.service.impl;

import com.pitstop.app.dto.AdminUserLoginRequest;
import com.pitstop.app.dto.AdminUserLoginResponse;
import com.pitstop.app.dto.AppUserLoginResponse;
import com.pitstop.app.exception.ResourceNotFoundException;
import com.pitstop.app.model.*;
import com.pitstop.app.repository.AdminUserRepository;
import com.pitstop.app.repository.AppUserRepository;
import com.pitstop.app.repository.BookingRepository;
import com.pitstop.app.repository.WorkshopUserRepository;
import com.pitstop.app.service.AdminUserService;
import com.pitstop.app.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminUserServiceImpl implements AdminUserService {
    private final AppUserRepository appUserRepository;
    private final WorkshopUserRepository workshopUserRepository;
    private final AdminUserRepository adminUserRepository;
    private static final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final AuthenticationManager manager;
    private final UserDetailsServiceImpl userDetailsService;
    private final JwtUtil jwtUtil;
    private final BookingServiceImpl bookingService;
    private final BookingRepository bookingRepository;

    @Transactional
    public String changeUserRole(String username, String newRole) {
        BaseUser user = appUserRepository.findByUsername(username)
                .map(u -> (BaseUser) u)
                .orElseGet(() -> workshopUserRepository.findByUsername(username)
                        .map(u -> (BaseUser) u)
                        .orElseThrow(() -> new ResourceNotFoundException("User not found")));

        return updateUserRole(user, newRole);
    }

    @Override
    public List<Booking> getBookingHistoryAppUser(String appUserId) {
        try {
            return bookingRepository.findByAppUserIdOrderByBookingStartedTimeDesc(appUserId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Booking> getBookingHistoryWorkShopUser(String workshopUserId) {
        try {
            return bookingRepository.findByWorkshopUserIdOrderByBookingStartedTimeDesc(workshopUserId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Booking getBookingDetailsById(String bookingId) {
        try {
            return bookingService.getBookingById(bookingId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String updateUserRole(BaseUser user, String newRole) {
        user.setRoles(List.of(newRole.toUpperCase()));

        if (user instanceof AppUser appUser) {
            appUserRepository.save(appUser);
        } else if (user instanceof WorkshopUser workshopUser) {
            workshopUserRepository.save(workshopUser);
        }

        return "Role updated for: " + user.getUsername();
    }
    public void createAdmin(AdminUser adminUser) {
        if(adminUser.getId() != null && adminUserRepository.existsById(adminUser.getId())){
            adminUserRepository.save(adminUser);
        }
        else {
            adminUser.setPassword(passwordEncoder.encode(adminUser.getPassword()));
            adminUserRepository.save(adminUser);
        }
    }
    public AdminUserLoginResponse loginAdminUser(AdminUserLoginRequest req){
        log.info("Login attempt for Admin: {}", req.getUsername());

        // Step 1: Authenticate username/password
        try {
            manager.authenticate(
                    new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword())
            );
        } catch (Exception ex) {
            log.warn("Invalid Admin credentials for {}", req.getUsername());
            throw new RuntimeException("Incorrect username or password");
        }

        // Step 2: Load full user details
        CustomUserDetails user =
                (CustomUserDetails) userDetailsService.loadUserByUsername(req.getUsername());

        // Step 3: Enforce ADMIN-only login rule
        if (user.getUserType() != UserType.ADMIN) {
            log.warn("User {} attempted Admin login but is {}",
                    req.getUsername(), user.getUserType());
            throw new RuntimeException("Only Admins can login here");
        }

        // Step 4: Generate full JWT
        String token = jwtUtil.generateToken(user);

        log.info("Admin {} logged in successfully", req.getUsername());

        return new AdminUserLoginResponse(
                user.getUsername(),
                token,
                "Login successful"
        );
    }
}
