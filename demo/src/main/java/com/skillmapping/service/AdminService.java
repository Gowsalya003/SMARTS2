package com.skillmapping.service;

import com.skillmapping.model.Admin;
import com.skillmapping.repository.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AdminService {

    @Autowired
    private AdminRepository adminRepository;

    public Admin authenticate(String email, String password) {
        Optional<Admin> adminOpt = adminRepository.findByEmail(email.trim());

        if (adminOpt.isPresent()) {
            Admin admin = adminOpt.get();
            // Direct comparison (or use BCryptPasswordEncoder if your passwords are hashed in SQL)
            if (admin.getPassword().equals(password)) {
                return admin;
            }
        }
        return null;
    }
}