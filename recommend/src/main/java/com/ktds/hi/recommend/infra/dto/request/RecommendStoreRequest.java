package com.ktds.hi.recommend.infra.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 매장 추천 요청 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "매장 추천 요청")
public class RecommendStoreRequest {
    
    @NotNull(message = "위도는 필수입니다")
    @Schema(description = "위도", example = "37.5665")
    private Double latitude;
    
    @NotNull(message = "경도는 필수입니다")
    @Schema(description = "경도", example = "126.9780")
    private Double longitude;
    
    @Schema(description = "검색 반경(미터)", example = "5000", defaultValue = "5000")
    private Integer radius = 5000;
    
    @Schema(description = "선호 카테고리", example = "[\"한식\", \"일식\"]")
    private List<String> preferredCategories;
    
    @Schema(description = "가격 범위", example = "MEDIUM")
    private String priceRange;
    
    @Schema(description = "추천 개수", example = "10", defaultValue = "10")
    private Integer limit = 10;
    
    /**
     * 유효성 검증
     */
    public void validate() {
        if (latitude == null || longitude == null) {
            throw new IllegalArgumentException("위도와 경도는 필수입니다");
        }
        if (latitude < -90 || latitude > 90) {
            throw new IllegalArgumentException("위도는 -90과 90 사이여야 합니다");
        }
        if (longitude < -180 || longitude > 180) {
            throw new IllegalArgumentException("경도는 -180과 180 사이여야 합니다");
        }
        if (radius != null && radius <= 0) {
            throw new IllegalArgumentException("검색 반경은 0보다 커야 합니다");
        }
    }
}
