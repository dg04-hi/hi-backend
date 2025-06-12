package com.ktds.hi.recommend.biz.usecase.out;

import com.ktds.hi.recommend.biz.domain.RecommendStore;
import com.ktds.hi.recommend.biz.domain.TasteProfile;

import java.util.List;
import java.util.Map;

public interface AiRecommendRepository {

    List<RecommendStore> recommendStoresByAI(Long memberId, Map<String, Object> preferences);
    List<RecommendStore> recommendStoresBySimilarUsers(Long memberId);
    List<RecommendStore> recommendStoresByCollaborativeFiltering(Long memberId);

    // 추가 필요한 메서드들
    List<RecommendStore> filterByPreferences(List<RecommendStore> stores, TasteProfile tasteProfile, String tags);
    String getStoreSummary(Long storeId);
}