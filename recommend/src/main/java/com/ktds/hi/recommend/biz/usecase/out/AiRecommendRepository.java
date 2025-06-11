package com.ktds.hi.recommend.biz.usecase.out;

import com.ktds.hi.recommend.biz.domain.RecommendStore;

import java.util.List;
import java.util.Map;

/**
 * AI 추천 리포지토리 인터페이스
 * AI 기반 추천 기능을 정의
 */
public interface AiRecommendRepository {
    
    /**
     * AI 기반 매장 추천
     */
    List<RecommendStore> recommendStoresByAI(Long memberId, Map<String, Object> preferences);
    
    /**
     * 유사 사용자 기반 추천
     */
    List<RecommendStore> recommendStoresBySimilarUsers(Long memberId);
    
    /**
     * 협업 필터링 추천
     */
    List<RecommendStore> recommendStoresByCollaborativeFiltering(Long memberId);
}
