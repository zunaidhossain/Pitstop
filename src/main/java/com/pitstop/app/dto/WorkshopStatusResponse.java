package com.pitstop.app.dto;

import com.pitstop.app.constants.WorkshopStatus;
import com.pitstop.app.model.Address;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class WorkshopStatusResponse {
    private String id;
    private String name;
    private String username;
    private WorkshopStatus workshopStatus;
    private Address address;
}
