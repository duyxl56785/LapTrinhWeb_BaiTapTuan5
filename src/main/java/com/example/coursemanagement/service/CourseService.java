package com.example.coursemanagement.service;

import com.example.coursemanagement.dto.CourseFilterDto;
import com.example.coursemanagement.dto.CourseFormDto;
import com.example.coursemanagement.entity.Course;
import com.example.coursemanagement.entity.CourseCategory;
import com.example.coursemanagement.entity.CourseImage;
import com.example.coursemanagement.exception.BusinessRuleException;
import com.example.coursemanagement.exception.ResourceNotFoundException;
import com.example.coursemanagement.repository.CourseCategoryRepository;
import com.example.coursemanagement.repository.CourseImageRepository;
import com.example.coursemanagement.repository.CourseRepository;
import com.example.coursemanagement.repository.CourseSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;
    private final CourseCategoryRepository categoryRepository;
    private final CourseImageRepository imageRepository;

    /**
     * Chức năng 2: Danh sách và tra cứu
     * Lọc trực tiếp bằng JPA Specification (DB level, không dùng vòng lặp code Java).
     * Bắt lỗi nếu Học phí tối thiểu > Học phí tối đa.
     */
    public List<Course> findAll(CourseFilterDto filter) {
        if (filter != null && filter.getMinFee() != null && filter.getMaxFee() != null) {
            if (filter.getMinFee() > filter.getMaxFee()) {
                throw new BusinessRuleException("Học phí tối thiểu không được lớn hơn học phí tối đa!");
            }
        }

        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        if (filter != null && filter.getSortBy() != null) {
            switch (filter.getSortBy()) {
                case "fee_asc":
                    sort = Sort.by(Sort.Direction.ASC, "tuitionFee");
                    break;
                case "fee_desc":
                    sort = Sort.by(Sort.Direction.DESC, "tuitionFee");
                    break;
                case "duration_asc":
                    sort = Sort.by(Sort.Direction.ASC, "duration");
                    break;
            }
        }

        return courseRepository.findAll(CourseSpecification.filter(filter), sort);
    }

    public Course findById(Long id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("CourseId không tồn tại: " + id));
    }

    public CourseFormDto toFormDto(Course course) {
        return CourseFormDto.builder()
                .id(course.getId())
                .name(course.getName())
                .tuitionFee(course.getTuitionFee())
                .duration(course.getDuration())
                .isActive(course.getIsActive())
                .description(course.getDescription())
                .categoryId(course.getCategory() != null ? course.getCategory().getId() : null)
                .build();
    }

    /**
     * Create / Update khóa học
     * Kiểm tra CourseCategoryId tồn tại
     * Kiểm tra Tên khóa học không được trùng trong cùng một loại khóa học
     */
    @Transactional
    public Course save(CourseFormDto formDto) {
        CourseCategory category = categoryRepository.findById(formDto.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("CourseCategoryId không tồn tại: " + formDto.getCategoryId()));

        // Kiểm tra validation tên trùng lặp trong cùng một category
        if (formDto.getId() == null) {
            if (courseRepository.existsByNameIgnoreCaseAndCategoryId(formDto.getName().trim(), formDto.getCategoryId())) {
                throw new BusinessRuleException("Tên khóa học '" + formDto.getName() + "' đã tồn tại trong loại khóa học này!");
            }
        } else {
            if (courseRepository.existsByNameIgnoreCaseAndCategoryIdAndIdNot(formDto.getName().trim(), formDto.getCategoryId(), formDto.getId())) {
                throw new BusinessRuleException("Tên khóa học '" + formDto.getName() + "' đã tồn tại trong loại khóa học này!");
            }
        }

        Course course;
        if (formDto.getId() != null) {
            course = findById(formDto.getId());
        } else {
            course = new Course();
        }

        course.setName(formDto.getName().trim());
        course.setTuitionFee(formDto.getTuitionFee());
        course.setDuration(formDto.getDuration());
        course.setIsActive(formDto.getIsActive() != null ? formDto.getIsActive() : true);
        course.setDescription(formDto.getDescription());
        course.setCategory(category);

        return courseRepository.save(course);
    }

    @Transactional
    public void delete(Long id) {
        Course course = findById(id);
        courseRepository.delete(course);
    }

    /**
     * Chức năng 4: Quản lý ảnh khóa học - Thêm ảnh bằng URL
     */
    @Transactional
    public CourseImage addImage(Long courseId, String imageUrl, Boolean isThumbnail) {
        Course course = findById(courseId);

        boolean makeThumbnail = Boolean.TRUE.equals(isThumbnail);

        // Nếu khóa học chưa có ảnh nào thì tự động đặt ảnh đầu tiên làm thumbnail
        if (course.getImages().isEmpty()) {
            makeThumbnail = true;
        }

        // Nếu ảnh này làm thumbnail thì các ảnh cũ phải chuyển thành ảnh thường
        if (makeThumbnail) {
            for (CourseImage img : course.getImages()) {
                img.setIsThumbnail(false);
                imageRepository.save(img);
            }
        }

        CourseImage newImage = CourseImage.builder()
                .imageUrl(imageUrl.trim())
                .isThumbnail(makeThumbnail)
                .course(course)
                .build();

        return imageRepository.save(newImage);
    }

    /**
     * Chức năng 4: Chọn một ảnh làm ảnh đại diện
     * Yêu cầu: Mỗi khóa học chỉ được có tối đa một ảnh đại diện.
     * Khi chọn ảnh mới làm ảnh đại diện, ảnh đại diện cũ phải tự động chuyển thành ảnh thường.
     */
    @Transactional
    public void setThumbnail(Long courseId, Long imageId) {
        Course course = findById(courseId);

        CourseImage targetImage = imageRepository.findByIdAndCourseId(imageId, courseId)
                .orElseThrow(() -> new ResourceNotFoundException("CourseImageId không tồn tại hoặc không thuộc khóa học này: " + imageId));

        for (CourseImage img : course.getImages()) {
            if (img.getId().equals(targetImage.getId())) {
                img.setIsThumbnail(true);
            } else {
                img.setIsThumbnail(false);
            }
            imageRepository.save(img);
        }
    }

    /**
     * Xóa ảnh khỏi khóa học
     */
    @Transactional
    public void deleteImage(Long courseId, Long imageId) {
        Course course = findById(courseId);
        CourseImage targetImage = imageRepository.findByIdAndCourseId(imageId, courseId)
                .orElseThrow(() -> new ResourceNotFoundException("CourseImageId không tồn tại hoặc không thuộc khóa học này: " + imageId));

        boolean wasThumbnail = Boolean.TRUE.equals(targetImage.getIsThumbnail());
        course.getImages().remove(targetImage);
        imageRepository.delete(targetImage);

        // Nếu xóa đúng ảnh đại diện mà vẫn còn ảnh khác, đặt ảnh đầu tiên còn lại làm đại diện
        if (wasThumbnail && !course.getImages().isEmpty()) {
            CourseImage firstRemaining = course.getImages().get(0);
            firstRemaining.setIsThumbnail(true);
            imageRepository.save(firstRemaining);
        }
    }
}
