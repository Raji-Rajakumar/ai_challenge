package com.busbooking.controller;

import com.busbooking.dto.UserProfileDTO;
import com.busbooking.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserProfileDTO> getUserProfile(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserProfile(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserProfileDTO> updateUserProfile(
            @PathVariable Long id,
            @RequestBody UserProfileDTO userProfileDTO) {
        return ResponseEntity.ok(userService.updateUserProfile(id, userProfileDTO));
    }
} 