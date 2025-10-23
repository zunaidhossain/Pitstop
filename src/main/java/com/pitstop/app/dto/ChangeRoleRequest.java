package com.pitstop.app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ChangeRoleRequest {
    private String id;
    private String role;
}
