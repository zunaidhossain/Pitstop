package com.pitstop.app.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WorkshopUserRegisterResponse {
    private String id;
    private String name;
    private String username;
    private String email;
    private String message;
}
