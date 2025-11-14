package com.pitstop.app.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PersonalInfoResponse {
    private String name;
    private String username;
    private String email;
    private double rating;
}
