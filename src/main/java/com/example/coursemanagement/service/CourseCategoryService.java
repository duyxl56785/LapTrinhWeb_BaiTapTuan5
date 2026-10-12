package com.example.coursemanagement.service;

import com.example.coursemanagement.entity.CourseCategory;
import com.example.coursemanagement.exception.BusinessRuleException;
import com.example.coursemanagement.exception.ResourceNotFoundException;
import com.example.coursemanagement.repository.CourseCategoryRepository;
import com.example.coursemanagement.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseCategoryService {

    private final CourseCategoryRepository categoryRepository;
    private final CourseRepository courseRepository;

    public List<CourseCategory> findAll() {
        return categoryRepository.findAll();
    }

    public CourseCategory findById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("CourseCategoryId không tồn tại: " + id));
    }

    @Transactional
    public CourseCategory save(CourseCategory category) {
        // Kiểm tra trùng tên loại khóa học
        if (category.getId() == null) {
            if (categoryRepository.existsByNameIgnoreCase(category.getName().trim())) {
                throw new BusinessRuleException("Loại khóa học '" + category.getName() + "' đã tồn tại!");
            }
        } else {
            if (categoryRepository.existsByNameIgnoreCaseAndIdNot(category.getName().trim(), category.getId())) {
                throw new BusinessRuleException("Loại khóa học '" + category.getName() + "' đã tồn tại!");
            }
        }
        return categoryRepository.save(category);
    }

    /**
     * Chức năng 5 - Ràng buộc 1:
     * Không cho phép xóa một CourseCategory nếu vẫn còn khóa học thuộc loại đó.
     */
    @Transactional
    public void delete(Long id) {
        CourseCategory category = findById(id);

        if (courseRepository.existsByCategoryId(id)) {
            throw new BusinessRuleException(
                    "Không được phép xóa loại khóa học '" + category.getName() +
                            "' vì hiện tại vẫn còn các khóa học đang thuộc loại này!");
        }

        categoryRepository.delete(category);
    }
}
