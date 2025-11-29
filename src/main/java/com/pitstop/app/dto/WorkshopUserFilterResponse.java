package com.pitstop.app.dto;

import com.pitstop.app.constants.WorkshopServiceType;
import com.pitstop.app.constants.WorkshopVehicleType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WorkshopUserFilterResponse {
    private String workshopId;
    private String workshopName;
    private double distanceKm;
    private WorkshopVehicleType vehicleType;
    private WorkshopServiceType serviceType;
    private String formattedAddress;
    private Double latitude;
    private Double longitude;
}
