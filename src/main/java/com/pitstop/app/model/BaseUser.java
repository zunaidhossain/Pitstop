package com.pitstop.app.model;

import java.util.List;

public interface BaseUser {
    String getUsername();
    String getPassword();
    List<String> getRoles();
    void setRoles(List<String> roles);
}
