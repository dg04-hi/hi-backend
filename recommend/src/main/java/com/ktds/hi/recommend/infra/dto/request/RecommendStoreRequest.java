package com.ktds.hi.recommend.infra.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "매장 추천 요청")
public class RecommendStoreRequest {

    @NotNull
    @Schema(description = "위도", example = "37.5665", required = true)
    private Double latitude;

    @NotNull
    @Schema(description = "경도", example = "126.9780", required = true)
    private Double longitude;

    @Schema(description = "반경(미터)", example = "3000", defaultValue = "3000")
    private Integer radius = 3000;

    @Schema(description = "선택된 태그들 (콤마 구분)", example = "매운맛,혼밥")
    private String tags;

    @Schema(description = "카테고리", example = "한식")
    private String category;
}