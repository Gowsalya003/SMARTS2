package com.skillmapping.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.skillmapping.model.Assessment;
import com.skillmapping.model.Question;
import com.skillmapping.model.Submission;

import com.skillmapping.repository.AssessmentRepository;
import com.skillmapping.repository.QuestionRepository;
import com.skillmapping.repository.SubmissionRepository;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;


@Service
public class AssessmentService {

    private final AssessmentRepository assessmentRepository;
    private final QuestionRepository questionRepository;
    private final SubmissionRepository submissionRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();


    public AssessmentService(
            AssessmentRepository assessmentRepository,
            QuestionRepository questionRepository,
            SubmissionRepository submissionRepository
    ) {
        this.assessmentRepository = assessmentRepository;
        this.questionRepository = questionRepository;
        this.submissionRepository = submissionRepository;
    }


    // =========================================================
    // CREATE ASSESSMENT
    // =========================================================

    public Assessment createAssessment(
            Map<String, Object> request
    ) throws Exception {

        String title = getString(request, "title");
        String description = getString(request, "description");
        String category = getString(request, "category");

        String jobTitle = getString(request, "jobTitle");

        List<String> requiredSkills =
                objectMapper.convertValue(
                        request.get("requiredSkills"),
                        new TypeReference<List<String>>() {}
                );

        Integer duration = getInteger(request, "duration");

        String employerId =
                getString(request, "employerId");

        String companyName =
                getString(request, "companyName");


        List<Map<String, Object>> questions =
                objectMapper.convertValue(
                        request.get("questions"),
                        new TypeReference<List<Map<String, Object>>>() {}
                );


        if (questions == null || questions.isEmpty()) {
            throw new RuntimeException(
                    "At least one question is required."
            );
        }


        int questionCount = questions.size();

        int totalMarks = 0;


        for (Map<String, Object> q : questions) {

            Integer marks = getInteger(q, "marks");

            if (marks == null || marks <= 0) {
                marks = 1;
            }

            totalMarks += marks;
        }


        // =========================================================
        // CREATE ASSESSMENT
        // =========================================================

        Assessment assessment = new Assessment();

        assessment.setTitle(title);
        assessment.setDescription(description);
        assessment.setCategory(category);

        assessment.setJobTitle(jobTitle);

        assessment.setRequiredSkillsJson(
                objectMapper.writeValueAsString(
                        requiredSkills != null
                                ? requiredSkills
                                : new ArrayList<>()
                )
        );

        assessment.setDuration(duration);

        assessment.setEmployerId(employerId);
        assessment.setCompanyName(companyName);

        assessment.setQuestionCount(questionCount);
        assessment.setTotalMarks(totalMarks);

        assessment.setStatus("Published");

        assessment.setCreatedAt(
                LocalDateTime.now().toString()
        );


        // Store original question JSON
        assessment.setQuestionsJson(
                objectMapper.writeValueAsString(questions)
        );


        Assessment savedAssessment =
                assessmentRepository.save(assessment);


        // =========================================================
        // SAVE QUESTIONS
        // =========================================================

        for (Map<String, Object> q : questions) {

            Question question = new Question();

            question.setAssessmentId(
                    savedAssessment.getId()
            );


            // =====================================================
            // QUESTION TEXT
            // =====================================================

            String questionText =
                    firstNonNull(
                            getString(q, "questionText"),
                            getString(q, "question"),
                            getString(q, "text")
                    );

            if (questionText == null ||
                    questionText.trim().isEmpty()) {

                throw new RuntimeException(
                        "Question text is missing."
                );
            }

            question.setQuestionText(
                    questionText.trim()
            );


            // =====================================================
            // QUESTION TYPE
            // =====================================================

            String questionType =
                    firstNonNull(
                            getString(q, "type"),
                            getString(q, "questionType")
                    );

            if (questionType == null ||
                    questionType.trim().isEmpty()) {

                questionType = "MCQ";
            }

            questionType =
                    questionType.trim().toUpperCase();

            question.setType(questionType);


            // =====================================================
            // OPTIONS
            // =====================================================

            String optionA = null;
            String optionB = null;
            String optionC = null;
            String optionD = null;


            /*
             * Frontend currently sends:
             *
             * options: [
             *     "...",
             *     "...",
             *     "...",
             *     "..."
             * ]
             *
             * So we must explicitly convert that array
             * into optionA / optionB / optionC / optionD.
             */

            Object optionsObject =
                    q.get("options");


            if (optionsObject != null) {

                try {

                    List<String> options =
                            objectMapper.convertValue(
                                    optionsObject,
                                    new TypeReference<List<String>>() {}
                            );


                    if (options.size() > 0) {
                        optionA = cleanString(options.get(0));
                    }

                    if (options.size() > 1) {
                        optionB = cleanString(options.get(1));
                    }

                    if (options.size() > 2) {
                        optionC = cleanString(options.get(2));
                    }

                    if (options.size() > 3) {
                        optionD = cleanString(options.get(3));
                    }

                } catch (Exception ignored) {
                    // Continue with individual option fields
                }
            }


            // =====================================================
            // INDIVIDUAL OPTION FALLBACK
            // =====================================================

            optionA =
                    firstNonNull(
                            optionA,
                            getString(q, "optionA"),
                            getString(q, "a")
                    );

            optionB =
                    firstNonNull(
                            optionB,
                            getString(q, "optionB"),
                            getString(q, "b")
                    );

            optionC =
                    firstNonNull(
                            optionC,
                            getString(q, "optionC"),
                            getString(q, "c")
                    );

            optionD =
                    firstNonNull(
                            optionD,
                            getString(q, "optionD"),
                            getString(q, "d")
                    );


            // =====================================================
            // TRUE / FALSE
            // =====================================================

            if ("TRUE_FALSE".equals(questionType)
                    || "TRUEFALSE".equals(questionType)
                    || "TF".equals(questionType)
                    || "BOOLEAN".equals(questionType)) {

                optionA = "True";
                optionB = "False";
                optionC = null;
                optionD = null;
            }


            question.setOptionA(optionA);
            question.setOptionB(optionB);
            question.setOptionC(optionC);
            question.setOptionD(optionD);


            // =====================================================
            // CORRECT ANSWER
            // =====================================================

            String correctAnswer =
                    firstNonNull(
                            getString(q, "correctAnswer"),
                            getString(q, "answer")
                    );


            if (correctAnswer != null) {
                correctAnswer =
                        correctAnswer.trim();
            }


            /*
             * MCQ:
             * correctAnswer = A / B / C / D
             *
             * TRUE_FALSE:
             * correctAnswer = True / False
             *
             * SHORT_ANSWER:
             * correctAnswer = expected text
             */

            question.setCorrectAnswer(
                    correctAnswer
            );


            // =====================================================
            // MARKS
            // =====================================================

            Integer marks =
                    getInteger(q, "marks");

            if (marks == null || marks <= 0) {
                marks = 1;
            }

            question.setMarks(marks);


            // =====================================================
            // SAVE QUESTION
            // =====================================================

            questionRepository.save(question);
        }


        return savedAssessment;
    }


    // =========================================================
    // GET PUBLISHED ASSESSMENTS
    // =========================================================

    public List<Map<String, Object>>
    getPublishedAssessments() {

        List<Assessment> assessments =
                assessmentRepository.findByStatus("Published");


        List<Map<String, Object>> result =
                new ArrayList<>();


        for (Assessment assessment : assessments) {

            Map<String, Object> map =
                    new LinkedHashMap<>();

            map.put("id", assessment.getId());
            map.put("title", assessment.getTitle());
            map.put("description", assessment.getDescription());
            map.put("category", assessment.getCategory());
            map.put("jobTitle", assessment.getJobTitle());
            map.put("duration", assessment.getDuration());
            map.put("totalMarks", assessment.getTotalMarks());
            map.put("questionCount", assessment.getQuestionCount());
            map.put("employerId", assessment.getEmployerId());
            map.put("companyName", assessment.getCompanyName());
            map.put("status", assessment.getStatus());
            map.put("createdAt", assessment.getCreatedAt());

            result.add(map);
        }


        return result;
    }


    // =========================================================
    // GET STUDENT AVAILABLE ASSESSMENTS
    // =========================================================

    public List<Map<String, Object>>
    getStudentAvailableAssessments(
            String registerNumber
    ) {

        List<Assessment> assessments =
                assessmentRepository.findByStatus("Published");


        List<Submission> submissions =
                submissionRepository
                        .findByStudentRegisterNumberOrderByIdDesc(
                                registerNumber
                        );


        Set<Long> submittedAssessmentIds =
                new HashSet<>();


        for (Submission submission : submissions) {

            if (submission.getAssessmentId() != null) {

                submittedAssessmentIds.add(
                        submission.getAssessmentId()
                );
            }
        }


        List<Map<String, Object>> result =
                new ArrayList<>();


        for (Assessment assessment : assessments) {

            if (submittedAssessmentIds.contains(
                    assessment.getId()
            )) {

                continue;
            }


            Map<String, Object> map =
                    new LinkedHashMap<>();


            map.put("id", assessment.getId());
            map.put("title", assessment.getTitle());
            map.put("description", assessment.getDescription());
            map.put("category", assessment.getCategory());
            map.put("jobTitle", assessment.getJobTitle());
            map.put("duration", assessment.getDuration());
            map.put("totalMarks", assessment.getTotalMarks());
            map.put("questionCount", assessment.getQuestionCount());
            map.put("employerId", assessment.getEmployerId());
            map.put("companyName", assessment.getCompanyName());
            map.put("status", assessment.getStatus());
            map.put("createdAt", assessment.getCreatedAt());


            result.add(map);
        }


        return result;
    }


    // =========================================================
    // GET SINGLE ASSESSMENT
    // =========================================================

    public Map<String, Object>
    getAssessment(Long id) {

        Assessment assessment =
                assessmentRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Assessment not found."
                                )
                        );


        List<Question> questions =
                questionRepository
                        .findByAssessmentId(id);


        Map<String, Object> response =
                new LinkedHashMap<>();


        response.put("id", assessment.getId());
        response.put("title", assessment.getTitle());
        response.put("description", assessment.getDescription());
        response.put("category", assessment.getCategory());
        response.put("jobTitle", assessment.getJobTitle());
        response.put("duration", assessment.getDuration());
        response.put("totalMarks", assessment.getTotalMarks());
        response.put("questionCount", questions.size());
        response.put("employerId", assessment.getEmployerId());
        response.put("companyName", assessment.getCompanyName());
        response.put("status", assessment.getStatus());
        response.put("createdAt", assessment.getCreatedAt());


        // =========================================================
        // QUESTIONS
        // =========================================================

        List<Map<String, Object>> questionList =
                new ArrayList<>();


        for (Question question : questions) {

            Map<String, Object> q =
                    new LinkedHashMap<>();

            q.put("id", question.getId());

            q.put(
                    "question",
                    question.getQuestionText()
            );

            q.put(
                    "type",
                    question.getType()
            );

            q.put(
                    "optionA",
                    question.getOptionA()
            );

            q.put(
                    "optionB",
                    question.getOptionB()
            );

            q.put(
                    "optionC",
                    question.getOptionC()
            );

            q.put(
                    "optionD",
                    question.getOptionD()
            );

            q.put(
                    "marks",
                    question.getMarks()
            );


            // DO NOT send correctAnswer to student.

            questionList.add(q);
        }


        response.put(
                "questions",
                questionList
        );


        return response;
    }


    // =========================================================
    // SUBMIT ASSESSMENT
    // =========================================================

    public Map<String, Object>
    submitAssessment(
            Long assessmentId,
            Map<String, Object> request
    ) {

        assessmentRepository.findById(assessmentId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Assessment not found."
                        )
                );


        String registerNumber =
                getString(
                        request,
                        "studentRegisterNumber"
                );


        if (registerNumber == null ||
                registerNumber.trim().isEmpty()) {

            throw new RuntimeException(
                    "Student register number is missing."
            );
        }


        registerNumber = registerNumber.trim();


        String studentName =
                getString(
                        request,
                        "studentName"
                );


        // =========================================================
        // PREVENT DUPLICATE
        // =========================================================

        Optional<Submission> existing =
                submissionRepository
                        .findByAssessmentIdAndStudentRegisterNumber(
                                assessmentId,
                                registerNumber
                        );


        if (existing.isPresent()) {

            throw new RuntimeException(
                    "You have already submitted this assessment."
            );
        }


        List<Map<String, Object>> answers =
                objectMapper.convertValue(
                        request.get("answers"),
                        new TypeReference<List<Map<String, Object>>>() {}
                );


        if (answers == null) {
            answers = new ArrayList<>();
        }


        List<Question> questions =
                questionRepository
                        .findByAssessmentId(assessmentId);


        int score = 0;


        // =========================================================
        // CALCULATE SCORE
        // =========================================================

        for (Question question : questions) {

            String selectedAnswer = null;


            for (Map<String, Object> answer : answers) {

                Object questionIdObject =
                        answer.get("questionId");


                if (questionIdObject == null) {
                    continue;
                }


                String questionId =
                        String.valueOf(
                                questionIdObject
                        );


                if (String.valueOf(
                        question.getId()
                ).equals(questionId)) {

                    selectedAnswer =
                            getString(
                                    answer,
                                    "answer"
                            );

                    break;
                }
            }


            if (selectedAnswer != null
                    && question.getCorrectAnswer() != null
                    && question.getCorrectAnswer()
                    .trim()
                    .equalsIgnoreCase(
                            selectedAnswer.trim()
                    )) {

                score +=
                        question.getMarks() != null
                                ? question.getMarks()
                                : 1;
            }
        }


        Assessment assessment =
                assessmentRepository.findById(assessmentId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Assessment not found."
                                )
                        );


        int totalMarks =
                assessment.getTotalMarks();


        double percentage = 0;


        if (totalMarks > 0) {

            percentage =
                    ((double) score / totalMarks)
                            * 100;
        }


        // =========================================================
        // SAVE SUBMISSION
        // =========================================================

        Submission submission =
                new Submission();


        submission.setAssessmentId(
                assessmentId
        );

        submission.setStudentRegisterNumber(
                registerNumber
        );

        submission.setStudentName(
                studentName
        );

        submission.setScore(score);

        submission.setTotalMarks(
                totalMarks
        );

        submission.setPercentage(
                Math.round(percentage * 100.0) / 100.0
        );

        submission.setSubmittedAt(
                LocalDateTime.now().toString()
        );

        submission.setScorePublished(false);


        submissionRepository.save(
                submission
        );


        // =========================================================
        // RESPONSE
        // =========================================================

        Map<String, Object> response =
                new LinkedHashMap<>();


        response.put(
                "message",
                "Assessment submitted successfully."
        );

        response.put(
                "scorePublished",
                false
        );


        return response;
    }


    // =========================================================
    // STUDENT RESULTS
    // =========================================================

    public List<Map<String, Object>>
    getStudentResults(
            String registerNumber
    ) {

        List<Submission> submissions =
                submissionRepository
                        .findByStudentRegisterNumberOrderByIdDesc(
                                registerNumber
                        );


        List<Map<String, Object>> results =
                new ArrayList<>();


        for (Submission submission : submissions) {

            Map<String, Object> result =
                    new LinkedHashMap<>();


            result.put(
                    "id",
                    submission.getId()
            );

            result.put(
                    "assessmentId",
                    submission.getAssessmentId()
            );

            result.put(
                    "studentRegisterNumber",
                    submission.getStudentRegisterNumber()
            );

            result.put(
                    "studentName",
                    submission.getStudentName()
            );

            result.put(
                    "submittedAt",
                    submission.getSubmittedAt()
            );


            Boolean published =
                    submission.getScorePublished();


            if (published == null) {
                published = false;
            }


            result.put(
                    "scorePublished",
                    published
            );


            if (published) {

                result.put(
                        "score",
                        submission.getScore()
                );

                result.put(
                        "totalMarks",
                        submission.getTotalMarks()
                );

                result.put(
                        "percentage",
                        submission.getPercentage()
                );

            } else {

                result.put(
                        "score",
                        null
                );

                result.put(
                        "totalMarks",
                        null
                );

                result.put(
                        "percentage",
                        null
                );
            }


            Optional<Assessment> assessment =
                    assessmentRepository
                            .findById(
                                    submission.getAssessmentId()
                            );


            if (assessment.isPresent()) {

                result.put(
                        "assessmentTitle",
                        assessment.get().getTitle()
                );

                result.put(
                        "jobTitle",
                        assessment.get().getJobTitle()
                );

                result.put(
                        "category",
                        assessment.get().getCategory()
                );

                result.put(
                        "companyName",
                        assessment.get().getCompanyName()
                );
            }


            results.add(result);
        }


        return results;
    }


    // =========================================================
    // EMPLOYER ASSESSMENTS
    // =========================================================

    public List<Map<String, Object>>
    getEmployerAssessments(
            String employerId
    ) {

        List<Assessment> assessments =
                assessmentRepository
                        .findByEmployerId(employerId);


        List<Map<String, Object>> result =
                new ArrayList<>();


        for (Assessment assessment : assessments) {

            Map<String, Object> map =
                    new LinkedHashMap<>();

            map.put("id", assessment.getId());
            map.put("title", assessment.getTitle());
            map.put("description", assessment.getDescription());
            map.put("category", assessment.getCategory());
            map.put("jobTitle", assessment.getJobTitle());
            map.put("duration", assessment.getDuration());
            map.put("totalMarks", assessment.getTotalMarks());
            map.put("questionCount", assessment.getQuestionCount());
            map.put("employerId", assessment.getEmployerId());
            map.put("companyName", assessment.getCompanyName());
            map.put("status", assessment.getStatus());
            map.put("createdAt", assessment.getCreatedAt());

            result.add(map);
        }


        return result;
    }


    // =========================================================
    // EMPLOYER: VIEW SUBMISSIONS
    // =========================================================

    public List<Map<String, Object>>
    getAssessmentSubmissions(
            Long assessmentId
    ) {

        assessmentRepository.findById(assessmentId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Assessment not found."
                        )
                );


        List<Submission> submissions =
                submissionRepository
                        .findByAssessmentIdOrderByIdDesc(
                                assessmentId
                        );


        List<Map<String, Object>> result =
                new ArrayList<>();


        for (Submission submission : submissions) {

            Map<String, Object> map =
                    new LinkedHashMap<>();


            map.put(
                    "id",
                    submission.getId()
            );

            map.put(
                    "assessmentId",
                    submission.getAssessmentId()
            );

            map.put(
                    "studentRegisterNumber",
                    submission.getStudentRegisterNumber()
            );

            map.put(
                    "studentName",
                    submission.getStudentName()
            );

            map.put(
                    "score",
                    submission.getScore()
            );

            map.put(
                    "totalMarks",
                    submission.getTotalMarks()
            );

            map.put(
                    "percentage",
                    submission.getPercentage()
            );

            map.put(
                    "submittedAt",
                    submission.getSubmittedAt()
            );

            map.put(
                    "scorePublished",
                    submission.getScorePublished()
            );


            result.add(map);
        }


        return result;
    }


    // =========================================================
    // EMPLOYER: PUBLISH SCORES
    // =========================================================

    public Map<String, Object>
    publishScores(
            Long assessmentId
    ) {

        Assessment assessment =
                assessmentRepository.findById(assessmentId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Assessment not found."
                                )
                        );


        List<Submission> submissions =
                submissionRepository
                        .findByAssessmentIdOrderByIdDesc(
                                assessmentId
                        );


        if (submissions.isEmpty()) {

            throw new RuntimeException(
                    "No student submissions found for this assessment."
            );
        }


        int publishedCount = 0;


        for (Submission submission : submissions) {

            if (!Boolean.TRUE.equals(
                    submission.getScorePublished()
            )) {

                submission.setScorePublished(
                        true
                );

                submissionRepository.save(
                        submission
                );

                publishedCount++;
            }
        }


        Map<String, Object> response =
                new LinkedHashMap<>();


        response.put(
                "message",
                "Scores published successfully."
        );

        response.put(
                "assessmentId",
                assessmentId
        );

        response.put(
                "assessmentTitle",
                assessment.getTitle()
        );

        response.put(
                "publishedCount",
                publishedCount
        );


        return response;
    }


    // =========================================================
    // HELPER METHODS
    // =========================================================

    private String getString(
            Map<String, Object> map,
            String key
    ) {

        if (map == null) {
            return null;
        }


        Object value =
                map.get(key);


        if (value == null) {
            return null;
        }


        return String.valueOf(value);
    }


    private Integer getInteger(
            Map<String, Object> map,
            String key
    ) {

        if (map == null) {
            return null;
        }


        Object value =
                map.get(key);


        if (value == null) {
            return null;
        }


        if (value instanceof Number) {

            return ((Number) value).intValue();
        }


        try {

            return Integer.parseInt(
                    String.valueOf(value)
            );

        } catch (Exception e) {

            return null;
        }
    }


    private String firstNonNull(
            String... values
    ) {

        if (values == null) {
            return null;
        }


        for (String value : values) {

            if (value != null &&
                    !value.trim().isEmpty()) {

                return value;
            }
        }


        return null;
    }


    private String cleanString(String value) {

        if (value == null) {
            return null;
        }


        String cleaned = value.trim();


        return cleaned.isEmpty()
                ? null
                : cleaned;
    }
}