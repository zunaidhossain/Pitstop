package com.pitstop.app.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class AdminUserRegisterResponse {
    private String id;
    private String username;
    private String name;
    private String email;
    private LocalDateTime createdAt;
    private String message;
}
