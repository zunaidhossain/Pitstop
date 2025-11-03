package com.pitstop.app.service;

import com.pitstop.app.dto.AddressRequest;
import com.pitstop.app.model.Address;
import com.pitstop.app.model.AppUser;
import java.util.List;

public interface AppUserService {
    void saveAppUserDetails(AppUser appUser);
    AppUser getAppUserById(String id);
    AppUser getAppUserByUsername(String username);
    List<AppUser> getAllAppUser();
    String addAddress(AddressRequest address);

    String changeDefaultAddress(AddressRequest addressRequest);
}
