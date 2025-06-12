package com.ktds.hi.recommend.biz.usecase.in;

import com.ktds.hi.common.dto.PageResponse;
import com.ktds.hi.recommend.infra.dto.request.RecommendStoreRequest;
import com.ktds.hi.recommend.infra.dto.response.RecommendStoreResponse;
import com.ktds.hi.recommend.infra.dto.response.StoreDetailResponse;

import java.util.List;

import org.springframework.data.domain.Pageable;

/**
 * 매장 추천 유스케이스 인터페이스
 * 사용자 취향 기반 매장 추천 기능을 정의
 */
public interface StoreRecommendUseCase {

    /**
     * 개인화 매장 추천 (Controller에서 호출하는 메서드명으로 수정)
     */
    List<RecommendStoreResponse> recommendPersonalizedStores(Long memberId, RecommendStoreRequest request);

    /**
     * 위치 기반 매장 추천 (PageResponse 반환으로 수정)
     */
    PageResponse<RecommendStoreResponse> recommendStoresByLocation(Double latitude, Double longitude, Integer radius,
        String category, Pageable pageable);

    /**
     * 인기 매장 추천
     */
    List<RecommendStoreResponse> recommendPopularStores(String category, Integer limit);

    /**
     * 추천 매장 상세 조회 (추가)
     */
    StoreDetailResponse getRecommendedStoreDetail(Long storeId, Long memberId);

    /**
     * 추천 클릭 로깅 (추가)
     */
    void logRecommendClick(Long memberId, Long storeId);
}
