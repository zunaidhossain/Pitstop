package com.pitstop.app.model;

import com.pitstop.app.constants.WorkshopStatus;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Document(collection = "workshops")
public class WorkshopUser {

    // Compulsory Attributes to create WorkshopUser Account
    @Id
    private String id;
    private String username;
    private String email;
    private String password;
    private String workshopAddress;
    private List<String> roles;

    // Set these attributes while object creation
    private double currentWalletBalance;
    private double rating;

    @DBRef
    private List<Booking> bookingHistory;
    private WorkshopStatus currentWorkshopStatus;
    private LocalDateTime accountCreationDateTime;
    private LocalDateTime accountLastModifiedDateTime;

    // Additional Attributes
    private boolean enabled;
    private boolean accountNonExpired;
    private boolean accountNonLocked;
    private boolean credentialsNonExpired;

    WorkshopUser(String username, String email, String password, String workshopAddress) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.workshopAddress = workshopAddress;
        roles = new ArrayList<>();
        roles.add("NORMAL_WORKSHOP_USER");

        currentWalletBalance = 0;
        rating = 5;
        bookingHistory = new ArrayList<>();
        currentWorkshopStatus = WorkshopStatus.CLOSED;
        accountCreationDateTime = LocalDateTime.now();
        accountLastModifiedDateTime = LocalDateTime.now();

        enabled = true;
        accountNonExpired = true;
        accountNonLocked = true;
        credentialsNonExpired = true;
    }
}
