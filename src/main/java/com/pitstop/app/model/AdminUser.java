package com.pitstop.app.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Document(collection = "admin-users")
public class AdminUser implements BaseUser{
    @Id
    private String id;
    private String name;
    private String username;
    private String email;
    private String password;
    private UserType userType = UserType.ADMIN;
    private List<String> roles = new ArrayList<>();

    public AdminUser() {
        this.roles.add("ADMIN");
    }

    private LocalDateTime accountCreationDateTime = LocalDateTime.now();
    private LocalDateTime accountLastModifiedDateTime = LocalDateTime.now();

    private boolean enabled = true;
}
