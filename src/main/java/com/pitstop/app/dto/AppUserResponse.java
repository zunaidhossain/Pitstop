package com.pitstop.app.dto;

import com.pitstop.app.model.Address;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AppUserResponse {
    private String name;
    private String email;
    private String password;
    List<Address> addresses;
}
