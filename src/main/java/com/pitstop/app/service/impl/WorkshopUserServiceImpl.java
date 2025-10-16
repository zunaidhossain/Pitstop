package com.pitstop.app.service.impl;

import com.pitstop.app.constants.WorkshopStatus;
import com.pitstop.app.dto.AppUserLoginResponse;
import com.pitstop.app.dto.WorkshopLoginRequest;
import com.pitstop.app.dto.WorkshopStatusResponse;
import com.pitstop.app.model.Address;
import com.pitstop.app.model.AppUser;
import com.pitstop.app.model.WorkshopUser;
import com.pitstop.app.repository.WorkshopUserRepository;
import com.pitstop.app.service.WorkshopService;
import com.pitstop.app.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WorkshopUserServiceImpl implements WorkshopService {
    private final WorkshopUserRepository workshopUserRepository;
    private static final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final AuthenticationManager manager;
    private final UserDetailsServiceImpl userDetailsService;
    private final JwtUtil jwtUtil;

    @Override
    public void saveWorkshopUserDetails(WorkshopUser workshopUser) {
        if(workshopUser.getId() != null && workshopUserRepository.existsById(workshopUser.getId())){
            workshopUserRepository.save(workshopUser);
        }
        else {
            workshopUser.setPassword(passwordEncoder.encode(workshopUser.getPassword()));
            workshopUserRepository.save(workshopUser);
        }
    }

    @Override
    public WorkshopUser getWorkshopUserById(String id) {
        return workshopUserRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("WorkshopUser not found with ID :"+id));
    }

    @Override
    public List<WorkshopUser> getAllWorkshopUser() {
        return workshopUserRepository.findAll();
    }

    @Override
    public String addAddress(Address address) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        WorkshopUser workshopUser = workshopUserRepository.findByUsername(username)
                .orElseThrow(()-> new RuntimeException("Workshop not found"));
        workshopUser.setWorkshopAddress(address);
        saveWorkshopUserDetails(workshopUser);
        return "Address added successfully";
    }

    public WorkshopStatusResponse openWorkshop(String username) {
        WorkshopUser workshopUser = workshopUserRepository.findByUsername(username)
                .orElseThrow(()-> new RuntimeException("Workshop not found with username : "+username));

        workshopUser.setCurrentWorkshopStatus(WorkshopStatus.OPEN);
        saveWorkshopUserDetails(workshopUser);

        return new WorkshopStatusResponse(workshopUser.getUsername(),workshopUser.getCurrentWorkshopStatus());
    }
    public ResponseEntity<?> loginWorkshopUser(WorkshopLoginRequest workshopUser){
        try {
            manager.authenticate(
                    new UsernamePasswordAuthenticationToken(workshopUser.getUsername(),workshopUser.getPassword())
            );
            UserDetails userDetails = userDetailsService.loadUserByUsername(workshopUser.getUsername());
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
