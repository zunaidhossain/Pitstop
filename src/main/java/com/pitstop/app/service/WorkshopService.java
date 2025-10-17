package com.pitstop.app.service;

import com.pitstop.app.model.Address;
import com.pitstop.app.model.WorkshopUser;

import java.util.List;

public interface WorkshopService {
    void saveWorkshopUserDetails(WorkshopUser workshopUser);
    WorkshopUser getWorkshopUserById(String id);
    List<WorkshopUser> getAllWorkshopUser();

    String addAddress(Address address);
}
