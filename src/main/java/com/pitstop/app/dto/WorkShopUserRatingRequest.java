package com.pitstop.app.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class WorkShopUserRatingRequest {
    private String bookingId;
    private int rating;
}
