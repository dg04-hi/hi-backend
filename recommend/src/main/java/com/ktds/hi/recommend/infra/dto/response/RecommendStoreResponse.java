package com.ktds.hi.recommend.infra.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * 추천 매장 응답 DTO
 */
@Getter
@Builder
@Schema(description = "추천 매장 응답")
public class RecommendStoreResponse {

    @Schema(description = "매장 ID", example = "1")
    private Long storeId;

    @Schema(description = "매장명", example = "맛있는 김치찌개")
    private String storeName;

    @Schema(description = "카테고리", example = "한식")
    private String category;

    @Schema(description = "주소", example = "서울시 강남구 테헤란로 123")
    private String address;

    @Schema(description = "위도", example = "37.5665")
    private Double latitude;

    @Schema(description = "경도", example = "126.9780")
    private Double longitude;

    @Schema(description = "평균 평점", example = "4.5")
    private Double averageRating;

    @Schema(description = "리뷰 수", example = "127")
    private Integer reviewCount;

    @Schema(description = "추천 점수", example = "0.85")
    private Double recommendScore;

    @Schema(description = "추천 이유", example = "좋아하는 '매운맛' 태그에 맞는 매장입니다")
    private String recommendReason;

    @Schema(description = "거리 (미터)", example = "1200")
    private Double distance;

    @Schema(description = "매장 이미지 URL 목록")
    private List<String> imageUrls;

    @Schema(description = "태그 목록", example = "[\"매운맛\", \"혼밥\"]")
    private List<String> tags;

    @Schema(description = "가게 점수")
    private Double rating;
}
