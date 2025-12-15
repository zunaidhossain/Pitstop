package com.pitstop.app.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AppUserRegisterResponse {
    private String id;
    private String name;
    private String username;
    private String email;
    private String message;
}
