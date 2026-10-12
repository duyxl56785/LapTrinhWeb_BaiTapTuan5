package com.example.coursemanagement.repository;

import com.example.coursemanagement.dto.CourseFilterDto;
import com.example.coursemanagement.entity.Course;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class CourseSpecification {

    /**
     * Lọc trực tiếp dưới Database bằng JPA Criteria API
     * Không sử dụng vòng lặp trong code Java để lọc theo đúng yêu cầu đề bài.
     */
    public static Specification<Course> filter(CourseFilterDto filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filter == null) {
                return cb.conjunction();
            }

            // 1. Tìm kiếm theo một phần tên khóa học (không phân biệt hoa thường)
            if (filter.getKeyword() != null && !filter.getKeyword().trim().isEmpty()) {
                String pattern = "%" + filter.getKeyword().trim().toLowerCase() + "%";
                predicates.add(cb.like(cb.lower(root.get("name")), pattern));
            }

            // 2. Lọc theo loại khóa học (CourseCategoryId)
            if (filter.getCategoryId() != null) {
                predicates.add(cb.equal(root.get("category").get("id"), filter.getCategoryId()));
            }

            // 3. Lọc theo trạng thái
            // Ràng buộc 3: Chọn "Đang tuyển sinh" chỉ hiển thị isActive = true
            // Chọn "Ngừng tuyển sinh" hiển thị isActive = false
            if ("active".equalsIgnoreCase(filter.getStatus())) {
                predicates.add(cb.isTrue(root.get("isActive")));
            } else if ("inactive".equalsIgnoreCase(filter.getStatus())) {
                predicates.add(cb.isFalse(root.get("isActive")));
            }

            // 4. Lọc theo khoảng học phí
            if (filter.getMinFee() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("tuitionFee"), filter.getMinFee()));
            }
            if (filter.getMaxFee() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("tuitionFee"), filter.getMaxFee()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
