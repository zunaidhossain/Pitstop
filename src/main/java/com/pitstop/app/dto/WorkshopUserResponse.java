package com.pitstop.app.dto;

import com.pitstop.app.model.Address;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WorkshopUserResponse {
    private String name;
    private String username;
    private String email;
    private String password;
    private Address address;
}
