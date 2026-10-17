package com.skillmapping.model;

import jakarta.persistence.*;

@Entity
@Table(name = "employers1")
public class Employer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "employer_id", unique = true, nullable = false)
    private String employerId;

    @Column(nullable = false)
    private String password;

    @Column(name = "company_name", nullable = false)
    private String companyName;

    // CHANGED: Explicitly mapped and marked unique for secure logins
    @Column(name = "email", unique = true, nullable = false)
    private String email;

    private String phone;

    private String industry;

    @Column(name = "company_description")
    private String companyDescription;


    // =========================================================
    // GETTERS / SETTERS
    // =========================================================

    public Long getId() {
        return id;
    }


    public String getEmployerId() {
        return employerId;
    }

    public void setEmployerId(String employerId) {
        this.employerId = employerId;
    }


    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
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


    public String getIndustry() {
        return industry;
    }

    public void setIndustry(String industry) {
        this.industry = industry;
    }


    public String getCompanyDescription() {
        return companyDescription;
    }

    public void setCompanyDescription(String companyDescription) {
        this.companyDescription = companyDescription;
    }
}
