package com.skillmapping.controller;

import com.skillmapping.model.Employer;
import com.skillmapping.service.EmployerService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/employers1")
@CrossOrigin
public class EmployerController {

    private final EmployerService employerService;


    public EmployerController(
            EmployerService employerService) {

        this.employerService =
                employerService;
    }


    // =========================================================
    // EMPLOYER LOGIN
    // =========================================================

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody Map<String, String> request) {


        // CHANGED: Extract email instead of employerId
        String email =
                request.get("email");

        String password =
                request.get("password");


        // -----------------------------------------------------
        // VALIDATE INPUT
        // -----------------------------------------------------

        // CHANGED: Validate email instead of employerId
        if (email == null ||
                email.trim().isEmpty() ||
                password == null ||
                password.trim().isEmpty()) {

            return ResponseEntity
                    .badRequest()
                    .body(
                            Map.of(
                                    "message",
                                    "Email and password are required"
                            )
                    );
        }


        // -----------------------------------------------------
        // LOGIN
        // -----------------------------------------------------

        // CHANGED: Pass email to the service layer login method
        String result =
                employerService.login(
                        email.trim(),
                        password
                );


        switch (result) {

            // CHANGED: Case modified to reflect invalid email response
            case "INVALID_EMAIL":

                return ResponseEntity
                        .status(401)
                        .body(
                                Map.of(
                                        "message",
                                        "Invalid email"
                                )
                        );


            case "INVALID_PASSWORD":

                return ResponseEntity
                        .status(401)
                        .body(
                                Map.of(
                                        "message",
                                        "Invalid password"
                                )
                        );


            default:

                // CHANGED: Look up the employer by email upon successful login
                Optional<Employer> employer =
                        employerService
                                .findByEmail(
                                        email.trim()
                                );


                String companyName =
                        "Employer";

                String employerId = "";


                if (employer.isPresent()) {
                    // Extract the actual employerId from the database record
                    if (employer.get().getEmployerId() != null) {
                        employerId = employer.get().getEmployerId();
                    }

                    if (employer.get().getCompanyName() != null &&
                            !employer.get().getCompanyName().isBlank()) {
                        companyName = employer.get().getCompanyName();
                    }
                }


                return ResponseEntity.ok(
                        Map.of(

                                "message",
                                "Login successful",

                                "nextPage",
                                "employer-dashboard.html",

                                // Keeps sending back employerId so frontend profile routes don't break
                                "employerId",
                                employerId,

                                "companyName",
                                companyName
                        )
                );
        }
    }


    // =========================================================
    // GET EMPLOYER PROFILE
    // =========================================================

    @GetMapping("/profile/{employerId}")
    public ResponseEntity<?> getProfile(
            @PathVariable String employerId) {


        Optional<Employer> employer =
                employerService
                        .getProfile(employerId);


        if (employer.isEmpty()) {

            return ResponseEntity
                    .status(404)
                    .body(
                            Map.of(
                                    "message",
                                    "Employer not found"
                            )
                    );
        }


        Employer e =
                employer.get();


        return ResponseEntity.ok(
                Map.of(

                        "employerId",
                        e.getEmployerId(),

                        "companyName",
                        e.getCompanyName() != null
                                ? e.getCompanyName()
                                : "",

                        "email",
                        e.getEmail() != null
                                ? e.getEmail()
                                : "",

                        "phone",
                        e.getPhone() != null
                                ? e.getPhone()
                                : "",

                        "industry",
                        e.getIndustry() != null
                                ? e.getIndustry()
                                : "",

                        "companyDescription",
                        e.getCompanyDescription() != null
                                ? e.getCompanyDescription()
                                : ""
                )
        );
    }
}
