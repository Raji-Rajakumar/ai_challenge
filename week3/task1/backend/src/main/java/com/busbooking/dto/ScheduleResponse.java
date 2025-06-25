package com.busbooking.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ScheduleResponse {
    private Long id;
    private Long busId;
    private Long routeId;
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
    private Double fare;
    private Integer availableSeats;
    private String busNumber;
    private String busName;
    private String routeName;
    private String source;
    private String destination;
} 