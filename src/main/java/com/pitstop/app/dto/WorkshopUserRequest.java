package com.pitstop.app.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WorkshopUserRequest {
    private String name;
    private String username;
    private String email;
    private String password;
}
