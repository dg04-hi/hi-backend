package com.ktds.hi.recommend.biz.usecase.out;

import com.ktds.hi.recommend.biz.domain.TasteProfile;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 사용자 선호도 리포지토리 인터페이스
 * 사용자 취향 데이터 처리 기능을 정의
 */
public interface UserPreferenceRepository {
    
    /**
     * 회원 취향 정보 조회
     */
    Optional<TasteProfile> getMemberPreferences(Long memberId);
    
    /**
     * 회원의 리뷰 기반 취향 분석
     */
    Map<String, Object> analyzePreferencesFromReviews(Long memberId);
    
    /**
     * 유사한 취향의 사용자 조회
     */
    List<Long> findSimilarTasteMembers(Long memberId);
    
    /**
     * 취향 프로필 업데이트
     */
    TasteProfile updateTasteProfile(Long memberId, Map<String, Object> analysisData);
}
