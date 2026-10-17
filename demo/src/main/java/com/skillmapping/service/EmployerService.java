package com.skillmapping.service;

import com.skillmapping.model.Employer;
import com.skillmapping.repository.EmployerRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class EmployerService {

    private final EmployerRepository employerRepository;

    public EmployerService(
            EmployerRepository employerRepository) {

        this.employerRepository =
                employerRepository;
    }


    // =========================================================
    // FIND EMPLOYER BY EMPLOYER ID
    // =========================================================

    public Optional<Employer> findByEmployerId(
            String employerId) {

        return employerRepository
                .findByEmployerId(employerId);
    }


    // NEW METHOD NEEDED BY THE CONTROLLER
    // =========================================================
    // FIND EMPLOYER BY EMAIL
    // =========================================================

    public Optional<Employer> findByEmail(
            String email) {

        return employerRepository
                .findByEmail(email);
    }


    // =========================================================
    // LOGIN
    // =========================================================

    public String login(
            String email,
            String password) {

        // CHANGED: Query by email instead of employerId
        Optional<Employer> optionalEmployer =
                employerRepository
                        .findByEmail(email);


        if (optionalEmployer.isEmpty()) {

            // CHANGED: Match the new Controller validation key
            return "INVALID_EMAIL";
        }


        Employer employer =
                optionalEmployer.get();


        if (!employer.getPassword()
                .equals(password)) {

            return "INVALID_PASSWORD";
        }


        return "LOGIN_SUCCESS";
    }


    // =========================================================
    // GET PROFILE
    // =========================================================

    public Optional<Employer> getProfile(
            String employerId) {

        return employerRepository
                .findByEmployerId(employerId);
    }
}
