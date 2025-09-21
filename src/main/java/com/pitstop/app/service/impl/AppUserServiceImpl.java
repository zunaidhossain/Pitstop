package com.pitstop.app.service.impl;

import com.pitstop.app.model.AppUser;
import com.pitstop.app.repository.AppUserRepository;
import com.pitstop.app.service.AppUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AppUserServiceImpl implements AppUserService {
    private final AppUserRepository appUserRepository;


    @Override
    public void saveAppUserDetails(AppUser appUser) {
        appUserRepository.save(appUser);
    }

    @Override
    public AppUser getAppUserById(String id) {
        return appUserRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("AppUser not found with ID :"+id));
    }

    @Override
    public List<AppUser> getAllAppUser() {
        return appUserRepository.findAll();
    }
}
