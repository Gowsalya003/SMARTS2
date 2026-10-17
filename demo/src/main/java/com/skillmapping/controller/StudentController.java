package com.skillmapping.controller;

import com.skillmapping.model.Student;
import com.skillmapping.service.StudentService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/students1")
@CrossOrigin
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    // =========================================================
    // STUDENT LOGIN
    // =========================================================

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> request) {

        String email = request.get("email");
        String password = request.get("password");

        if (email == null || email.trim().isEmpty()
                || password == null || password.trim().isEmpty()) {

            return ResponseEntity.badRequest().body(
                    Map.of("message", "Email and password are required"));
        }

        email = email.trim();

        String result = studentService.login(email, password);

        switch (result) {

            case "INVALID_EMAIL":
                return ResponseEntity.status(401)
                        .body(Map.of("message", "Invalid email"));

            case "INVALID_PASSWORD":
                return ResponseEntity.status(401)
                        .body(Map.of("message", "Invalid password"));

            case "ACCESS_NOT_GRANTED":
                return ResponseEntity.status(403)
                        .body(Map.of(
                                "message",
                                "Your access has not been granted yet."));

            case "PROFILE_INCOMPLETE":

                Optional<Student> incompleteStudent =
                        studentService.findByEmail(email);

                if (incompleteStudent.isEmpty()) {
                    return ResponseEntity.status(404)
                            .body(Map.of("message", "Student not found"));
                }

                Student incomplete = incompleteStudent.get();

                String incompleteName =
                        (incomplete.getName() != null
                                && !incomplete.getName().isBlank())
                                ? incomplete.getName()
                                : "Student";

                return ResponseEntity.ok(
                        Map.of(
                                "message", "Login successful",
                                "nextPage", "student-profile.html",
                                "name", incompleteName,
                                "email", incomplete.getEmail(),
                                "registerNumber",
                                incomplete.getRegisterNumber()
                        ));

            default:

                Optional<Student> student =
                        studentService.findByEmail(email);

                if (student.isEmpty()) {
                    return ResponseEntity.status(404)
                            .body(Map.of("message", "Student not found"));
                }

                Student s = student.get();

                String name =
                        (s.getName() != null
                                && !s.getName().isBlank())
                                ? s.getName()
                                : "Student";

                return ResponseEntity.ok(
                        Map.of(
                                "message", "Login successful",
                                "nextPage", "student-dashboard.html",
                                "name", name,
                                "email", s.getEmail(),
                                "registerNumber",
                                s.getRegisterNumber()
                        ));
        }
    }

    // =========================================================
    // GET STUDENT PROFILE BY EMAIL
    // =========================================================

    @GetMapping("/profile/email/{email}")
    public ResponseEntity<?> getProfileByEmail(
            @PathVariable String email) {

        Optional<Student> student =
                studentService.findByEmail(email);

        if (student.isEmpty()) {
            return ResponseEntity.status(404)
                    .body(Map.of("message", "Student not found"));
        }

        Student s = student.get();

        Map<String, Object> profile = new LinkedHashMap<>();

        profile.put("registerNumber", s.getRegisterNumber());
        profile.put("name", s.getName());
        profile.put("email", s.getEmail());
        profile.put("phone", s.getPhone());
        profile.put("department", s.getDepartment());
        profile.put("college", s.getCollege());
        profile.put("yearOfStudy", s.getYearOfStudy());
        profile.put("cgpa", s.getCgpa());
        profile.put("graduationYear", s.getGraduationYear());
        profile.put("resumePath", s.getResumePath());

        // Skills
        profile.put("skills", s.getSkills());

        // Certificate name
        profile.put("certificateName", s.getCertificates());

        profile.put("profileCompleted", s.getProfileCompleted());

        return ResponseEntity.ok(profile);
    }

    // =========================================================
    // SAVE / UPDATE PROFILE
    // =========================================================

    @PostMapping(value = "/profile", consumes = "multipart/form-data")
    public ResponseEntity<?> saveProfile(

            @RequestParam("registerNumber")
            String registerNumber,

            @RequestParam("name")
            String name,

            @RequestParam("email")
            String email,

            @RequestParam("phone")
            String phone,

            @RequestParam("department")
            String department,

            @RequestParam("college")
            String college,

            @RequestParam("yearOfStudy")
            String yearOfStudy,

            @RequestParam("cgpa")
            Double cgpa,

            @RequestParam("graduationYear")
            Integer graduationYear,

            // =====================================================
            // SKILLS
            // =====================================================

            @RequestParam(value = "skills", required = false)
            String skills,

            // =====================================================
            // CERTIFICATE NAME
            // =====================================================

            @RequestParam(value = "certificateName", required = false)
            String certificateName,

            // =====================================================
            // RESUME
            // =====================================================

            @RequestParam(value = "resume", required = false)
            MultipartFile resume) {

        try {

            Student student = studentService.saveProfile(

                    registerNumber.trim(),

                    name.trim(),

                    email.trim(),

                    phone.trim(),

                    department.trim(),

                    college.trim(),

                    yearOfStudy,

                    cgpa,

                    graduationYear,

                    skills,

                    certificateName,

                    resume
            );

            return ResponseEntity.ok(
                    Map.of(
                            "message",
                            "Profile saved successfully",

                            "profileCompleted",
                            student.getProfileCompleted(),

                            "nextPage",
                            "student-dashboard.html"
                    )
            );

        } catch (RuntimeException e) {

            return ResponseEntity.status(404)
                    .body(
                            Map.of(
                                    "message",
                                    e.getMessage()
                            )
                    );

        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity.status(500)
                    .body(
                            Map.of(
                                    "message",
                                    "Unable to save profile"
                            )
                    );
        }
    }
}