package com.ktds.hi.recommend.biz.usecase.out;

import com.ktds.hi.recommend.biz.domain.RecommendHistory;
import com.ktds.hi.recommend.biz.domain.TasteProfile;

import java.util.List;
import java.util.Optional;

/**
 * 추천 리포지토리 인터페이스
 * 추천 관련 데이터 영속성 기능을 정의
 */
public interface RecommendRepository {
    
    /**
     * 추천 히스토리 저장
     */
    RecommendHistory saveRecommendHistory(RecommendHistory history);
    
    /**
     * 회원 ID로 추천 히스토리 조회
     */
    List<RecommendHistory> findRecommendHistoriesByMemberId(Long memberId);
    
    /**
     * 취향 프로필 저장
     */
    TasteProfile saveTasteProfile(TasteProfile profile);
    
    /**
     * 회원 ID로 취향 프로필 조회
     */
    Optional<TasteProfile> findTasteProfileByMemberId(Long memberId);
}
