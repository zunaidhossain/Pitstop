package com.pitstop.app.service;

import com.pitstop.app.model.BaseUser;

import java.util.Optional;

public interface AdminUserService {
    String changeUserRole(String id,String role);
}
