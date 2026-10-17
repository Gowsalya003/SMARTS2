package com.skillmapping.repository;

import com.skillmapping.model.Employer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmployerRepository
        extends JpaRepository<Employer, Long> {

    // CHANGED: Fixed method name to match your entity field ("email") instead of "emailId"
    Optional<Employer> findByEmail(String email);

    // RESTORED: Needed for your getProfile() service calls to compile successfully
    Optional<Employer> findByEmployerId(String employerId);
}
