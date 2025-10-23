package com.pitstop.app.service.impl;

import com.pitstop.app.dto.AdminUserLoginRequest;
import com.pitstop.app.dto.AppUserLoginResponse;
import com.pitstop.app.exception.ResourceNotFoundException;
import com.pitstop.app.model.AdminUser;
import com.pitstop.app.model.AppUser;
import com.pitstop.app.model.BaseUser;
import com.pitstop.app.model.WorkshopUser;
import com.pitstop.app.repository.AdminUserRepository;
import com.pitstop.app.repository.AppUserRepository;
import com.pitstop.app.repository.WorkshopUserRepository;
import com.pitstop.app.service.AdminUserService;
import com.pitstop.app.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
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
public class AdminUserServiceImpl implements AdminUserService {
    private final AppUserRepository appUserRepository;
    private final WorkshopUserRepository workshopUserRepository;
    private final AdminUserRepository adminUserRepository;
    private static final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final AuthenticationManager manager;
    private final UserDetailsServiceImpl userDetailsService;
    private final JwtUtil jwtUtil;

    @Transactional
    public String changeUserRole(String username, String newRole) {
        BaseUser user = appUserRepository.findByUsername(username)
                .map(u -> (BaseUser) u)
                .orElseGet(() -> workshopUserRepository.findByUsername(username)
                        .map(u -> (BaseUser) u)
                        .orElseThrow(() -> new ResourceNotFoundException("User not found")));

        return updateUserRole(user, newRole);
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
    public ResponseEntity<?> loginAdminUser(AdminUserLoginRequest adminUserLoginRequest){
        try {
            manager.authenticate(
                    new UsernamePasswordAuthenticationToken(adminUserLoginRequest.getUsername(),adminUserLoginRequest.getPassword())
            );
            UserDetails userDetails = userDetailsService.loadUserByUsername(adminUserLoginRequest.getUsername());
            String token = jwtUtil.generateToken(userDetails.getUsername());
            AppUserLoginResponse response = new AppUserLoginResponse(
                    userDetails.getUsername(),
                    token,
                    "Login successful"
            );
            return new ResponseEntity<>(response, HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>("Incorrect username or password",HttpStatus.BAD_REQUEST);
        }
    }
}
