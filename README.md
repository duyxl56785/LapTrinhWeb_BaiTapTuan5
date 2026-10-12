# BÀI ÔN LUYỆN GIỮA KỲ: QUẢN LÝ KHÓA HỌC (SPRING BOOT MVC)

Dự án hoàn chỉnh triển khai theo đầy đủ yêu cầu đề bài môn Lập trình Web với:
- **Công nghệ**: Java 21, Spring Boot 3.3.4 (Spring MVC, Spring Data JPA, Thymeleaf, Hibernate Validator, Lombok).
- **Cơ sở dữ liệu**: PostgreSQL (Hỗ trợ chuyển đổi MySQL hoặc H2 chạy ngay lập tức).
- **Giao diện**: Bootstrap 5, Bootstrap Icons, Responsive hiện đại.

---

## 1. Hướng dẫn chạy dự án

### Cách 1: Chạy trực tiếp trên Visual Studio Code (Khuyên dùng)
1. Mở thư mục dự án `BTtuan5` trong Visual Studio Code.
2. VS Code sẽ tự động nhận diện dự án Maven thông qua extension **Java Extension Pack** / **Spring Boot Tools**.
3. Mở file: `src/main/java/com/example/coursemanagement/CourseManagementApplication.java`.
4. Nhấn nút **Run** (hoặc `F5` / `Debug`) xuất hiện ngay phía trên hàm `main`.
5. Mở trình duyệt web và truy cập: [http://localhost:8088](http://localhost:8088) (hoặc [http://localhost:8088/courses](http://localhost:8088/courses)).

---

### Cách 2: Cấu hình Cơ sở dữ liệu trong `application.properties`
Mặc định dự án kết nối đến **PostgreSQL** đang chạy trên máy (cổng 5432):
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/postgres
spring.datasource.username=postgres
spring.datasource.password=postgres
spring.jpa.hibernate.ddl-auto=update
```
> *Lưu ý*: Hãy chỉnh `spring.datasource.password` cho đúng với mật khẩu PostgreSQL trên máy bạn.

**Nếu muốn chạy thử nghiệm ngay lập tức không cần cấu hình DB:**
Mở `application.properties`, comment phần PostgreSQL và bỏ comment phần **H2 Database**:
```properties
spring.datasource.url=jdbc:h2:mem:coursedb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
```

---

## 2. Kịch bản kiểm tra 14 Tiêu chí Demo của Đề bài

Dưới đây là kịch bản chuẩn giúp bạn tự tin đạt điểm tối đa khi giảng viên kiểm tra:

| STT | Nội dung Demo | Cách thực hiện và Kết quả kiểm tra |
|:---:|:---|:---|
| **1** | **Database & 3 bảng** | Mở pgAdmin (hoặc DB tool) xem 3 bảng tự sinh: `courses`, `course_categories`, `course_images`. |
| **2** | **Quan hệ giữa 3 bảng** | Kiểm tra khóa ngoại: `course_category_id` trong `courses` và `course_id` trong `course_images`. |
| **3** | **Danh sách khóa học & tên loại** | Truy cập `/courses`: Mỗi dòng hiển thị đầy đủ tên khóa học và badge tên loại khóa học tương ứng. |
| **4** | **Tìm kiếm theo một phần tên** | Nhập `React` hoặc `API` vào ô "Tên khóa học" rồi bấm Lọc: Kết quả lọc chính xác theo từ khóa. |
| **5** | **Lọc theo loại khóa học** | Chọn loại khóa học (VD: `Lập trình Web`), bấm Lọc: Chỉ hiện các khóa học thuộc loại đó. |
| **6** | **Lọc theo trạng thái** | Chọn "Đang tuyển sinh" (chỉ hiện `IsActive = true`) hoặc "Ngừng tuyển sinh" (chỉ hiện `IsActive = false`). |
| **7** | **Lọc theo khoảng học phí** | Nhập Min: `4000000`, Max: `6000000` -> Hiển thị các khóa học trong tầm giá. Sau khi submit, giá trị Min/Max vẫn giữ nguyên trên form. |
| **8** | **Sắp xếp theo học phí / thời lượng** | Chọn sắp xếp `Học phí tăng dần`, `Học phí giảm dần` hoặc `Thời lượng tăng dần` -> Bảng sắp xếp theo đúng thứ tự. |
| **9** | **Phân loại thời lượng (Ngắn/Dài)** | Cột "Phân loại thời lượng" tự động hiển thị: `Ngắn hạn (≤ 30h)` nếu Duration ≤ 30, hoặc `Dài hạn (> 30h)` nếu Duration > 30. Trường tính toán qua getter `@Transient`, không tạo cột trong DB. |
| **10** | **Create hoặc Edit khóa học** | Bấm "Thêm khóa học mới": thử để trống tên/nhập học phí âm để xem Validation báo lỗi inline. Nhập hợp lệ -> Lưu thành công. Bấm nút sửa để cập nhật. |
| **11** | **Thêm ảnh cho khóa học** | Bấm vào nút xem Chi tiết (biểu tượng con mắt) của một khóa học -> Dán một URL ảnh và bấm "Lưu và thêm ảnh" -> Ảnh xuất hiện ngay trong bộ sưu tập. |
| **12** | **Đổi ảnh đại diện (Thumbnail)** | Tại trang Detail, trên một ảnh thường bấm nút "Đặt đại diện" -> Ảnh đó lập tức trở thành Thumbnail (viền vàng, badge đại diện), ảnh đại diện cũ tự động trở về ảnh thường (luôn duy nhất 1 ảnh đại diện). |
| **13** | **Thử xóa một loại đang có khóa học** | Vào mục "Loại khóa học", thử bấm Xóa loại `Lập trình Web` -> Bị chặn lại ngay và thông báo lỗi đỏ: *"Không được phép xóa loại khóa học... vì hiện tại vẫn còn các khóa học đang thuộc loại này!"* (Ràng buộc nghiệp vụ 1). |
| **14** | **Thử truy cập CourseId không tồn tại** | Nhập thẳng lên URL: `http://localhost:8088/courses/9999` -> Màn hình hiển thị trang lỗi 404 thân thiện, thông báo rõ ràng kèm nút quay lại danh sách, **tuyệt đối không văng lỗi 500 hay WhiteLabel Error Page**! |
