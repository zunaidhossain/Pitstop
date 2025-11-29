package com.pitstop.app.model;

import com.pitstop.app.constants.WorkshopServiceType;
import com.pitstop.app.constants.WorkshopStatus;
import com.pitstop.app.constants.WorkshopVehicleType;
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
    private String name;
    private String username;
    private String email;
    private String password;
    private Address workshopAddress;
    private List<String> roles = List.of("WORKSHOP");

    private double currentWalletBalance = 0.0;
    private double rating = 5.0;
    private List<Integer> ratingsList = new ArrayList<>();
    private List<WorkshopServiceType> servicesOffered = new ArrayList<>();
    private WorkshopVehicleType vehicleTypeSupported;

    @DBRef
    private List<Booking> bookingHistory = new ArrayList<>();

    private WorkshopStatus currentWorkshopStatus = WorkshopStatus.CLOSED;
    private LocalDateTime accountCreationDateTime = LocalDateTime.now();
    private LocalDateTime accountLastModifiedDateTime = LocalDateTime.now();

    private boolean enabled = true;
}
