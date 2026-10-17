package com.skillmapping.controller;

import com.skillmapping.model.Admin;
import com.skillmapping.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*") // Allows your HTML frontend to communicate with Spring Boot
public class AdminController {

    @Autowired
    private AdminService adminService;

    @PostMapping("/login")
    public ResponseEntity<?> loginAdmin(@RequestBody Map<String, String> credentials) {
        String email = credentials.get("email");
        String password = credentials.get("password");

        if (email == null || password == null) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Email and password are required.");
            return ResponseEntity.badRequest().body(errorResponse);
        }

        Admin admin = adminService.authenticate(email, password);

        if (admin != null) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Login successful");
            response.put("nextPage", "admin-files.html");
            response.put("adminName", admin.getName());
            response.put("email", admin.getEmail());
            return ResponseEntity.ok(response);
        } else {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Invalid email or password.");
            return ResponseEntity.status(401).body(errorResponse);
        }
    }
}