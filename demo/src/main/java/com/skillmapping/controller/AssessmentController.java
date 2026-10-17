
        package com.skillmapping.controller;

import com.skillmapping.service.AssessmentService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
@RequestMapping("/api/assessments")
@CrossOrigin
public class AssessmentController {

    private final AssessmentService assessmentService;


    public AssessmentController(
            AssessmentService assessmentService
    ) {
        this.assessmentService =
                assessmentService;
    }


    // =========================================================
    // CREATE ASSESSMENT
    // =========================================================

    @PostMapping
    public ResponseEntity<?> createAssessment(
            @RequestBody Map<String, Object> request
    ) {

        try {

            return ResponseEntity.ok(
                    assessmentService.createAssessment(
                            request
                    )
            );

        } catch (Exception e) {

            return ResponseEntity
                    .badRequest()
                    .body(
                            Map.of(
                                    "error",
                                    e.getMessage()
                            )
                    );
        }
    }


    // =========================================================
    // GET PUBLISHED ASSESSMENTS
    // =========================================================

    @GetMapping("/published")
    public ResponseEntity<?> getPublishedAssessments() {

        try {

            return ResponseEntity.ok(
                    assessmentService
                            .getPublishedAssessments()
            );

        } catch (Exception e) {

            return ResponseEntity
                    .badRequest()
                    .body(
                            Map.of(
                                    "error",
                                    e.getMessage()
                            )
                    );
        }
    }

// =========================================================
// GET STUDENT AVAILABLE ASSESSMENTS
// =========================================================
//
// Returns published assessments excluding assessments
// already submitted by this student.
// =========================================================

    @GetMapping("/student/{registerNumber}/available")
    public ResponseEntity<?> getStudentAvailableAssessments(
            @PathVariable String registerNumber
    ) {

        try {

            return ResponseEntity.ok(
                    assessmentService
                            .getStudentAvailableAssessments(
                                    registerNumber
                            )
            );

        } catch (Exception e) {

            return ResponseEntity
                    .badRequest()
                    .body(
                            Map.of(
                                    "error",
                                    e.getMessage()
                            )
                    );
        }
    }
    // =========================================================
    // GET EMPLOYER ASSESSMENTS
    // =========================================================

    @GetMapping("/employer/{employerId}")
    public ResponseEntity<?> getEmployerAssessments(
            @PathVariable String employerId
    ) {

        try {

            return ResponseEntity.ok(
                    assessmentService
                            .getEmployerAssessments(
                                    employerId
                            )
            );

        } catch (Exception e) {

            return ResponseEntity
                    .badRequest()
                    .body(
                            Map.of(
                                    "error",
                                    e.getMessage()
                            )
                    );
        }
    }


    // =========================================================
    // GET SINGLE ASSESSMENT
    // =========================================================

    @GetMapping("/{id}")
    public ResponseEntity<?> getAssessment(
            @PathVariable Long id
    ) {

        try {

            return ResponseEntity.ok(
                    assessmentService
                            .getAssessment(id)
            );

        } catch (Exception e) {

            return ResponseEntity
                    .badRequest()
                    .body(
                            Map.of(
                                    "error",
                                    e.getMessage()
                            )
                    );
        }
    }


    // =========================================================
    // GET SUBMISSIONS FOR EMPLOYER
    // =========================================================

    @GetMapping("/{id}/submissions")
    public ResponseEntity<?> getAssessmentSubmissions(
            @PathVariable Long id
    ) {

        try {

            return ResponseEntity.ok(
                    assessmentService
                            .getAssessmentSubmissions(id)
            );

        } catch (Exception e) {

            return ResponseEntity
                    .badRequest()
                    .body(
                            Map.of(
                                    "error",
                                    e.getMessage()
                            )
                    );
        }
    }


    // =========================================================
    // EMPLOYER PUBLISH SCORES
    // =========================================================

    @PostMapping("/{id}/publish-scores")
    public ResponseEntity<?> publishScores(
            @PathVariable Long id
    ) {

        try {

            return ResponseEntity.ok(
                    assessmentService
                            .publishScores(id)
            );

        } catch (Exception e) {

            return ResponseEntity
                    .badRequest()
                    .body(
                            Map.of(
                                    "error",
                                    e.getMessage()
                            )
                    );
        }
    }


    // =========================================================
    // STUDENT SUBMIT
    // =========================================================

    @PostMapping("/{id}/submit")
    public ResponseEntity<?> submitAssessment(
            @PathVariable Long id,
            @RequestBody Map<String, Object> request
    ) {

        try {

            return ResponseEntity.ok(
                    assessmentService
                            .submitAssessment(
                                    id,
                                    request
                            )
            );

        } catch (Exception e) {

            return ResponseEntity
                    .badRequest()
                    .body(
                            Map.of(
                                    "error",
                                    e.getMessage()
                            )
                    );
        }
    }


    // =========================================================
    // STUDENT RESULTS
    // =========================================================

    @GetMapping("/results/{registerNumber}")
    public ResponseEntity<?> getStudentResults(
            @PathVariable String registerNumber
    ) {

        try {

            return ResponseEntity.ok(
                    assessmentService
                            .getStudentResults(
                                    registerNumber
                            )
            );

        } catch (Exception e) {

            return ResponseEntity
                    .badRequest()
                    .body(
                            Map.of(
                                    "error",
                                    e.getMessage()
                            )
                    );
        }
    }
}

