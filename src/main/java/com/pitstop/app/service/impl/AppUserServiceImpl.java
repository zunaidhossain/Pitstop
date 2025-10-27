package com.pitstop.app.service.impl;

import com.pitstop.app.dto.AppUserLoginRequest;
import com.pitstop.app.dto.AppUserLoginResponse;
import com.pitstop.app.exception.UserAlreadyExistException;
import com.pitstop.app.exception.ResourceNotFoundException;
import com.pitstop.app.model.Address;
import com.pitstop.app.model.AppUser;
import com.pitstop.app.repository.AppUserRepository;
import com.pitstop.app.service.AppUserService;
import com.pitstop.app.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AppUserServiceImpl implements AppUserService {
    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager manager;
    private final UserDetailsServiceImpl userDetailsService;
    private final JwtUtil jwtUtil;

    @Override
    public void saveAppUserDetails(AppUser appUser) {
        //register new AppUsers
        Optional<AppUser> existingUser = appUserRepository.findByUsernameOrEmail(appUser.getUsername(),appUser.getEmail());
        if(existingUser.isPresent()){
            throw new UserAlreadyExistException("AppUser already exists");
        }
        appUser.setPassword(passwordEncoder.encode(appUser.getPassword()));
        appUserRepository.save(appUser);
    }
    public void updateAppUserDetails(AppUser appUser){
        if(appUser.getId() != null && appUserRepository.existsById(appUser.getId())){
            appUserRepository.save(appUser);
        }
        else {
            appUser.setPassword(passwordEncoder.encode(appUser.getPassword()));
            appUserRepository.save(appUser);
        }
    }

    @Override
    public AppUser getAppUserById(String id) {
        return appUserRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("AppUser not found with ID :"+id));
    }

    @Override
    public AppUser getAppUserByUsername(String username) {
        return appUserRepository.findByUsername(username)
                .orElseThrow(()-> new RuntimeException("AppUser not found with username :"+username));
    }

    @Override
    public List<AppUser> getAllAppUser() {
        return new ArrayList<>(appUserRepository.findAll());
    }

    @Override
    @Transactional
    public String addAddress(Address address) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        AppUser appUser = appUserRepository.findByUsername(username)
                .orElseThrow(()-> new ResourceNotFoundException("User not found"));
            List<Address> addresses = appUser.getUserAddress();
            addresses.add(address);
            appUser.setUserAddress(addresses);
            appUser.setAccountLastModifiedDateTime(LocalDateTime.now());
        updateAppUserDetails(appUser);
        return "Address added successfully";
    }
    public ResponseEntity<?> loginAppUser(AppUserLoginRequest appUser){
        try {
            manager.authenticate(
                    new UsernamePasswordAuthenticationToken(appUser.getUsername(),appUser.getPassword())
            );
            UserDetails userDetails = userDetailsService.loadUserByUsername(appUser.getUsername());
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
