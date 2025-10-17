package com.pitstop.app.service;

import com.pitstop.app.model.Address;
import com.pitstop.app.model.AppUser;
import java.util.List;

public interface AppUserService {
    void saveAppUserDetails(AppUser appUser);
    AppUser getAppUserById(String id);
    List<AppUser> getAllAppUser();

    String addAddress(Address address);
}
