package com.example.coursemanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CourseFilterDto {

    private String keyword;
    private Long categoryId;
    private String status; // "all", "active" (Đang tuyển sinh), "inactive" (Ngừng tuyển sinh)
    private Double minFee;
    private Double maxFee;
    private String sortBy; // "fee_asc", "fee_desc", "duration_asc"

    public boolean hasFeeFilter() {
        return minFee != null || maxFee != null;
    }
}
