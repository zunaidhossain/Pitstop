package com.pitstop.app.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Document(collection = "admin-users")
public class AdminUser implements BaseUser{
    @Id
    private String id;
    private String username;
    private String email;
    private String password;
    private List<String> roles = List.of("ADMIN");

    private LocalDateTime accountCreationDateTime = LocalDateTime.now();
    private LocalDateTime accountLastModifiedDateTime = LocalDateTime.now();

    private boolean enabled = true;
}
