package com.ktds.hi.recommend.infra.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * 매장 상세 정보 응답 DTO
 */
@Getter
@Builder
@Schema(description = "매장 상세 정보 응답")
public class StoreDetailResponse {

    @Schema(description = "매장 ID", example = "1")
    private Long storeId;

    @Schema(description = "매장명", example = "맛있는 김치찌개")
    private String storeName;

    @Schema(description = "카테고리", example = "한식")
    private String category;

    @Schema(description = "주소", example = "서울시 강남구 테헤란로 123")
    private String address;

    @Schema(description = "전화번호", example = "02-1234-5678")
    private String phoneNumber;

    @Schema(description = "위도", example = "37.5665")
    private Double latitude;

    @Schema(description = "경도", example = "126.9780")
    private Double longitude;

    @Schema(description = "평균 평점", example = "4.5")
    private Double averageRating;

    @Schema(description = "리뷰 수", example = "127")
    private Integer reviewCount;

    @Schema(description = "매장 이미지 URL 목록")
    private List<String> imageUrls;

    @Schema(description = "운영시간", example = "10:00-22:00")
    private String operatingHours;

    @Schema(description = "AI 요약", example = "친절한 서비스와 맛있는 김치찌개로 유명한 곳입니다")
    private String aiSummary;

    @Schema(description = "긍정 키워드", example = "[\"맛있다\", \"친절하다\", \"깔끔하다\"]")
    private List<String> topPositiveKeywords;

    @Schema(description = "부정 키워드", example = "[\"시끄럽다\", \"대기시간\"]")
    private List<String> topNegativeKeywords;
}