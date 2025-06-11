package com.ktds.hi.recommend.biz.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 추천 매장 도메인 클래스
 * 추천된 매장 정보를 담는 도메인 객체
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecommendStore {
    
    private Long storeId;
    private String storeName;
    private String address;
    private String category;
    private List<String> tags;
    private Double rating;
    private Integer reviewCount;
    private Double distance;
    private Double recommendScore;
    private RecommendType recommendType;
    private String recommendReason;
    
    /**
     * 추천 점수 업데이트
     */
    public RecommendStore updateRecommendScore(Double newScore) {
        return RecommendStore.builder()
                .storeId(this.storeId)
                .storeName(this.storeName)
                .address(this.address)
                .category(this.category)
                .tags(this.tags)
                .rating(this.rating)
                .reviewCount(this.reviewCount)
                .distance(this.distance)
                .recommendScore(newScore)
                .recommendType(this.recommendType)
                .recommendReason(this.recommendReason)
                .build();
    }
    
    /**
     * 추천 이유 업데이트
     */
    public RecommendStore updateRecommendReason(String newReason) {
        return RecommendStore.builder()
                .storeId(this.storeId)
                .storeName(this.storeName)
                .address(this.address)
                .category(this.category)
                .tags(this.tags)
                .rating(this.rating)
                .reviewCount(this.reviewCount)
                .distance(this.distance)
                .recommendScore(this.recommendScore)
                .recommendType(this.recommendType)
                .recommendReason(newReason)
                .build();
    }
}
