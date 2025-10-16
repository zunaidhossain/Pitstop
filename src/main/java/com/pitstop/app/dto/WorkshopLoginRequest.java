package com.pitstop.app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class WorkshopLoginRequest {
    private String username;
    private String password;
}
