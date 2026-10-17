package com.skillmapping.service;

import com.skillmapping.model.Course;
import com.skillmapping.repository.CourseRepository;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class CourseService {

    private final CourseRepository courseRepository;


    public CourseService(
            CourseRepository courseRepository) {

        this.courseRepository =
                courseRepository;
    }


    // =========================================================
    // CREATE COURSE
    // =========================================================

    public Course createCourse(
            Map<String, Object> request) {


        String courseName =
                getString(
                        request,
                        "courseName"
                );


        String category =
                getString(
                        request,
                        "category"
                );


        String description =
                getString(
                        request,
                        "description"
                );


        String skills =
                getString(
                        request,
                        "skills"
                );


        String level =
                getString(
                        request,
                        "level"
                );


        String duration =
                getString(
                        request,
                        "duration"
                );


        String prerequisites =
                getString(
                        request,
                        "prerequisites"
                );


        String learningOutcomes =
                getString(
                        request,
                        "learningOutcomes"
                );


        String keywords =
                getString(
                        request,
                        "keywords"
                );


        String employerId =
                getString(
                        request,
                        "employerId"
                );


        String companyName =
                getString(
                        request,
                        "companyName"
                );


        // =====================================================
        // VALIDATION
        // =====================================================

        if (courseName.isBlank()) {

            throw new RuntimeException(
                    "Course name is required"
            );
        }


        if (category.isBlank()) {

            throw new RuntimeException(
                    "Course category is required"
            );
        }


        if (skills.isBlank()) {

            throw new RuntimeException(
                    "At least one skill is required"
            );
        }


        if (employerId.isBlank()) {

            throw new RuntimeException(
                    "Employer ID is required"
            );
        }


        if (companyName.isBlank()) {

            throw new RuntimeException(
                    "Company name is required"
            );
        }


        // =====================================================
        // CREATE COURSE
        // =====================================================

        Course course =
                new Course();


        course.setCourseName(
                courseName
        );


        course.setCategory(
                category
        );


        course.setDescription(
                description
        );


        course.setSkills(
                skills
        );


        course.setLevel(
                level
        );


        course.setDuration(
                duration
        );


        course.setPrerequisites(
                prerequisites
        );


        course.setLearningOutcomes(
                learningOutcomes
        );


        course.setKeywords(
                keywords
        );


        course.setEmployerId(
                employerId
        );


        course.setCompanyName(
                companyName
        );


        course.setStatus(
                "Published"
        );


        course.setCreatedAt(
                LocalDateTime
                        .now()
                        .toString()
        );


        return courseRepository.save(
                course
        );
    }


    // =========================================================
    // GET PUBLISHED COURSES
    // =========================================================

    public List<Course>
    getPublishedCourses() {

        return courseRepository
                .findByStatusOrderByIdDesc(
                        "Published"
                );
    }


    // =========================================================
    // GET EMPLOYER COURSES
    // =========================================================

    public List<Course>
    getEmployerCourses(
            String employerId) {

        return courseRepository
                .findByEmployerIdOrderByIdDesc(
                        employerId
                );
    }


    // =========================================================
    // GET SINGLE COURSE
    // =========================================================

    public Course getCourse(
            Long id) {

        return courseRepository
                .findById(id)
                .orElseThrow(
                        () -> new RuntimeException(
                                "Course not found"
                        )
                );
    }


    // =========================================================
    // HELPER
    // =========================================================

    private String getString(
            Map<String, Object> map,
            String key) {

        Object value =
                map.get(key);


        if (value == null) {

            return "";
        }


        return String.valueOf(value)
                .trim();
    }
}