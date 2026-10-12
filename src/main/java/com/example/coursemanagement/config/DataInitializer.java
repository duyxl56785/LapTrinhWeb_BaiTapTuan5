package com.example.coursemanagement.config;

import com.example.coursemanagement.entity.Course;
import com.example.coursemanagement.entity.CourseCategory;
import com.example.coursemanagement.entity.CourseImage;
import com.example.coursemanagement.repository.CourseCategoryRepository;
import com.example.coursemanagement.repository.CourseImageRepository;
import com.example.coursemanagement.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final CourseCategoryRepository categoryRepository;
    private final CourseRepository courseRepository;
    private final CourseImageRepository imageRepository;

    @Override
    public void run(String... args) {
        if (categoryRepository.count() > 0) {
            log.info("Dữ liệu đã tồn tại, bỏ qua bước khởi tạo DataInitializer.");
            return;
        }

        log.info("Bắt đầu khởi tạo dữ liệu mẫu tối thiểu (3 danh mục, 5 khóa học kèm ảnh)...");

        // 1. Tạo 3 loại khóa học (và 1 loại rỗng để demo xóa thành công)
        CourseCategory catWeb = CourseCategory.builder()
                .name("Lập trình Web")
                .description("Các công nghệ phát triển web fullstack hiện đại: Spring Boot, ASP.NET, React, Vue...")
                .build();

        CourseCategory catAI = CourseCategory.builder()
                .name("Khoa học Dữ liệu & AI")
                .description("Phân tích dữ liệu, Học máy (Machine Learning) và Trí tuệ nhân tạo chuyên sâu...")
                .build();

        CourseCategory catMobile = CourseCategory.builder()
                .name("Lập trình Di động")
                .description("Lập trình ứng dụng di động đa nền tảng Flutter, iOS Swift, Android Kotlin...")
                .build();

        CourseCategory catEmpty = CourseCategory.builder()
                .name("Kiểm thử Phần mềm (QA/QC)")
                .description("Danh mục demo kiểm thử (không chứa khóa học để test xóa thành công)...")
                .build();

        categoryRepository.saveAll(Arrays.asList(catWeb, catAI, catMobile, catEmpty));

        // 2. Tạo 5 khóa học với các đặc tính để demo
        // Khóa học 1: Lập trình Web - Ngắn hạn (24h <= 30h) - Đang tuyển sinh (true)
        Course c1 = Course.builder()
                .name("ASP.NET Core Web API")
                .tuitionFee(4500000.0)
                .duration(24) // Ngắn hạn
                .isActive(true)
                .description("Xây dựng RESTful API chuyên nghiệp với .NET 8, Entity Framework Core và Docker.")
                .category(catWeb)
                .build();
        courseRepository.save(c1);

        CourseImage img1_1 = CourseImage.builder()
                .imageUrl("https://images.unsplash.com/photo-1517694712202-14dd9538aa97?w=600&auto=format&fit=crop&q=80")
                .isThumbnail(true) // Ảnh đại diện
                .course(c1)
                .build();
        CourseImage img1_2 = CourseImage.builder()
                .imageUrl("https://images.unsplash.com/photo-1555066931-4365d14bab8c?w=600&auto=format&fit=crop&q=80")
                .isThumbnail(false)
                .course(c1)
                .build();
        imageRepository.saveAll(Arrays.asList(img1_1, img1_2));

        // Khóa học 2: Lập trình Web - Dài hạn (45h > 30h) - Đang tuyển sinh (true)
        Course c2 = Course.builder()
                .name("ReactJS & Next.js Toàn Diện")
                .tuitionFee(5200000.0)
                .duration(45) // Dài hạn
                .isActive(true)
                .description("Làm chủ React 19, Next.js App Router, TailwindCSS và kiến trúc Single Page Application.")
                .category(catWeb)
                .build();
        courseRepository.save(c2);

        CourseImage img2_1 = CourseImage.builder()
                .imageUrl("https://images.unsplash.com/photo-1633356122544-f134324a6cee?w=600&auto=format&fit=crop&q=80")
                .isThumbnail(true)
                .course(c2)
                .build();
        CourseImage img2_2 = CourseImage.builder()
                .imageUrl("https://images.unsplash.com/photo-1581291518655-9523c932eccf?w=600&auto=format&fit=crop&q=80")
                .isThumbnail(false)
                .course(c2)
                .build();
        imageRepository.saveAll(Arrays.asList(img2_1, img2_2));

        // Khóa học 3: Khoa học Dữ liệu - Dài hạn (60h > 30h) - Đang tuyển sinh (true)
        Course c3 = Course.builder()
                .name("Python & Machine Learning Cơ Bản")
                .tuitionFee(6800000.0)
                .duration(60) // Dài hạn
                .isActive(true)
                .description("Học máy cơ bản đến nâng cao: Scikit-learn, PyTorch, thị giác máy tính và xử lý ngôn ngữ tự nhiên.")
                .category(catAI)
                .build();
        courseRepository.save(c3);

        CourseImage img3_1 = CourseImage.builder()
                .imageUrl("https://images.unsplash.com/photo-1526374965328-7f61d4dc18c5?w=600&auto=format&fit=crop&q=80")
                .isThumbnail(true)
                .course(c3)
                .build();
        imageRepository.save(img3_1);

        // Khóa học 4: Khoa học Dữ liệu - Ngắn hạn (20h <= 30h) - Ngừng tuyển sinh (false -> demo Ràng buộc 2 & 3)
        Course c4 = Course.builder()
                .name("Xử lý Dữ liệu với Pandas & SQL")
                .tuitionFee(2500000.0)
                .duration(20) // Ngắn hạn
                .isActive(false) // Ràng buộc 2: Hiển thị "Ngừng tuyển sinh"
                .description("Khóa học nền tảng làm sạch dữ liệu, truy vấn phân tích SQL và trực quan hóa Matplotlib.")
                .category(catAI)
                .build();
        courseRepository.save(c4);

        CourseImage img4_1 = CourseImage.builder()
                .imageUrl("https://images.unsplash.com/photo-1551288049-bebda4e38f71?w=600&auto=format&fit=crop&q=80")
                .isThumbnail(true)
                .course(c4)
                .build();
        imageRepository.save(img4_1);

        // Khóa học 5: Lập trình Di động - Dài hạn (36h > 30h) - Đang tuyển sinh (true)
        Course c5 = Course.builder()
                .name("Flutter Cross-Platform Mobile Dev")
                .tuitionFee(5500000.0)
                .duration(36) // Dài hạn
                .isActive(true)
                .description("Phát triển ứng dụng iOS và Android từ một codebase duy nhất với ngôn ngữ Dart.")
                .category(catMobile)
                .build();
        courseRepository.save(c5);

        CourseImage img5_1 = CourseImage.builder()
                .imageUrl("https://images.unsplash.com/photo-1512941937669-90a1b58e7e9c?w=600&auto=format&fit=crop&q=80")
                .isThumbnail(true)
                .course(c5)
                .build();
        CourseImage img5_2 = CourseImage.builder()
                .imageUrl("https://images.unsplash.com/photo-1551650975-87deedd944c3?w=600&auto=format&fit=crop&q=80")
                .isThumbnail(false)
                .course(c5)
                .build();
        imageRepository.saveAll(Arrays.asList(img5_1, img5_2));

        log.info("Khởi tạo dữ liệu mẫu thành công! Sẵn sàng demo ứng dụng.");
    }
}
