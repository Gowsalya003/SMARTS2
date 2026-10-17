
        package com.skillmapping.repository;

import com.skillmapping.model.Assessment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AssessmentRepository
        extends JpaRepository<Assessment, Long> {

    // Get assessments by status
    List<Assessment> findByStatus(String status);

    // Get assessments posted by a particular employer
    List<Assessment> findByEmployerId(String employerId);
}

