package com.ktds.hi.recommend.infra.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 매장 추천 응답 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "매장 추천 응답")
public class RecommendStoreResponse {
    
    @Schema(description = "매장 ID")
    private Long storeId;
    
    @Schema(description = "매장명")
    private String storeName;
    
    @Schema(description = "주소")
    private String address;
    
    @Schema(description = "카테고리")
    private String category;
    
    @Schema(description = "태그 목록")
    private List<String> tags;
    
    @Schema(description = "평점")
    private Double rating;
    
    @Schema(description = "리뷰 수")
    private Integer reviewCount;
    
    @Schema(description = "거리(미터)")
    private Double distance;
    
    @Schema(description = "추천 점수")
    private Double recommendScore;
    
    @Schema(description = "추천 이유")
    private String recommendReason;
}
