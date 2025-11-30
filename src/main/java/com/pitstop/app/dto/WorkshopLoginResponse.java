package com.pitstop.app.dto;

import lombok.*;

@Data
@AllArgsConstructor
public class WorkshopLoginResponse {
    private String username;
    private String token;
    private String message;
}
