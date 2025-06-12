package com.ktds.hi.recommend.biz.service;

import com.ktds.hi.recommend.biz.domain.PreferenceTag;
import com.ktds.hi.recommend.biz.usecase.in.TasteAnalysisUseCase;
import com.ktds.hi.recommend.biz.usecase.out.PreferenceTagRepository;
import com.ktds.hi.recommend.biz.usecase.out.UserPreferenceRepository;
import com.ktds.hi.recommend.biz.domain.TasteProfile;
import com.ktds.hi.recommend.biz.domain.TasteCategory;
import com.ktds.hi.recommend.infra.dto.response.PreferenceTagResponse;
import com.ktds.hi.recommend.infra.dto.response.TasteAnalysisResponse;
import com.ktds.hi.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 취향 분석 인터랙터 클래스
 * 사용자 취향 분석 기능을 구현
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TasteAnalysisInteractor implements TasteAnalysisUseCase {
    
    private final UserPreferenceRepository userPreferenceRepository;
    private final PreferenceTagRepository preferenceTagRepository;
    
    @Override
    @Transactional(readOnly = true)
    public TasteAnalysisResponse analyzeMemberTaste(Long memberId) {
        TasteProfile profile = userPreferenceRepository.getMemberPreferences(memberId)
                .orElseThrow(() -> new BusinessException("사용자 취향 정보를 찾을 수 없습니다"));
        
        // 취향 분석 결과 생성
        List<String> preferredCategories = profile.getPreferredCategories()
                .stream()
                .map(TasteCategory::getDescription)
                .collect(Collectors.toList());
        
        Map<String, Double> categoryScores = profile.getCategoryScores();
        String topCategory = categoryScores.entrySet()
                .stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("정보 없음");
        
        return TasteAnalysisResponse.builder()
                .memberId(memberId)
                .preferredCategories(preferredCategories)
                .topCategory(topCategory)
                .categoryScores(categoryScores)
                .preferredTags(profile.getPreferredTags())
                .pricePreference(profile.getPricePreference())
                .distancePreference(profile.getDistancePreference())
                .analysisDate(profile.getUpdatedAt())
                .build();
    }
    
    @Override
    public void updateTasteProfile(Long memberId) {
        log.info("취향 프로필 업데이트 시작: memberId={}", memberId);
        
        try {
            // 리뷰 기반 취향 분석
            Map<String, Object> analysisData = userPreferenceRepository.analyzePreferencesFromReviews(memberId);
            
            // 취향 프로필 업데이트
            TasteProfile updatedProfile = userPreferenceRepository.updateTasteProfile(memberId, analysisData);
            
            log.info("취향 프로필 업데이트 완료: memberId={}, profileId={}", memberId, updatedProfile.getId());
            
        } catch (Exception e) {
            log.error("취향 프로필 업데이트 실패: memberId={}, error={}", memberId, e.getMessage(), e);
            throw new BusinessException("취향 프로필 업데이트 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    @Override
    public List<PreferenceTagResponse> getAvailablePreferenceTags() {
        log.info("가용한 취향 태그 목록 조회");

        try {
            List<PreferenceTag> tags = preferenceTagRepository.findAllActiveTags();

            return tags.stream()
                .map(tag -> PreferenceTagResponse.builder()
                    .tagName(tag.getTagName())
                    .icon(tag.getIcon())
                    .description(tag.getDescription())
                    .build())
                .collect(Collectors.toList());

        } catch (Exception e) {
            log.error("취향 태그 목록 조회 중 오류 발생", e);
            throw new BusinessException("취향 태그 목록 조회에 실패했습니다");
        }
    }
}
