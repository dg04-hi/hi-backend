package com.ktds.hi.store.infra.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 매장 검색 응답 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "매장 검색 응답")
public class StoreSearchResponse {

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

    @Schema(description = "거리(km)", example = "1.2")
    private Double distance;
}