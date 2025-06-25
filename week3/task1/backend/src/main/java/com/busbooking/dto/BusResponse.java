package com.busbooking.dto;

import lombok.Data;

@Data
public class BusResponse {
    private Long id;
    private String busNumber;
    private String busName;
    private Integer totalSeats;
    private String busType;
    private Integer availableSeats;
} 