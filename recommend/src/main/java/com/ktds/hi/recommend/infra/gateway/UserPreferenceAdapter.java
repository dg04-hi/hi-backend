package com.ktds.hi.recommend.infra.gateway;

import com.ktds.hi.recommend.biz.usecase.out.UserPreferenceRepository;
import com.ktds.hi.recommend.biz.domain.TasteProfile;
import com.ktds.hi.recommend.biz.domain.TasteCategory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserPreferenceAdapter implements UserPreferenceRepository {

    private final RecommendRepositoryAdapter recommendRepositoryAdapter;

    @Override
    public Optional<TasteProfile> getMemberPreferences(Long memberId) {
        return recommendRepositoryAdapter.findTasteProfileByMemberId(memberId);
    }

    @Override
    public Map<String, Object> analyzePreferencesFromReviews(Long memberId) {
        log.info("리뷰 기반 취향 분석 시작: memberId={}", memberId);

        return Map.of(
            "preferredCategories", Arrays.asList(TasteCategory.KOREAN, TasteCategory.JAPANESE),
            "categoryScores", Map.of(
                "한식", 85.0,
                "일식", 78.0,
                "양식", 65.0
            ),
            "preferredTags", Arrays.asList("맛집", "깔끔", "친절"),
            "pricePreference", 60.0,
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
        return Arrays.asList(123L, 456L, 789L);
    }

    @Override
    public TasteProfile updateTasteProfile(Long memberId, Map<String, Object> analysisData) {
        log.info("취향 프로필 업데이트: memberId={}", memberId);

        Optional<TasteProfile> existingProfile = getMemberPreferences(memberId);

        TasteProfile.TasteProfileBuilder builder = TasteProfile.builder()
            .memberId(memberId)
            .preferredCategories((List<TasteCategory>) analysisData.get("preferredCategories"))
            .categoryScores((Map<String, Double>) analysisData.get("categoryScores"))
            .preferredTags((List<String>) analysisData.get("preferredTags"))
            .behaviorPatterns((Map<String, Object>) analysisData.get("behaviorPatterns"))
            .pricePreference((Double) analysisData.get("pricePreference"))
            .distancePreferenceDouble((Double) analysisData.get("distancePreference"))
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
