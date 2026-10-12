package com.example.coursemanagement.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
    name = "courses",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_course_category_name", columnNames = {"course_category_id", "name"})
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Tên khóa học không được để trống")
    @Column(nullable = false, length = 200)
    private String name;

    @NotNull(message = "Học phí không được để trống")
    @Positive(message = "Học phí phải lớn hơn 0")
    @Column(nullable = false)
    private Double tuitionFee;

    @NotNull(message = "Thời lượng không được để trống")
    @Positive(message = "Thời lượng phải lớn hơn 0")
    @Column(nullable = false)
    private Integer duration;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @Column(columnDefinition = "TEXT")
    private String description;

    @NotNull(message = "Loại khóa học không được để trống")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "course_category_id", nullable = false)
    private CourseCategory category;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    @ToString.Exclude
    private List<CourseImage> images = new ArrayList<>();

    /**
     * Chức năng 3: Phân loại thời lượng khóa học (Trường tính toán - Transient không tạo cột DB)
     * - Ngắn hạn: Duration <= 30 (giờ)
     * - Dài hạn: Duration > 30 (giờ)
     */
    @Transient
    public String getDurationType() {
        if (duration == null) {
            return "Chưa xác định";
        }
        return duration <= 30 ? "Ngắn hạn" : "Dài hạn";
    }

    /**
     * Lấy URL ảnh đại diện (thumbnail) hoặc trả về placeholder nếu chưa có ảnh
     */
    @Transient
    public String getThumbnailUrl() {
        if (images != null && !images.isEmpty()) {
            for (CourseImage img : images) {
                if (Boolean.TRUE.equals(img.getIsThumbnail())) {
                    return img.getImageUrl();
                }
            }
            // Nếu chưa đánh dấu thumbnail nào, lấy ảnh đầu tiên
            return images.get(0).getImageUrl();
        }
        // Ảnh placeholder mặc định khi khóa học chưa có ảnh
        return "https://images.unsplash.com/photo-1516321318423-f06f85e504b3?w=500&auto=format&fit=crop&q=60";
    }

    /**
     * Kiểm tra khóa học có ít nhất 1 ảnh hay chưa
     */
    @Transient
    public boolean hasImages() {
        return images != null && !images.isEmpty();
    }
}
