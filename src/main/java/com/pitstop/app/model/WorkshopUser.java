package com.pitstop.app.model;

import com.pitstop.app.constants.WorkshopStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
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
@NoArgsConstructor
public class WorkshopUser implements BaseUser{

    @Id
    private String id;
    private String username;
    private String email;
    private String password;
    private Address workshopAddress;
    private List<String> roles = List.of("WORKSHOP");

    private double currentWalletBalance = 0.0;
    private double rating = 0.0;

    @DBRef
    private List<Booking> bookingHistory = new ArrayList<>();

    private WorkshopStatus currentWorkshopStatus = WorkshopStatus.CLOSED;
    private LocalDateTime accountCreationDateTime = LocalDateTime.now();
    private LocalDateTime accountLastModifiedDateTime = LocalDateTime.now();

    private boolean enabled = true;
}
