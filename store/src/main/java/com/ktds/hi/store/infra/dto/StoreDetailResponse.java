package com.ktds.hi.store.infra.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

/**
 * 매장 상세 조회 응답 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "매장 상세 조회 응답")
public class StoreDetailResponse {

    @Schema(description = "매장 ID", example = "1")
    private Long storeId;

    @Schema(description = "매장명", example = "맛집 한번 가볼래?")
    private String storeName;

    @Schema(description = "주소", example = "서울시 강남구 테헤란로 123")
    private String address;

    @Schema(description = "위도", example = "37.5665")
    private Double latitude;

    @Schema(description = "경도", example = "126.9780")
    private Double longitude;

    @Schema(description = "매장 설명", example = "맛있는 한식당입니다.")
    private String description;

    @Schema(description = "전화번호", example = "02-1234-5678")
    private String phone;

    @Schema(description = "운영시간", example = "월-금 09:00-21:00")
    private String operatingHours;

    @Schema(description = "카테고리", example = "한식")
    private String category;

    @Schema(description = "평점", example = "4.5")
    private Double rating;

    @Schema(description = "리뷰 수", example = "127")
    private Integer reviewCount;

    @Schema(description = "매장 상태", example = "ACTIVE")
    private String status;

    @Schema(description = "매장 태그 목록")
    private List<String> tags;

    @Schema(description = "메뉴 목록")
    private List<MenuResponse> menus;

    @Schema(description = "AI 요약 정보")
    private String aiSummary;
}