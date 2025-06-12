package com.ktds.hi.recommend.infra.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * 매장 추천 요청 DTO
 */
@Getter
@Setter
@NoArgsConstructor
@Schema(description = "매장 추천 요청")
public class RecommendStoreRequest {

    @NotNull(message = "위도는 필수입니다")
    @DecimalMin(value = "-90.0", message = "위도는 -90도 이상이어야 합니다")
    @DecimalMax(value = "90.0", message = "위도는 90도 이하여야 합니다")
    @Schema(description = "위도", example = "37.5665")
    private Double latitude;

    @NotNull(message = "경도는 필수입니다")
    @DecimalMin(value = "-180.0", message = "경도는 -180도 이상이어야 합니다")
    @DecimalMax(value = "180.0", message = "경도는 180도 이하여야 합니다")
    @Schema(description = "경도", example = "126.9780")
    private Double longitude;

    @Min(value = 100, message = "반경은 최소 100m 이상이어야 합니다")
    @Max(value = 50000, message = "반경은 최대 50km 이하여야 합니다")
    @Schema(description = "검색 반경 (미터)", example = "5000")
    private Integer radius = 5000;

    @Schema(description = "카테고리 필터", example = "한식")
    private String category;

    @Schema(description = "취향 태그 목록", example = "[\"매운맛\", \"혼밥\"]")
    private List<String> tags;

    @Min(value = 1, message = "최소 1개 이상의 결과가 필요합니다")
    @Max(value = 50, message = "최대 50개까지 조회 가능합니다")
    @Schema(description = "결과 개수", example = "10")
    private Integer limit = 10;
}