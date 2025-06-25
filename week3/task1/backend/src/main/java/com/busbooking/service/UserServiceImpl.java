package com.busbooking.service;

import com.busbooking.dto.UserProfileDTO;
import com.busbooking.entity.User;
import com.busbooking.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserProfileDTO getUserProfile(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return convertToDTO(user);
    }

    @Override
    public UserProfileDTO updateUserProfile(Long id, UserProfileDTO userProfileDTO) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        user.setFullName(userProfileDTO.getFullName());
        user.setPhoneNumber(userProfileDTO.getPhoneNumber());
        
        User updatedUser = userRepository.save(user);
        return convertToDTO(updatedUser);
    }

    private UserProfileDTO convertToDTO(User user) {
        UserProfileDTO dto = new UserProfileDTO();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setFullName(user.getFullName());
        dto.setPhoneNumber(user.getPhoneNumber());
        return dto;
    }
} 