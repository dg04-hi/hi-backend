package com.ktds.hi.recommend.biz.usecase.out;

import com.ktds.hi.recommend.biz.domain.RecommendHistory;
import com.ktds.hi.recommend.biz.domain.RecommendStore;
import com.ktds.hi.recommend.biz.domain.TasteProfile;

import java.util.List;
import java.util.Optional;

public interface RecommendRepository {

    // 기존 메서드들
    RecommendHistory saveRecommendHistory(RecommendHistory history);
    List<RecommendHistory> findRecommendHistoriesByMemberId(Long memberId);
    TasteProfile saveTasteProfile(TasteProfile profile);
    Optional<TasteProfile> findTasteProfileByMemberId(Long memberId);

    // 추가로 필요한 메서드들
    Optional<RecommendStore> findStoreById(Long storeId);
    List<RecommendStore> findPopularStores(String category, Integer limit);
    void logStoreClick(Long memberId, Long storeId);
    void logRecommendation(Long memberId, List<Long> storeIds, String context);
}