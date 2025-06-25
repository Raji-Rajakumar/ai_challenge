package com.busbooking.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BookingRequest {
    @NotNull(message = "Schedule ID is required")
    private Long scheduleId;

    @NotBlank(message = "Seat number is required")
    private String seatNumber;

    @NotBlank(message = "Passenger name is required")
    private String passengerName;

    @NotBlank(message = "Passenger phone is required")
    private String passengerPhone;
} 