package com.pitstop.app.service.impl;

import com.pitstop.app.constants.WorkshopStatus;
import com.pitstop.app.dto.*;
import com.pitstop.app.exception.UserAlreadyExistException;
import com.pitstop.app.exception.ResourceNotFoundException;
import com.pitstop.app.model.Address;
import com.pitstop.app.model.WorkshopUser;
import com.pitstop.app.repository.WorkshopUserRepository;
import com.pitstop.app.service.WorkshopService;
import com.pitstop.app.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class WorkshopUserServiceImpl implements WorkshopService {
    private final WorkshopUserRepository workshopUserRepository;
    private static final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final AuthenticationManager manager;
    private final UserDetailsServiceImpl userDetailsService;
    private final JwtUtil jwtUtil;

    @Value("${trueway.api.url}")
    private String truewayApiUrl;

    @Value("${trueway.api.key}")
    private String truewayApiKey;

    @Value("${trueway.api.host}")
    private String truewayApiHost;

    @Value("${nominatim.api.url}")
    private String nominatimApiUrl;

    @Value("${nominatim.user.agent}")
    private String nominatimUserAgent;

    @Override
    public void saveWorkshopUserDetails(WorkshopUser workshopUser) {
        boolean existsByUsername = workshopUserRepository.findByUsername(workshopUser.getUsername()).isPresent();
        boolean existsByEmail = workshopUserRepository.findByEmail(workshopUser.getEmail()).isPresent();

        if(existsByEmail || existsByUsername){
            throw new UserAlreadyExistException("WorkshopUser already exists");
        }
        workshopUser.setPassword(passwordEncoder.encode(workshopUser.getPassword()));
        workshopUserRepository.save(workshopUser);
    }
    public void updateWorkshopUserDetails(WorkshopUser workshopUser){
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
                .orElseThrow(()-> new ResourceNotFoundException("WorkshopUser not found with ID :"+id));
    }

    @Override
    public List<WorkshopUser> getAllWorkshopUser() {
        return workshopUserRepository.findAll();
    }

    @Override
    public String addAddress(AddressRequest address) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        WorkshopUser workshopUser = workshopUserRepository.findByUsername(username)
                .orElseThrow(()-> new ResourceNotFoundException("Workshop not found"));
        Address finalAddress = null;

        /* case 1 : if user provides coordinates (gps location)
        only case 1 implemented
        */

        if(address.getLatitude() != null && address.getLongitude() != null){
            AddressResponse addressResponse = findAddressFromCoordinates(address.getLatitude(),address.getLongitude());
            finalAddress = Address.builder()
                    .latitude(address.getLatitude())
                    .longitude(address.getLongitude())
                    .formattedAddress(addressResponse.getFormattedAddress())
                    .build();
        }
        workshopUser.setWorkshopAddress(finalAddress);
        workshopUser.setAccountLastModifiedDateTime(LocalDateTime.now());
        updateWorkshopUserDetails(workshopUser);
        return "Address added successfully";
    }

    public WorkshopUser getWorkshopUserByUsername(String username) {
        return workshopUserRepository.findByUsername(username)
                .orElseThrow(()-> new RuntimeException("WorkshopUser not found with username :"+username));
    }

    public WorkshopStatusResponse openWorkshop(String username) {
        WorkshopUser workshopUser = workshopUserRepository.findByUsername(username)
                .orElseThrow(()-> new ResourceNotFoundException("Workshop not found with username : "+username));

        workshopUser.setCurrentWorkshopStatus(WorkshopStatus.OPEN);
        updateWorkshopUserDetails(workshopUser);

        return new WorkshopStatusResponse(workshopUser.getId(), workshopUser.getName(),
                workshopUser.getUsername(),workshopUser.getCurrentWorkshopStatus(), workshopUser.getWorkshopAddress());
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
    private AddressResponse findAddressFromCoordinates(double latitude, double longitude) {
        try {
            String url = String.format(
                    "%s?lat=%f&lon=%f&format=json&addressdetails=1",
                    nominatimApiUrl, latitude, longitude
            );

            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.set("User-Agent", nominatimUserAgent);

            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

            JSONObject json = new JSONObject(response.getBody());
            JSONObject address = json.optJSONObject("address");
            String formattedAddress = json.optString("display_name", "Address not found");

            if (json.has("display_name") && address != null) {
                return AddressResponse.builder()
                        .formattedAddress(formattedAddress)
                        .build();
            } else {
                return AddressResponse.builder()
                        .formattedAddress("Address not found")
                        .build();
            }
        } catch (Exception e) {
            log.error("Invalid coordinates {}", e.getMessage());
            return AddressResponse.builder()
                    .formattedAddress("Error fetching address: " + e.getMessage())
                    .build();
        }
    }
    public AddressResponse findCoordinatesFromAddress(String addressPlainText) {
        try {
            String encodedAddress = URLEncoder.encode(addressPlainText, StandardCharsets.UTF_8);
            String url = truewayApiUrl + "?address=" + encodedAddress;

            java.net.http.HttpRequest request = java.net.http.HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("x-rapidapi-key", truewayApiKey)
                    .header("x-rapidapi-host", truewayApiHost)
                    .GET()
                    .build();

            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JSONObject json = new JSONObject(response.body());
                JSONArray results = json.optJSONArray("results");
                if (results == null || results.isEmpty()) {
                    throw new RuntimeException("No results found for: " + addressPlainText);
                }

                JSONObject first = results.getJSONObject(0);
                JSONObject location = first.getJSONObject("location");

                return AddressResponse.builder()
                        .latitude(location.getDouble("lat"))
                        .longitude(location.getDouble("lng"))
                        .formattedAddress(first.optString("address", addressPlainText))
                        .build();
            } else {
                throw new RuntimeException("API Error: " + response.statusCode() + " - " + response.body());
            }

        } catch (Exception e) {
            throw new RuntimeException("Error fetching coordinates for: " + addressPlainText + " | " + e.getMessage(), e);
        }
    }
    @Override
    public WorkshopUserResponse getWorkshopUserDetails() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        WorkshopUser currentWorkshopUser = workshopUserRepository.findByUsername(username)
                .orElseThrow(()-> new ResourceNotFoundException("Workshop not found"));

        WorkshopUserResponse workshopUserResponse = new WorkshopUserResponse();
        workshopUserResponse.setUsername(currentWorkshopUser.getUsername());
        workshopUserResponse.setEmail(currentWorkshopUser.getEmail());
        workshopUserResponse.setAddress(currentWorkshopUser.getWorkshopAddress());

        return workshopUserResponse;
    }

    @Override
    public String updateWorkshopUser(WorkshopUserRequest workshopUserRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        WorkshopUser currentWorkshopUser = workshopUserRepository.findByUsername(username)
                .orElseThrow(()-> new ResourceNotFoundException("Workshop not found"));

        if(workshopUserRequest.getName() != null) {
            currentWorkshopUser.setName(workshopUserRequest.getName());
        }
        if(workshopUserRequest.getEmail() != null) {
            currentWorkshopUser.setEmail(workshopUserRequest.getEmail());
        }
        if(workshopUserRequest.getUsername() != null) {
            currentWorkshopUser.setUsername(workshopUserRequest.getUsername());
        }

        updateWorkshopUserDetails(currentWorkshopUser);

        return "AppUser Details updated successfully";
    }

    @Override
    public ResponseEntity<?> changePassword(WorkshopUserRequest workshopUserRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        WorkshopUser currentWorkshopUser = workshopUserRepository.findByUsername(username)
                .orElseThrow(()-> new ResourceNotFoundException("Workshop not found"));

        String oldPassword = currentWorkshopUser.getPassword();
        String newPassword = workshopUserRequest.getPassword();
        if(newPassword.equals(oldPassword)) {
            return new ResponseEntity<>("Change Password cannot be same", HttpStatusCode.valueOf(500));
        }

        currentWorkshopUser.setPassword(newPassword);
        currentWorkshopUser.setAccountLastModifiedDateTime(LocalDateTime.now());
        updateWorkshopUserDetails(currentWorkshopUser);
        return new ResponseEntity<>("Password changed successfully", HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> deleteAppUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        WorkshopUser currentWorkshopUser = workshopUserRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        workshopUserRepository.delete(currentWorkshopUser);
        return new ResponseEntity<>("AppUser Deleted successfully", HttpStatus.OK);
    }

    @Override
    public PersonalInfoResponse getPersonalProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        WorkshopUser currentWorkShopUser = workshopUserRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return new PersonalInfoResponse(currentWorkShopUser.getName(),
                currentWorkShopUser.getUsername(), currentWorkShopUser.getEmail(),
                Math.round(currentWorkShopUser.getRating() * 10.0) / 10.0);
    }
}
