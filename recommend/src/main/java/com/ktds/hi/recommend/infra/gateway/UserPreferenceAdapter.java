package com.ktds.hi.recommend.infra.gateway;

import com.ktds.hi.recommend.biz.usecase.out.UserPreferenceRepository;
import com.ktds.hi.recommend.biz.domain.TasteProfile;
import com.ktds.hi.recommend.biz.domain.TasteCategory;
import com.ktds.hi.recommend.infra.gateway.repository.TasteProfileJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 사용자 선호도 어댑터 클래스
 * 사용자 취향 데이터 처리 기능을 구현
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class UserPreferenceAdapter implements UserPreferenceRepository {
    
    private final TasteProfileJpaRepository tasteProfileJpaRepository;
    private final RecommendRepositoryAdapter recommendRepositoryAdapter;
    
    @Override
    public Optional<TasteProfile> getMemberPreferences(Long memberId) {
        return recommendRepositoryAdapter.findTasteProfileByMemberId(memberId);
    }
    
    @Override
    public Map<String, Object> analyzePreferencesFromReviews(Long memberId) {
        log.info("리뷰 기반 취향 분석 시작: memberId={}", memberId);
        
        // Mock 구현 - 실제로는 리뷰 서비스 API 호출하여 분석
        return Map.of(
                "preferredCategories", List.of(TasteCategory.KOREAN, TasteCategory.JAPANESE),
                "categoryScores", Map.of(
                        "한식", 85.0,
                        "일식", 78.0,
                        "양식", 65.0
                ),
                "preferredTags", List.of("맛집", "깔끔", "친절"),
                "pricePreference", 60.0, // 0-100 점수
                "distancePreference", 70.0,
                "behaviorPatterns", Map.of(
                        "weekendDining", true,
                        "avgRating", 4.2,
                        "reviewFrequency", "medium"
                )
        );
    }
    
    @Override
    public List<Long> findSimilarTasteMembers(Long memberId) {
        log.info("유사 취향 사용자 조회: memberId={}", memberId);
        
        // Mock 구현 - 실제로는 ML 모델 또는 유사도 계산 알고리즘 사용
        return List.of(123L, 456L, 789L);
    }
    
    @Override
    public TasteProfile updateTasteProfile(Long memberId, Map<String, Object> analysisData) {
        log.info("취향 프로필 업데이트: memberId={}", memberId);
        
        // 기존 프로필 조회 또는 새로 생성
        Optional<TasteProfile> existingProfile = getMemberPreferences(memberId);
        
        TasteProfile.TasteProfileBuilder builder = TasteProfile.builder()
                .memberId(memberId)
                .preferredCategories((List<TasteCategory>) analysisData.get("preferredCategories"))
                .categoryScores((Map<String, Double>) analysisData.get("categoryScores"))
                .preferredTags((List<String>) analysisData.get("preferredTags"))
                .behaviorPatterns((Map<String, Object>) analysisData.get("behaviorPatterns"))
                .pricePreference((Double) analysisData.get("pricePreference"))
                .distancePreference((Double) analysisData.get("distancePreference"))
                .updatedAt(LocalDateTime.now());
        
        if (existingProfile.isPresent()) {
            builder.id(existingProfile.get().getId())
                   .createdAt(existingProfile.get().getCreatedAt());
        } else {
            builder.createdAt(LocalDateTime.now());
        }
        
        TasteProfile updatedProfile = builder.build();
        return recommendRepositoryAdapter.saveTasteProfile(updatedProfile);
    }
}
