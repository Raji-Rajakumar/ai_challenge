package com.busbooking.dto;

import lombok.Data;

@Data
public class UserProfileDTO {
    private Long id;
    private String email;
    private String fullName;
    private String phoneNumber;
} 