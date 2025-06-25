package com.taskmanager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taskmanager.model.User;
import com.taskmanager.service.UserService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@WebServlet("/api/users/*")
public class UserController extends HttpServlet {
    private final UserService userService;
    private final ObjectMapper objectMapper;

    public UserController(UserService userService) {
        this.userService = userService;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        
        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                // Get all users
                response.setContentType("application/json");
                objectMapper.writeValue(response.getOutputStream(), userService.getAllUsers());
            } else {
                // Get user by ID
                Long userId = Long.parseLong(pathInfo.substring(1));
                Optional<User> user = userService.getUserById(userId);
                
                if (user.isPresent()) {
                    response.setContentType("application/json");
                    objectMapper.writeValue(response.getOutputStream(), user.get());
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    sendError(response, "User not found");
                }
            }
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            sendError(response, "Invalid user ID format");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            Map<String, String> requestData = objectMapper.readValue(request.getInputStream(), Map.class);
            String email = requestData.get("email");
            String password = requestData.get("password");

            if (email == null || password == null) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                sendError(response, "Email and password are required");
                return;
            }

            User user = userService.createUser(email, password);
            response.setStatus(HttpServletResponse.SC_CREATED);
            response.setContentType("application/json");
            objectMapper.writeValue(response.getOutputStream(), user);
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            sendError(response, e.getMessage());
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        
        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                sendError(response, "User ID is required");
                return;
            }

            Long userId = Long.parseLong(pathInfo.substring(1));
            Map<String, String> requestData = objectMapper.readValue(request.getInputStream(), Map.class);
            String email = requestData.get("email");
            String password = requestData.get("password");

            User updatedUser = userService.updateUser(userId, email, password);
            response.setContentType("application/json");
            objectMapper.writeValue(response.getOutputStream(), updatedUser);
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            sendError(response, "Invalid user ID format");
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            sendError(response, e.getMessage());
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        
        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                sendError(response, "User ID is required");
                return;
            }

            Long userId = Long.parseLong(pathInfo.substring(1));
            userService.deleteUser(userId);
            response.setStatus(HttpServletResponse.SC_NO_CONTENT);
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            sendError(response, "Invalid user ID format");
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            sendError(response, e.getMessage());
        }
    }

    private void sendError(HttpServletResponse response, String message) throws IOException {
        Map<String, String> error = new HashMap<>();
        error.put("error", message);
        response.setContentType("application/json");
        objectMapper.writeValue(response.getOutputStream(), error);
    }
} 