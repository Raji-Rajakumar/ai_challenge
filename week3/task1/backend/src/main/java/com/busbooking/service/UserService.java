package com.busbooking.service;

import com.busbooking.dto.UserProfileDTO;

public interface UserService {
    UserProfileDTO getUserProfile(Long id);
    UserProfileDTO updateUserProfile(Long id, UserProfileDTO userProfileDTO);
} 