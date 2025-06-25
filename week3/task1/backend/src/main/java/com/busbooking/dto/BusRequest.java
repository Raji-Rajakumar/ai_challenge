package com.busbooking.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class BusRequest {
    @NotBlank(message = "Bus number is required")
    private String busNumber;

    @NotBlank(message = "Bus name is required")
    private String busName;

    @NotNull(message = "Total seats is required")
    @Positive(message = "Total seats must be positive")
    private Integer totalSeats;

    @NotBlank(message = "Bus type is required")
    private String busType;
} 