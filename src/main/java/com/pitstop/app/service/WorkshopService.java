package com.pitstop.app.service;

import com.pitstop.app.dto.AddressRequest;
import com.pitstop.app.dto.PersonalInfoResponse;
import com.pitstop.app.dto.WorkshopUserRequest;
import com.pitstop.app.dto.WorkshopUserResponse;
import com.pitstop.app.model.WorkshopUser;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface WorkshopService {
    void saveWorkshopUserDetails(WorkshopUser workshopUser);
    WorkshopUser getWorkshopUserById(String id);
    List<WorkshopUser> getAllWorkshopUser();

    String addAddress(AddressRequest address);

    WorkshopUserResponse getWorkshopUserDetails();

    String updateWorkshopUser(WorkshopUserRequest workshopUserRequest);

    ResponseEntity<?> changePassword(WorkshopUserRequest workshopUserRequest);

    ResponseEntity<?> deleteAppUser();

    PersonalInfoResponse getPersonalProfile();
}
