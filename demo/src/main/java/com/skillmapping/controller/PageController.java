
package com.skillmapping.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @GetMapping("/student/login")
    public String studentLogin() {
        return "forward:/student-login.html";
    }

    @GetMapping("/company/login")
    public String companyLogin() {
        return "forward:/company-login.html";
    }

    @GetMapping("/admin/login")
    public String adminLogin() {
        return "forward:/admin-login.html";
    }

    @GetMapping("/company/dashboard")
    public String employerDashboard() {
        return "forward:/employer-dashboard.html";
    }
}
