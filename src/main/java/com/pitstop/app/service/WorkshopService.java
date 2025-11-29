package com.pitstop.app.service;

import com.pitstop.app.constants.WorkshopServiceType;
import com.pitstop.app.constants.WorkshopVehicleType;
import com.pitstop.app.dto.*;
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

    void addWorkshopServiceType(WorkshopServiceTypeRequest workshopServiceType);
    void addWorkshopVehicleType(WorkShopVehicleTypeRequest workshopVehicleType);
    void deleteWorkshopServiceType(WorkshopServiceTypeRequest workshopServiceTypeRequest);
    void deleteWorkshopVehicleType(WorkShopVehicleTypeRequest workshopVehicleTypeRequest);
    List<WorkshopServiceType> getAllWorkshopServiceType();
    WorkshopVehicleType getWorkshopSupportedVehicleType();
}
