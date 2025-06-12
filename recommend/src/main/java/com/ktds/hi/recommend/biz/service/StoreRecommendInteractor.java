package com.ktds.hi.recommend.biz.service;

import com.ktds.hi.recommend.biz.usecase.in.StoreRecommendUseCase;
import com.ktds.hi.recommend.biz.usecase.out.*;
import com.ktds.hi.recommend.biz.domain.*;
import com.ktds.hi.recommend.infra.dto.request.RecommendStoreRequest;
import com.ktds.hi.recommend.infra.dto.response.RecommendStoreResponse;
import com.ktds.hi.recommend.infra.dto.response.StoreDetailResponse;
import com.ktds.hi.common.dto.PageResponse;
import com.ktds.hi.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Arrays;

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
    public List<RecommendStoreResponse> recommendPersonalizedStores(Long memberId, RecommendStoreRequest request) {
        try {
            // 사용자 취향 프로필 조회
            TasteProfile tasteProfile = userPreferenceRepository.getMemberPreferences(memberId)
                .orElse(createDefaultTasteProfile(memberId));

            // 위치 기반 매장 검색
            List<RecommendStore> nearbyStores = locationRepository.findStoresWithinDistance(
                request.getLatitude(),
                request.getLongitude(),
                request.getRadius()
            );

            // 취향 기반 필터링 및 점수 계산
            List<RecommendStore> recommendedStores = aiRecommendRepository.filterByPreferences(
                nearbyStores,
                tasteProfile,
                request.getTags()
            );

            // 추천 로그 저장
            recommendRepository.logRecommendation(memberId,
                recommendedStores.stream().map(RecommendStore::getStoreId).toList(),
                "개인화추천"
            );

            return convertToResponseList(recommendedStores);

        } catch (Exception e) {
            log.error("개인화 매장 추천 실패: memberId={}", memberId, e);
            return getDefaultRecommendations();
        }
    }

    @Override
    public PageResponse<RecommendStoreResponse> recommendStoresByLocation(Double latitude, Double longitude, Integer radius, String category, Pageable pageable) {
        try {
            List<RecommendStore> stores = locationRepository.findStoresWithinDistance(latitude, longitude, radius);

            // 카테고리 필터링
            if (category != null && !category.isEmpty()) {
                stores = stores.stream()
                    .filter(store -> category.equals(store.getCategory()))
                    .toList();
            }

            // 페이징 처리
            int start = (int) pageable.getOffset();
            int end = Math.min(start + pageable.getPageSize(), stores.size());
            List<RecommendStore> pagedStores = stores.subList(start, end);

            List<RecommendStoreResponse> responses = convertToResponseList(pagedStores);

            return PageResponse.of(responses, pageable.getPageNumber(), pageable.getPageSize(), stores.size());

        } catch (Exception e) {
            log.error("위치 기반 매장 추천 실패: lat={}, lng={}", latitude, longitude, e);
            return PageResponse.of(getDefaultRecommendations(), 0, pageable.getPageSize(), 0);
        }
    }

    @Override
    public List<RecommendStoreResponse> recommendPopularStores(String category, Integer limit) {
        try {
            List<RecommendStore> popularStores = recommendRepository.findPopularStores(category, limit);
            return convertToResponseList(popularStores);

        } catch (Exception e) {
            log.error("인기 매장 추천 실패: category={}", category, e);
            return getDefaultRecommendations();
        }
    }

    @Override
    public StoreDetailResponse getRecommendedStoreDetail(Long storeId, Long memberId) {
        try {
            RecommendStore store = recommendRepository.findStoreById(storeId)
                .orElseThrow(() -> new BusinessException("매장을 찾을 수 없습니다."));

            // 클릭 로그 저장 (조회도 클릭으로 간주)
            if (memberId != null) {
                recommendRepository.logStoreClick(memberId, storeId);
            }

            // AI 요약 정보 조회
            String aiSummary = aiRecommendRepository.getStoreSummary(storeId);

            // 개인화 추천 이유 생성
            String personalizedReason = "";
            if (memberId != null) {
                TasteProfile tasteProfile = userPreferenceRepository.getMemberPreferences(memberId).orElse(null);
                if (tasteProfile != null) {
                    personalizedReason = generatePersonalizedReason(store, tasteProfile);
                }
            }

            return StoreDetailResponse.builder()
                .storeId(store.getStoreId())
                .storeName(store.getStoreName())
                .address(store.getAddress())
                .category(store.getCategory())
                .rating(store.getRating())
                .distance(store.getDistance())
                .tags(store.getTags())
                .aiSummary(aiSummary)
                .personalizedReason(personalizedReason)
                .build();

        } catch (Exception e) {
            log.error("매장 상세 조회 실패: storeId={}", storeId, e);
            throw new BusinessException("매장 상세 정보를 조회할 수 없습니다.");
        }
    }

    @Override
    public void logRecommendClick(Long memberId, Long storeId) {
        try {
            recommendRepository.logStoreClick(memberId, storeId);
            log.info("추천 클릭 로그 저장: memberId={}, storeId={}", memberId, storeId);
        } catch (Exception e) {
            log.error("추천 클릭 로그 저장 실패: memberId={}, storeId={}", memberId, storeId, e);
        }
    }

    // 기본 취향 프로필 생성
    private TasteProfile createDefaultTasteProfile(Long memberId) {
        return TasteProfile.builder()
            .memberId(memberId)
            .cuisinePreferences(Arrays.asList("한식", "중식", "일식"))
            .priceRange("중간")
            .distancePreference(3000)
            .tasteTags(Arrays.asList("맛있는", "친절한"))
            .build();
    }

    // 개인화 추천 이유 생성
    private String generatePersonalizedReason(RecommendStore store, TasteProfile tasteProfile) {
        StringBuilder reason = new StringBuilder();

        // 취향 태그 매칭
        List<String> matchingTags = store.getTags().stream()
            .filter(tasteProfile.getTasteTags()::contains)
            .toList();

        if (!matchingTags.isEmpty()) {
            reason.append("당신이 좋아하는 '").append(String.join(", ", matchingTags))
                .append("' 태그와 일치합니다. ");
        }

        // 가격대 매칭
        if (tasteProfile.getPriceRange().equals(store.getPriceRange())) {
            reason.append("선호하시는 ").append(tasteProfile.getPriceRange())
                .append(" 가격대 매장입니다. ");
        }

        return reason.toString().trim();
    }

    // 응답 변환
    private List<RecommendStoreResponse> convertToResponseList(List<RecommendStore> stores) {
        return stores.stream()
            .map(store -> RecommendStoreResponse.builder()
                .storeId(store.getStoreId())
                .storeName(store.getStoreName())
                .address(store.getAddress())
                .category(store.getCategory())
                .rating(store.getRating())
                .distance(store.getDistance())
                .tags(store.getTags())
                .recommendReason(store.getRecommendReason())
                .build())
            .toList();
    }

    // 기본 추천 목록 (에러 발생 시)
    private List<RecommendStoreResponse> getDefaultRecommendations() {
        return Arrays.asList(
            RecommendStoreResponse.builder()
                .storeId(1L)
                .storeName("맛집 플레이스")
                .address("서울시 강남구 테헤란로 123")
                .category("한식")
                .rating(4.5)
                .distance(500)
                .tags(Arrays.asList("맛있는", "친절한"))
                .recommendReason("인기 매장입니다")
                .build()
        );
    }
}