
        package com.skillmapping.model;

import jakarta.persistence.*;

@Entity
@Table(
        name = "assessment_submissions",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {
                                "assessment_id",
                                "student_register_number"
                        }
                )
        }
)
public class Submission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "assessment_id", nullable = false)
    private Long assessmentId;

    @Column(name = "student_register_number", nullable = false)
    private String studentRegisterNumber;

    @Column(name = "student_name")
    private String studentName;

    @Column(nullable = false)
    private Integer score;

    @Column(name = "total_marks", nullable = false)
    private Integer totalMarks;

    @Column(name = "percentage", nullable = false)
    private Double percentage;

    @Column(name = "submitted_at")
    private String submittedAt;

    /*
     * false = student cannot see score
     * true  = employer has published score
     */
    @Column(name = "score_published", nullable = false)
    private Boolean scorePublished = false;


    // =========================
    // GETTERS AND SETTERS
    // =========================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAssessmentId() {
        return assessmentId;
    }

    public void setAssessmentId(Long assessmentId) {
        this.assessmentId = assessmentId;
    }

    public String getStudentRegisterNumber() {
        return studentRegisterNumber;
    }

    public void setStudentRegisterNumber(String studentRegisterNumber) {
        this.studentRegisterNumber = studentRegisterNumber;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public Integer getTotalMarks() {
        return totalMarks;
    }

    public void setTotalMarks(Integer totalMarks) {
        this.totalMarks = totalMarks;
    }

    public Double getPercentage() {
        return percentage;
    }

    public void setPercentage(Double percentage) {
        this.percentage = percentage;
    }

    public String getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(String submittedAt) {
        this.submittedAt = submittedAt;
    }

    public Boolean getScorePublished() {
        return scorePublished;
    }

    public void setScorePublished(Boolean scorePublished) {
        this.scorePublished = scorePublished;
    }
}

