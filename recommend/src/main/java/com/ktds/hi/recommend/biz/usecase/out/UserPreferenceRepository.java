package com.ktds.hi.recommend.biz.usecase.out;

import com.ktds.hi.recommend.biz.domain.TasteProfile;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface UserPreferenceRepository {

    Optional<TasteProfile> getMemberPreferences(Long memberId);
    Map<String, Object> analyzePreferencesFromReviews(Long memberId);
    List<Long> findSimilarTasteMembers(Long memberId);
    TasteProfile updateTasteProfile(Long memberId, Map<String, Object> analysisData);
}