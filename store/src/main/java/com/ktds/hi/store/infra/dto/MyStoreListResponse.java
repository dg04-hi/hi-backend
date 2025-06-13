package com.ktds.hi.store.infra.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 내 매장 목록 응답 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "내 매장 목록 응답")
public class MyStoreListResponse {

    @Schema(description = "매장 ID", example = "1")
    private Long storeId;

    @Schema(description = "매장명", example = "맛집 한번 가볼래?")
    private String storeName;

    @Schema(description = "주소", example = "서울시 강남구 테헤란로 123")
    private String address;

    @Schema(description = "카테고리", example = "한식")
    private String category;

    @Schema(description = "평점", example = "4.5")
    private Double rating;

    @Schema(description = "리뷰 수", example = "127")
    private Integer reviewCount;

    @Schema(description = "운영 상태", example = "운영중")
    private String status;

    @Schema(description = "운영시간", example = "월-금 09:00-21:00")
    private String operatingHours;
}