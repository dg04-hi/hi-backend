package com.ktds.hi.recommend.infra.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

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

    @Schema(description = "거리(미터)", example = "500.0")  // Double로 수정
    private Double distance;

    @Schema(description = "태그 목록", example = "[\"맛집\", \"혼밥\", \"가성비\"]")
    private List<String> tags;

    @Schema(description = "개인화된 추천 이유", example = "당신이 좋아하는 '매운맛' 특성을 가진 매장입니다")
    private String personalizedReason;

    @Schema(description = "AI 요약", example = "고객들이 매운맛과 친절한 서비스를 칭찬하는 매장입니다")
    private String aiSummary;
}