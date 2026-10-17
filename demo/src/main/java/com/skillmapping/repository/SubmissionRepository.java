package com.skillmapping.repository;

import com.skillmapping.model.Submission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SubmissionRepository
        extends JpaRepository<Submission, Long> {

    Optional<Submission> findByAssessmentIdAndStudentRegisterNumber(
            Long assessmentId,
            String studentRegisterNumber
    );

    List<Submission> findByStudentRegisterNumberOrderByIdDesc(
            String studentRegisterNumber
    );

    List<Submission> findByAssessmentIdOrderByIdDesc(
            Long assessmentId
    );
}