package com.skillmapping.controller;

import com.skillmapping.model.Course;
import com.skillmapping.service.CourseService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/courses")
@CrossOrigin
public class CourseController {

    private final CourseService courseService;


    public CourseController(
            CourseService courseService) {

        this.courseService =
                courseService;
    }


    // =========================================================
    // CREATE / PUBLISH COURSE
    // =========================================================

    @PostMapping
    public ResponseEntity<?> createCourse(
            @RequestBody Map<String, Object> request) {

        try {

            Course course =
                    courseService.createCourse(
                            request
                    );


            return ResponseEntity.ok(
                    Map.of(
                            "message",
                            "Course published successfully",

                            "id",
                            course.getId(),

                            "courseName",
                            course.getCourseName(),

                            "companyName",
                            course.getCompanyName(),

                            "status",
                            course.getStatus()
                    )
            );

        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity
                    .badRequest()
                    .body(
                            Map.of(
                                    "message",
                                    e.getMessage() != null
                                            ? e.getMessage()
                                            : "Unable to publish course"
                            )
                    );
        }
    }


    // =========================================================
    // GET ALL PUBLISHED COURSES
    // =========================================================

    @GetMapping("/published")
    public ResponseEntity<?> getPublishedCourses() {

        try {

            return ResponseEntity.ok(
                    courseService
                            .getPublishedCourses()
            );

        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity
                    .internalServerError()
                    .body(
                            Map.of(
                                    "message",
                                    "Unable to load courses"
                            )
                    );
        }
    }


    // =========================================================
    // GET EMPLOYER COURSES
    // =========================================================

    @GetMapping("/employer/{employerId}")
    public ResponseEntity<?> getEmployerCourses(
            @PathVariable String employerId) {

        try {

            return ResponseEntity.ok(
                    courseService
                            .getEmployerCourses(
                                    employerId
                            )
            );

        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity
                    .internalServerError()
                    .body(
                            Map.of(
                                    "message",
                                    "Unable to load employer courses"
                            )
                    );
        }
    }


    // =========================================================
    // GET SINGLE COURSE
    // =========================================================

    @GetMapping("/{id}")
    public ResponseEntity<?> getCourse(
            @PathVariable Long id) {

        try {

            return ResponseEntity.ok(
                    courseService
                            .getCourse(id)
            );

        } catch (Exception e) {

            return ResponseEntity
                    .status(404)
                    .body(
                            Map.of(
                                    "message",
                                    "Course not found"
                            )
                    );
        }
    }
}