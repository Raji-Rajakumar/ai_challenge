package com.busbooking.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class BookingResponse {
    private Long id;
    private Long scheduleId;
    private String seatNumber;
    private String passengerName;
    private String passengerPhone;
    private String status;
    private LocalDateTime bookingTime;
    private Double fare;
    private String busNumber;
    private String routeName;
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
} 