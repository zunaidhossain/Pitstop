package com.pitstop.app.service.impl;

import com.pitstop.app.constants.WorkshopStatus;
import com.pitstop.app.model.WorkshopUser;
import com.pitstop.app.repository.WorkshopUserRepository;
import com.pitstop.app.service.WorkshopService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WorkshopUserServiceImpl implements WorkshopService {
    private final WorkshopUserRepository workshopUserRepository;

    @Override
    public WorkshopUser saveWorkshopUserDetails(WorkshopUser workshopUser) {
        return workshopUserRepository.save(workshopUser);
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

    public WorkshopUser openWorkshop(String workshopUserId) {
        WorkshopUser workshopUser = getWorkshopUserById(workshopUserId);
        workshopUser.setCurrentWorkshopStatus(WorkshopStatus.OPEN);
        return saveWorkshopUserDetails(workshopUser);
    }
}
