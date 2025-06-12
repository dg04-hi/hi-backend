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

    @Schema(description = "매장명", example = "맛있는 한식당")
    private String storeName;

    @Schema(description = "주소", example = "서울시 강남구 테헤란로 123")
    private String address;

    @Schema(description = "카테고리", example = "한식")
    private String category;

    @Schema(description = "평점", example = "4.5")
    private Double rating;

    @Schema(description = "리뷰 수", example = "256")
    private Integer reviewCount;

    @Schema(description = "거리(미터)", example = "500")
    private Integer distance;

    @Schema(description = "태그 목록", example = "[\"맛집\", \"혼밥\", \"가성비\"]")
    private List<String> tags;

    @Schema(description = "매장 이미지 URL 목록")
    private List<String> images;

    @Schema(description = "매장 설명")
    private String description;

    @Schema(description = "개인화된 추천 이유", example = "당신이 좋아하는 '매운맛' 특성을 가진 매장입니다")
    private String personalizedReason;

    @Schema(description = "AI 요약", example = "고객들이 매운맛과 친절한 서비스를 칭찬하는 매장입니다")
    private String aiSummary;

    @Schema(description = "운영 시간")
    private String operatingHours;

    @Schema(description = "전화번호")
    private String phoneNumber;

    @Schema(description = "메뉴 정보")
    private List<MenuInfo> menuList;

    /**
     * 메뉴 정보 내부 클래스
     */
    @Getter
    @Builder
    @Schema(description = "메뉴 정보")
    public static class MenuInfo {

        @Schema(description = "메뉴명", example = "김치찌개")
        private String menuName;

        @Schema(description = "가격", example = "8000")
        private Integer price;

        @Schema(description = "메뉴 설명", example = "매콤한 김치찌개")
        private String description;

        @Schema(description = "인기 메뉴 여부", example = "true")
        private Boolean isPopular;
    }
}