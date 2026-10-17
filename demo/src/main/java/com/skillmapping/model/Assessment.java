
        package com.skillmapping.model;

import jakarta.persistence.*;

@Entity
@Table(name = "assessments")
public class Assessment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false)
    private Integer duration;

    @Column(name = "total_marks", nullable = false)
    private Integer totalMarks;

    @Column(name = "question_count", nullable = false)
    private Integer questionCount;

    @Column(name = "employer_id", nullable = false)
    private String employerId;
    @Column(name = "job_title", nullable = false)
    private String jobTitle;
    @Column(
            name = "required_skills",
            columnDefinition = "LONGTEXT",
            nullable = false
    )
    private String requiredSkillsJson;
    @Column(name = "company_name", nullable = false)
    private String companyName;

    @Column(
            columnDefinition = "LONGTEXT",
            nullable = false
    )
    private String questionsJson;

    @Column(nullable = false)
    private String status;

    @Column(name = "created_at")
    private String createdAt;


    public Assessment() {
    }


    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }
    public String getDescription() {
        return description;
    }
    public String getRequiredSkillsJson() {
        return requiredSkillsJson;
    }

    public void setRequiredSkillsJson(String requiredSkillsJson) {
        this.requiredSkillsJson = requiredSkillsJson;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public Integer getTotalMarks() {
        return totalMarks;
    }

    public void setTotalMarks(Integer totalMarks) {
        this.totalMarks = totalMarks;
    }

    public Integer getQuestionCount() {
        return questionCount;
    }

    public void setQuestionCount(Integer questionCount) {
        this.questionCount = questionCount;
    }

    public String getEmployerId() {
        return employerId;
    }

    public void setEmployerId(String employerId) {
        this.employerId = employerId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getQuestionsJson() {
        return questionsJson;
    }

    public void setQuestionsJson(String questionsJson) {
        this.questionsJson = questionsJson;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}

