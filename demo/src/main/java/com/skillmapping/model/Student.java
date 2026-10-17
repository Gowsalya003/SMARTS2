package com.skillmapping.model;

import jakarta.persistence.*;

@Entity
@Table(name = "students1")
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "register_number", unique = true, nullable = false)
    private String registerNumber;

    @Column(nullable = false)
    private String password;

    @Column(name = "access_granted")
    private Boolean accessGranted = false;

    private String name;

    private String email;

    private String phone;

    private String department;

    private String college;

    @Column(name = "year_of_study")
    private String yearOfStudy;

    private Double cgpa;

    @Column(name = "graduation_year")
    private Integer graduationYear;

    @Column(name = "resume_path")
    private String resumePath;

    @Column(name = "profile_completed")
    private Boolean profileCompleted = false;

    // ===========================
    // NEW FIELDS
    // ===========================

    @Column(columnDefinition = "TEXT")
    private String skills;

    @Column(columnDefinition = "TEXT")
    private String certificates;

    // ===========================
    // GETTERS & SETTERS
    // ===========================

    public Long getId() {
        return id;
    }

    public String getRegisterNumber() {
        return registerNumber;
    }

    public void setRegisterNumber(String registerNumber) {
        this.registerNumber = registerNumber;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getAccessGranted() {
        return accessGranted;
    }

    public void setAccessGranted(Boolean accessGranted) {
        this.accessGranted = accessGranted;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getCollege() {
        return college;
    }

    public void setCollege(String college) {
        this.college = college;
    }

    public String getYearOfStudy() {
        return yearOfStudy;
    }

    public void setYearOfStudy(String yearOfStudy) {
        this.yearOfStudy = yearOfStudy;
    }

    public Double getCgpa() {
        return cgpa;
    }

    public void setCgpa(Double cgpa) {
        this.cgpa = cgpa;
    }

    public Integer getGraduationYear() {
        return graduationYear;
    }

    public void setGraduationYear(Integer graduationYear) {
        this.graduationYear = graduationYear;
    }

    public String getResumePath() {
        return resumePath;
    }

    public void setResumePath(String resumePath) {
        this.resumePath = resumePath;
    }

    public Boolean getProfileCompleted() {
        return profileCompleted;
    }

    public void setProfileCompleted(Boolean profileCompleted) {
        this.profileCompleted = profileCompleted;
    }

    // ===========================
    // NEW GETTERS & SETTERS
    // ===========================

    public String getSkills() {
        return skills;
    }

    public void setSkills(String skills) {
        this.skills = skills;
    }

    public String getCertificates() {
        return certificates;
    }

    public void setCertificates(String certificates) {
        this.certificates = certificates;
    }
}