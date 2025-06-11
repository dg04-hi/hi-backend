package com.ktds.hi.recommend.biz.usecase.in;

import com.ktds.hi.recommend.infra.dto.request.RecommendStoreRequest;
import com.ktds.hi.recommend.infra.dto.response.RecommendStoreResponse;

import java.util.List;

/**
 * 매장 추천 유스케이스 인터페이스
 * 사용자 취향 기반 매장 추천 기능을 정의
 */
public interface StoreRecommendUseCase {
    
    /**
     * 사용자 취향 기반 매장 추천
     */
    List<RecommendStoreResponse> recommendStores(Long memberId, RecommendStoreRequest request);
    
    /**
     * 위치 기반 매장 추천
     */
    List<RecommendStoreResponse> recommendStoresByLocation(Double latitude, Double longitude, Integer radius);
    
    /**
     * 인기 매장 추천
     */
    List<RecommendStoreResponse> recommendPopularStores(String category, Integer limit);
}
