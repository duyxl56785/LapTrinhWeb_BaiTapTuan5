package com.example.coursemanagement.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseFormDto {

    private Long id;

    @NotBlank(message = "Tên khóa học không được để trống")
    private String name;

    @NotNull(message = "Học phí không được để trống")
    @Positive(message = "Học phí phải lớn hơn 0")
    private Double tuitionFee;

    @NotNull(message = "Thời lượng không được để trống")
    @Positive(message = "Thời lượng phải lớn hơn 0")
    private Integer duration;

    @Builder.Default
    private Boolean isActive = true;

    private String description;

    @NotNull(message = "Vui lòng chọn loại khóa học")
    private Long categoryId;
}
