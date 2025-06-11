package com.ktds.hi.recommend.biz.service;

import com.ktds.hi.recommend.biz.usecase.in.StoreRecommendUseCase;
import com.ktds.hi.recommend.biz.usecase.out.*;
import com.ktds.hi.recommend.biz.domain.*;
import com.ktds.hi.recommend.infra.dto.request.RecommendStoreRequest;
import com.ktds.hi.recommend.infra.dto.response.RecommendStoreResponse;
import com.ktds.hi.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 매장 추천 인터랙터 클래스
 * 사용자 취향 기반 매장 추천 기능을 구현
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class StoreRecommendInteractor implements StoreRecommendUseCase {
    
    private final RecommendRepository recommendRepository;
    private final AiRecommendRepository aiRecommendRepository;
    private final LocationRepository locationRepository;
    private final UserPreferenceRepository userPreferenceRepository;
    
    @Override
    public List<RecommendStoreResponse> recommendStores(Long memberId, RecommendStoreRequest request) {
        // 사용자 취향 프로필 조회
        TasteProfile tasteProfile = userPreferenceRepository.getMemberPreferences(memberId)
                .orElseThrow(() -> new BusinessException("사용자 취향 정보를 찾을 수 없습니다. 취향 등록을 먼저 해주세요."));
        
        // AI 기반 추천
        Map<String, Object> preferences = Map.of(
                "categories", tasteProfile.getPreferredCategories(),
                "tags", tasteProfile.getPreferredTags(),
                "pricePreference", tasteProfile.getPricePreference(),
                "distancePreference", tasteProfile.getDistancePreference(),
                "latitude", request.getLatitude(),
                "longitude", request.getLongitude()
        );
        
        List<RecommendStore> aiRecommendStores = aiRecommendRepository.recommendStoresByAI(memberId, preferences);
        
        // 위치 기반 추천 결합
        List<RecommendStore> locationStores = locationRepository.findStoresWithinRadius(
                request.getLatitude(), request.getLongitude(), request.getRadius());
        
        // 추천 결과 통합 및 점수 계산
        List<RecommendStore> combinedStores = combineRecommendations(aiRecommendStores, locationStores, tasteProfile);
        
        // 추천 히스토리 저장
        RecommendHistory history = RecommendHistory.builder()
                .memberId(memberId)
                .recommendedStoreIds(combinedStores.stream().map(RecommendStore::getStoreId).collect(Collectors.toList()))
                .recommendType(RecommendType.TASTE_BASED)
                .criteria("취향 + AI + 위치 기반 통합 추천")
                .createdAt(LocalDateTime.now())
                .build();
        
        recommendRepository.saveRecommendHistory(history);
        
        log.info("매장 추천 완료: memberId={}, 추천 매장 수={}", memberId, combinedStores.size());
        
        return combinedStores.stream()
                .map(this::toRecommendStoreResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<RecommendStoreResponse> recommendStoresByLocation(Double latitude, Double longitude, Integer radius) {
        List<RecommendStore> stores = locationRepository.findStoresWithinRadius(latitude, longitude, radius);
        
        return stores.stream()
                .map(store -> store.updateRecommendReason("위치 기반 추천"))
                .map(this::toRecommendStoreResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<RecommendStoreResponse> recommendPopularStores(String category, Integer limit) {
        // Mock 구현 - 실제로는 인기도 기반 쿼리 필요
        List<RecommendStore> popularStores = List.of(
                RecommendStore.builder()
                        .storeId(1L)
                        .storeName("인기 매장 1")
                        .address("서울시 강남구")
                        .category(category)
                        .rating(4.5)
                        .reviewCount(100)
                        .recommendScore(95.0)
                        .recommendType(RecommendType.POPULARITY_BASED)
                        .recommendReason("높은 평점과 많은 리뷰")
                        .build()
        );
        
        return popularStores.stream()
                .limit(limit != null ? limit : 10)
                .map(this::toRecommendStoreResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * 추천 결과 통합 및 점수 계산
     */
    private List<RecommendStore> combineRecommendations(List<RecommendStore> aiStores, 
                                                       List<RecommendStore> locationStores,
                                                       TasteProfile profile) {
        // AI 추천과 위치 기반 추천을 통합하여 최종 점수 계산
        // 실제로는 더 복잡한 로직이 필요
        
        return aiStores.stream()
                .map(store -> store.updateRecommendScore(
                        calculateFinalScore(store, profile)
                ))
                .sorted((s1, s2) -> Double.compare(s2.getRecommendScore(), s1.getRecommendScore()))
                .limit(20)
                .collect(Collectors.toList());
    }
    
    /**
     * 최종 추천 점수 계산
     */
    private Double calculateFinalScore(RecommendStore store, TasteProfile profile) {
        double baseScore = store.getRecommendScore() != null ? store.getRecommendScore() : 0.0;
        double ratingScore = store.getRating() != null ? store.getRating() * 10 : 0.0;
        double reviewScore = store.getReviewCount() != null ? Math.min(store.getReviewCount() * 0.1, 10) : 0.0;
        double distanceScore = store.getDistance() != null ? Math.max(0, 10 - store.getDistance() / 1000) : 0.0;
        
        return (baseScore * 0.4) + (ratingScore * 0.3) + (reviewScore * 0.2) + (distanceScore * 0.1);
    }
    
    /**
     * 도메인을 응답 DTO로 변환
     */
    private RecommendStoreResponse toRecommendStoreResponse(RecommendStore store) {
        return RecommendStoreResponse.builder()
                .storeId(store.getStoreId())
                .storeName(store.getStoreName())
                .address(store.getAddress())
                .category(store.getCategory())
                .tags(store.getTags())
                .rating(store.getRating())
                .reviewCount(store.getReviewCount())
                .distance(store.getDistance())
                .recommendScore(store.getRecommendScore())
                .recommendReason(store.getRecommendReason())
                .build();
    }
}
