package com.skillmapping.repository;

import com.skillmapping.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CourseRepository
        extends JpaRepository<Course, Long> {

    List<Course> findByStatusOrderByIdDesc(String status);

    List<Course> findByEmployerIdOrderByIdDesc(String employerId);
}