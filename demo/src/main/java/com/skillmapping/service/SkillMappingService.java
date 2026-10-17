package com.skillmapping.service;

import com.skillmapping.model.Assessment;
import com.skillmapping.model.Course;
import com.skillmapping.model.Submission;

import com.skillmapping.repository.AssessmentRepository;
import com.skillmapping.repository.CourseRepository;
import com.skillmapping.repository.SubmissionRepository;

import org.springframework.stereotype.Service;

import java.util.*;

/**

 * =========================================================
 * SMARTS - AI Skill Mapping Service
 * =========================================================
 *
 * First version of the SMARTS skill-gap engine.
 *
 * Current flow:
 *
 * Student
 * ↓
 * Published Assessment Result
 * ↓
 * Assessment Category
 * ↓
 * Student Skill Score
 * ↓
 * Required Skill Score
 * ↓
 * Skill Gap
 * ↓
 * Course Recommendation
 *
 * IMPORTANT:
 * This version uses the existing Assessment category
 * as the skill.
 *
 * Example:
 *
 * Assessment category = Programming
 * Student percentage  = 42%
 *
 * Result:
 * Programming skill = 42%
 *
 * SBERT / semantic AI can be added later.
 */
@Service
public class SkillMappingService {

    private final AssessmentRepository assessmentRepository;

    private final SubmissionRepository submissionRepository;

    private final CourseRepository courseRepository;

    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public SkillMappingService(
            AssessmentRepository assessmentRepository,
            SubmissionRepository submissionRepository,
            CourseRepository courseRepository
    ) {


        this.assessmentRepository =
                assessmentRepository;

        this.submissionRepository =
                submissionRepository;

        this.courseRepository =
                courseRepository;


    }

    // =========================================================
    // MAIN METHOD
    // =========================================================
    //
    // Generates complete skill-gap analysis
    // for one student.
    //
    // =========================================================

    public Map<String, Object> generateSkillGapAnalysis(
            String studentRegisterNumber
    ) {


        if (studentRegisterNumber == null
                || studentRegisterNumber.trim().isEmpty()) {

            throw new RuntimeException(
                    "Student register number is required."
            );
        }


        // -----------------------------------------------------
        // Get all submissions of the student
        // -----------------------------------------------------

        List<Submission> submissions =
                submissionRepository
                        .findByStudentRegisterNumberOrderByIdDesc(
                                studentRegisterNumber
                        );


        // -----------------------------------------------------
        // Main response
        // -----------------------------------------------------

        Map<String, Object> response =
                new LinkedHashMap<>();


        response.put(
                "studentRegisterNumber",
                studentRegisterNumber
        );


        response.put(
                "totalSubmissions",
                submissions.size()
        );


        // -----------------------------------------------------
        // Store skill information
        // -----------------------------------------------------

        List<Map<String, Object>> skills =
                new ArrayList<>();


        List<Map<String, Object>> skillGaps =
                new ArrayList<>();


        List<Map<String, Object>> recommendations =
                new ArrayList<>();


        // -----------------------------------------------------
        // Used to avoid processing the same skill multiple
        // times when a student has multiple assessments
        // in the same category.
        // -----------------------------------------------------

        Map<String, Double> skillScores =
                new LinkedHashMap<>();


        Map<String, Integer> skillAttempts =
                new LinkedHashMap<>();


        Map<String, Assessment> skillAssessments =
                new LinkedHashMap<>();


        // =====================================================
        // PROCESS SUBMISSIONS
        // =====================================================

        for (Submission submission : submissions) {


            // -------------------------------------------------
            // Only published scores should be used for
            // skill mapping.
            // -------------------------------------------------

            if (!Boolean.TRUE.equals(
                    submission.getScorePublished()
            )) {

                continue;
            }


            Long assessmentId =
                    submission.getAssessmentId();


            if (assessmentId == null) {
                continue;
            }


            // -------------------------------------------------
            // Find the corresponding assessment
            // -------------------------------------------------

            Optional<Assessment> assessmentOptional =
                    assessmentRepository.findById(
                            assessmentId
                    );


            if (assessmentOptional.isEmpty()) {
                continue;
            }


            Assessment assessment =
                    assessmentOptional.get();


            // -------------------------------------------------
            // Assessment category becomes the skill
            // in this first version.
            // -------------------------------------------------

            String category =
                    assessment.getCategory();


            if (category == null
                    || category.trim().isEmpty()) {

                continue;
            }


            String skill =
                    normalizeSkillName(category);


            // -------------------------------------------------
            // Student percentage
            // -------------------------------------------------

            Double percentage =
                    submission.getPercentage();


            if (percentage == null) {
                continue;
            }


            // Keep score between 0 and 100.

            percentage =
                    Math.max(
                            0,
                            Math.min(
                                    100,
                                    percentage
                            )
                    );


            // -------------------------------------------------
            // If multiple assessments belong to the same
            // category, calculate the average.
            // -------------------------------------------------

            skillScores.put(
                    skill,
                    skillScores.getOrDefault(
                            skill,
                            0.0
                    ) + percentage
            );


            skillAttempts.put(
                    skill,
                    skillAttempts.getOrDefault(
                            skill,
                            0
                    ) + 1
            );


            skillAssessments.put(
                    skill,
                    assessment
            );
        }


        // =====================================================
        // NO PUBLISHED RESULTS
        // =====================================================

        if (skillScores.isEmpty()) {

            response.put(
                    "message",
                    "No published assessment scores are available for skill mapping."
            );


            response.put(
                    "skills",
                    skills
            );


            response.put(
                    "skillGaps",
                    skillGaps
            );


            response.put(
                    "recommendedCourses",
                    recommendations
            );


            return response;
        }


        // =====================================================
        // CREATE SKILL PROFILE + SKILL GAP
        // =====================================================

        for (Map.Entry<String, Double> entry :
                skillScores.entrySet()) {


            String skill =
                    entry.getKey();


            Double totalScore =
                    entry.getValue();


            Integer attempts =
                    skillAttempts.get(skill);


            if (attempts == null
                    || attempts <= 0) {

                continue;
            }


            // -------------------------------------------------
            // Average score for this skill
            // -------------------------------------------------

            double studentScore =
                    totalScore / attempts;


            studentScore =
                    round(studentScore);


            // -------------------------------------------------
            // Required score
            // -------------------------------------------------

            double requiredScore =
                    getRequiredScore(skill);


            // -------------------------------------------------
            // Skill gap
            //
            // If student already meets the required level,
            // gap becomes 0.
            // -------------------------------------------------

            double gap =
                    Math.max(
                            0,
                            requiredScore - studentScore
                    );


            gap =
                    round(gap);


            // -------------------------------------------------
            // Skill level
            // -------------------------------------------------

            String skillLevel =
                    determineSkillLevel(
                            studentScore
                    );


            // =================================================
            // STUDENT SKILL PROFILE
            // =================================================

            Map<String, Object> skillData =
                    new LinkedHashMap<>();


            skillData.put(
                    "skill",
                    skill
            );


            skillData.put(
                    "score",
                    studentScore
            );


            skillData.put(
                    "skillLevel",
                    skillLevel
            );


            skillData.put(
                    "attempts",
                    attempts
            );


            skills.add(
                    skillData
            );


            // =================================================
            // SKILL GAP INFORMATION
            // =================================================

            Map<String, Object> gapData =
                    new LinkedHashMap<>();


            gapData.put(
                    "skill",
                    skill
            );


            gapData.put(
                    "studentScore",
                    studentScore
            );


            gapData.put(
                    "requiredScore",
                    requiredScore
            );


            gapData.put(
                    "gap",
                    gap
            );


            gapData.put(
                    "skillLevel",
                    skillLevel
            );


            // -------------------------------------------------
            // Determine whether the skill is a gap.
            // -------------------------------------------------

            if (gap > 0) {

                gapData.put(
                        "status",
                        "Skill Gap"
                );

            } else {

                gapData.put(
                        "status",
                        "Requirement Met"
                );
            }


            skillGaps.add(
                    gapData
            );
        }


        // =====================================================
        // COURSE RECOMMENDATIONS
        // =====================================================

        recommendations =
                generateCourseRecommendations(
                        skillGaps
                );


        // =====================================================
        // FINAL RESPONSE
        // =====================================================

        response.put(
                "message",
                "Skill gap analysis generated successfully."
        );


        response.put(
                "skills",
                skills
        );


        response.put(
                "skillGaps",
                skillGaps
        );


        response.put(
                "recommendedCourses",
                recommendations
        );


        return response;


    }

    // =========================================================
    // REQUIRED SCORE
    // =========================================================
    //
    // Defines the minimum score SMARTS considers necessary
    // for a skill.
    //
    // These values can later move into the database when
    // employer-specific job requirements are implemented.
    //
    // =========================================================

    private double getRequiredScore(
            String skill
    ) {


        if (skill == null) {
            return 60;
        }


        String normalized =
                skill.trim()
                        .toLowerCase();


        switch (normalized) {

            case "programming":
                return 70;

            case "technical":
            case "technical knowledge":
                return 70;

            case "aptitude":
                return 60;

            case "communication":
                return 60;

            case "logical reasoning":
                return 60;

            case "python":
                return 70;

            case "java":
                return 70;

            case "sql":
                return 60;

            default:
                return 60;
        }


    }

    // =========================================================
    // DETERMINE SKILL LEVEL
    // =========================================================
    //
    // 0 - 39   = Beginner
    // 40 - 69  = Intermediate
    // 70 - 100 = Advanced
    //
    // =========================================================

    private String determineSkillLevel(
            double score
    ) {


        if (score < 40) {

            return "Beginner";
        }


        if (score < 70) {

            return "Intermediate";
        }


        return "Advanced";


    }

    // =========================================================
    // COURSE RECOMMENDATION
    // =========================================================
    //
    // Uses the existing Course table.
    //
    // Matching is currently based on:
    //
    // 1. Course category
    // 2. Course skills
    // 3. Course keywords
    // 4. Course description
    //
    // SBERT semantic matching will replace/improve this
    // section later.
    //
    // =========================================================

    private List<Map<String, Object>>
    generateCourseRecommendations(
            List<Map<String, Object>> skillGaps
    ) {


        List<Map<String, Object>> recommendations =
                new ArrayList<>();


        // -----------------------------------------------------
        // Get published courses
        // -----------------------------------------------------

        List<Course> courses =
                courseRepository
                        .findByStatusOrderByIdDesc(
                                "Published"
                        );


        // -----------------------------------------------------
        // Process each skill gap
        // -----------------------------------------------------

        for (Map<String, Object> gapData :
                skillGaps) {


            Object gapObject =
                    gapData.get("gap");


            if (gapObject == null) {
                continue;
            }


            double gap =
                    Double.parseDouble(
                            String.valueOf(
                                    gapObject
                            )
                    );


            // -------------------------------------------------
            // No recommendation needed if there is no gap.
            // -------------------------------------------------

            if (gap <= 0) {
                continue;
            }


            String skill =
                    String.valueOf(
                            gapData.get("skill")
                    );


            // -------------------------------------------------
            // Find matching courses
            // -------------------------------------------------

            for (Course course : courses) {


                double matchScore =
                        calculateCourseMatchScore(
                                skill,
                                course
                        );


                if (matchScore <= 0) {
                    continue;
                }


                Map<String, Object> recommendation =
                        new LinkedHashMap<>();


                recommendation.put(
                        "skillGap",
                        skill
                );


                recommendation.put(
                        "gap",
                        gap
                );


                recommendation.put(
                        "courseId",
                        course.getId()
                );


                recommendation.put(
                        "courseName",
                        course.getCourseName()
                );


                recommendation.put(
                        "category",
                        course.getCategory()
                );


                recommendation.put(
                        "description",
                        course.getDescription()
                );


                recommendation.put(
                        "skills",
                        course.getSkills()
                );


                recommendation.put(
                        "level",
                        course.getLevel()
                );


                recommendation.put(
                        "duration",
                        course.getDuration()
                );


                recommendation.put(
                        "prerequisites",
                        course.getPrerequisites()
                );


                recommendation.put(
                        "learningOutcomes",
                        course.getLearningOutcomes()
                );


                recommendation.put(
                        "keywords",
                        course.getKeywords()
                );


                recommendation.put(
                        "matchScore",
                        round(matchScore)
                );


                recommendations.add(
                        recommendation
                );
            }
        }


        // -----------------------------------------------------
        // Sort recommendations by highest match score.
        // -----------------------------------------------------

        recommendations.sort(
                (a, b) -> {

                    double scoreA =
                            Double.parseDouble(
                                    String.valueOf(
                                            a.get("matchScore")
                                    )
                            );


                    double scoreB =
                            Double.parseDouble(
                                    String.valueOf(
                                            b.get("matchScore")
                                    )
                            );


                    return Double.compare(
                            scoreB,
                            scoreA
                    );
                }
        );


        // -----------------------------------------------------
        // Remove duplicate course + skill combinations.
        // -----------------------------------------------------

        List<Map<String, Object>> uniqueRecommendations =
                new ArrayList<>();


        Set<String> seen =
                new HashSet<>();


        for (Map<String, Object> recommendation :
                recommendations) {


            String key =
                    String.valueOf(
                            recommendation.get(
                                    "skillGap"
                            )
                    )
                            + "_"
                            + String.valueOf(
                            recommendation.get(
                                    "courseId"
                            )
                    );


            if (seen.add(key)) {

                uniqueRecommendations.add(
                        recommendation
                );
            }
        }


        return uniqueRecommendations;


    }

    // =========================================================
    // COURSE MATCH SCORE
    // =========================================================
    //
    // Current baseline matching algorithm.
    //
    // This is NOT the final AI model.
    //
    // Later:
    //
    // Course text + Skill text
    //        ↓
    //      SBERT
    //        ↓
    // Semantic similarity
    //
    // =========================================================


    private double calculateCourseMatchScore(
            String skill,
            Course course
    ) {

        if (skill == null || course == null) {
            return 0;
        }

        String targetSkill =
                normalizeText(skill);

        double score = 0;

        /*
         * ---------------------------------------------------------
         * 1. Direct skill matching
         * ---------------------------------------------------------
         */

        if (containsIgnoreCase(
                course.getCategory(),
                targetSkill
        )) {
            score += 50;
        }

        if (containsIgnoreCase(
                course.getSkills(),
                targetSkill
        )) {
            score += 30;
        }

        if (containsIgnoreCase(
                course.getKeywords(),
                targetSkill
        )) {
            score += 15;
        }

        if (containsIgnoreCase(
                course.getDescription(),
                targetSkill
        )) {
            score += 5;
        }


        /*
         * ---------------------------------------------------------
         * 2. Technical skill mapping
         * ---------------------------------------------------------
         *
         * Technical assessments can relate to:
         *
         * Programming
         * Python
         * Java
         * DSA
         * SQL
         * Database
         * Web Development
         * JavaScript
         * React
         * Machine Learning
         * AI/ML
         * Deep Learning
         * Cloud
         * Computer Science
         */

        if (targetSkill.equals("technical")) {

            String technicalText =
                    normalizeText(
                            String.valueOf(
                                    course.getCourseName()
                            )
                                    + " "
                                    + String.valueOf(
                                    course.getCategory()
                            )
                                    + " "
                                    + String.valueOf(
                                    course.getDescription()
                            )
                                    + " "
                                    + String.valueOf(
                                    course.getSkills()
                            )
                                    + " "
                                    + String.valueOf(
                                    course.getKeywords()
                            )
                    );


            if (technicalText.contains("programming")) {
                score += 40;
            }

            if (technicalText.contains("python")) {
                score += 35;
            }

            if (technicalText.contains("java")) {
                score += 35;
            }

            if (technicalText.contains("dsa")) {
                score += 30;
            }

            if (technicalText.contains("algorithms")) {
                score += 30;
            }

            if (technicalText.contains("sql")) {
                score += 30;
            }

            if (technicalText.contains("database")) {
                score += 25;
            }

            if (technicalText.contains("web development")) {
                score += 25;
            }

            if (technicalText.contains("javascript")) {
                score += 25;
            }

            if (technicalText.contains("react")) {
                score += 25;
            }

            if (technicalText.contains("machine learning")) {
                score += 30;
            }

            if (technicalText.contains("ai/ml")) {
                score += 30;
            }

            if (technicalText.contains("deep learning")) {
                score += 30;
            }

            if (technicalText.contains("cloud")) {
                score += 20;
            }

            if (technicalText.contains("computer science")) {
                score += 20;
            }
        }


        /*
         * ---------------------------------------------------------
         * 3. Limit match score to 100
         * ---------------------------------------------------------
         */

        return Math.min(
                100,
                score
        );
    }



    // =========================================================
    // TEXT MATCH
    // =========================================================

    private boolean containsIgnoreCase(
            String text,
            String search
    ) {


        if (text == null
                || search == null) {

            return false;
        }


        return normalizeText(text)
                .contains(
                        normalizeText(search)
                );


    }

    // =========================================================
    // NORMALIZE SKILL NAME
    // =========================================================

    private String normalizeSkillName(
            String skill
    ) {


        if (skill == null) {
            return "";
        }


        String value =
                skill.trim();


        if (value.isEmpty()) {
            return "";
        }


        // Convert common variations into one name.

        if (value.equalsIgnoreCase(
                "Technical Knowledge"
        )) {

            return "Technical";
        }


        if (value.equalsIgnoreCase(
                "Logical"
        )) {

            return "Logical Reasoning";
        }


        return value;


    }

    // =========================================================
    // NORMALIZE TEXT
    // =========================================================

    private String normalizeText(
            String text
    ) {


        if (text == null) {
            return "";
        }


        return text
                .trim()
                .toLowerCase()
                .replaceAll(
                        "\\s+",
                        " "
                );


    }

    // =========================================================
    // ROUND VALUE
    // =========================================================

    private double round(
            double value
    ) {


        return Math.round(
                value * 100.0
        ) / 100.0;


    }
}
