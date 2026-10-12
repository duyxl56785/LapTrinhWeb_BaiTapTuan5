package com.example.coursemanagement.repository;

import com.example.coursemanagement.entity.CourseImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseImageRepository extends JpaRepository<CourseImage, Long> {
    List<CourseImage> findByCourseId(Long courseId);
    Optional<CourseImage> findByIdAndCourseId(Long id, Long courseId);
}
