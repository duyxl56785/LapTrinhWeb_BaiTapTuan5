package com.example.coursemanagement.repository;

import com.example.coursemanagement.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long>, JpaSpecificationExecutor<Course> {

    // Chức năng 5 - Ràng buộc 1: Kiểm tra xem category có khóa học nào không
    boolean existsByCategoryId(Long categoryId);

    // Validation: Tên khóa học không được trùng trong cùng một loại khóa học
    boolean existsByNameIgnoreCaseAndCategoryId(String name, Long categoryId);

    // Validation khi cập nhật: Tránh trùng tên với khóa học khác trong cùng loại
    boolean existsByNameIgnoreCaseAndCategoryIdAndIdNot(String name, Long categoryId, Long id);
}
