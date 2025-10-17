package com.pitstop.app.dto;

import com.pitstop.app.constants.WorkshopStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class WorkshopStatusResponse {
    private String username;
    private WorkshopStatus workshopStatus;
}
