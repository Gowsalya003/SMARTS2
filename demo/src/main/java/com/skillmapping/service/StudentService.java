package com.skillmapping.service;

import com.skillmapping.model.Student;
import com.skillmapping.repository.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.UUID;

@Service
public class StudentService {

    private final StudentRepository studentRepository;

    private final Path uploadDirectory =
            Paths.get("uploads/resumes");

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    // =========================================================
    // FIND STUDENT BY EMAIL
    // =========================================================

    public Optional<Student> findByEmail(String email) {
        return studentRepository.findByEmail(email);
    }

    // =========================================================
    // LOGIN USING EMAIL
    // =========================================================

    public String login(String email, String password) {

        Optional<Student> optionalStudent =
                studentRepository.findByEmail(email);

        if (optionalStudent.isEmpty()) {
            return "INVALID_EMAIL";
        }

        Student student = optionalStudent.get();

        if (!student.getPassword().equals(password)) {
            return "INVALID_PASSWORD";
        }

        if (!Boolean.TRUE.equals(student.getAccessGranted())) {
            return "ACCESS_NOT_GRANTED";
        }

        if (!Boolean.TRUE.equals(student.getProfileCompleted())) {
            return "PROFILE_INCOMPLETE";
        }

        return "LOGIN_SUCCESS";
    }

    // =========================================================
    // SAVE / UPDATE PROFILE
    // =========================================================

    public Student saveProfile(
            String registerNumber,
            String name,
            String email,
            String phone,
            String department,
            String college,
            String yearOfStudy,
            Double cgpa,
            Integer graduationYear,
            String skills,
            String certificateName,
            MultipartFile resume
    ) throws IOException {

        Optional<Student> optionalStudent =
                studentRepository.findByRegisterNumber(registerNumber);

        if (optionalStudent.isEmpty()) {
            throw new RuntimeException("Student not found");
        }

        Student student = optionalStudent.get();

        student.setName(name);
        student.setEmail(email);
        student.setPhone(phone);
        student.setDepartment(department);
        student.setCollege(college);
        student.setYearOfStudy(yearOfStudy);
        student.setCgpa(cgpa);
        student.setGraduationYear(graduationYear);

        // =====================================================
        // SAVE SKILLS
        // =====================================================

        student.setSkills(skills);

        // =====================================================
        // SAVE CERTIFICATE NAME
        // =====================================================

        student.setCertificates(certificateName);

        // =====================================================
        // RESUME UPLOAD
        // =====================================================

        if (resume != null && !resume.isEmpty()) {

            Files.createDirectories(uploadDirectory);

            String originalFileName = resume.getOriginalFilename();

            String extension = "";

            if (originalFileName != null && originalFileName.contains(".")) {
                extension = originalFileName.substring(
                        originalFileName.lastIndexOf(".")
                );
            }

            String fileName =
                    registerNumber + "_" + UUID.randomUUID() + extension;

            Path filePath = uploadDirectory.resolve(fileName);

            Files.write(filePath, resume.getBytes());

            student.setResumePath(filePath.toString());
        }

        student.setProfileCompleted(true);

        return studentRepository.save(student);
    }
}