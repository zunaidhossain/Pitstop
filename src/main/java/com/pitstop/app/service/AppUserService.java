package com.pitstop.app.service;

import com.pitstop.app.dto.AddressRequest;
import com.pitstop.app.dto.AppUserRequest;
import com.pitstop.app.dto.AppUserResponse;
import com.pitstop.app.dto.PersonalInfoResponse;
import com.pitstop.app.model.AppUser;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface AppUserService {
    void saveAppUserDetails(AppUser appUser);
    AppUser getAppUserById(String id);
    AppUser getAppUserByUsername(String username);
    List<AppUser> getAllAppUser();
    String addAddress(AddressRequest address);

    String changeDefaultAddress(AddressRequest addressRequest);

    String updateAppUser(AppUserRequest appUserRequest);

    AppUserResponse getAppUserDetails();

    ResponseEntity<?> changePassword(AppUserRequest appUserRequest);

    ResponseEntity<?>  deleteAppUser();

    PersonalInfoResponse getPersonalProfile();
}
