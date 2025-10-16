package com.pitstop.app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AppUserLoginResponse {
    private String username;
    private String token;
    private String message;
}
