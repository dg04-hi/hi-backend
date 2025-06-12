package com.ktds.hi.recommend.infra.gateway;

import com.ktds.hi.recommend.biz.usecase.out.AiRecommendRepository;
import com.ktds.hi.recommend.biz.domain.RecommendStore;
import com.ktds.hi.recommend.biz.domain.RecommendType;
import com.ktds.hi.recommend.biz.domain.TasteProfile;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class AiRecommendAdapter implements AiRecommendRepository {

    @Override
    public List<RecommendStore> recommendStoresByAI(Long memberId, Map<String, Object> preferences) {
        log.info("AI 기반 매장 추천 요청: memberId={}", memberId);

        return Arrays.asList(
            RecommendStore.builder()
                .storeId(1L)
                .storeName("AI 추천 매장 1")
                .address("서울시 강남구 역삼동")
                .category("한식")
                .tags(Arrays.asList("맛집", "깔끔", "한식"))
                .rating(4.5)
                .reviewCount(150)
                .distance(500.0)
                .recommendScore(92.0)
                .recommendType(RecommendType.AI_RECOMMENDATION)
                .recommendReason("사용자 취향과 92% 일치")
                .build()
        );
    }

    @Override
    public List<RecommendStore> recommendStoresBySimilarUsers(Long memberId) {
        log.info("유사 사용자 기반 추천 요청: memberId={}", memberId);

        return Arrays.asList(
            RecommendStore.builder()
                .storeId(3L)
                .storeName("유사 취향 추천 매장")
                .address("서울시 서초구 서초동")
                .category("양식")
                .tags(Arrays.asList("파스타", "분위기", "데이트"))
                .rating(4.2)
                .reviewCount(78)
                .distance(1200.0)
                .recommendScore(85.0)
                .recommendType(RecommendType.COLLABORATIVE_FILTERING)
                .recommendReason("비슷한 취향의 사용자들이 선호")
                .build()
        );
    }

    @Override
    public List<RecommendStore> recommendStoresByCollaborativeFiltering(Long memberId) {
        return recommendStoresBySimilarUsers(memberId);
    }

    @Override
    public List<RecommendStore> filterByPreferences(List<RecommendStore> stores, TasteProfile tasteProfile, String tags) {
        log.info("취향 기반 매장 필터링: 매장 수={}, 태그={}", stores.size(), tags);

        // 간단한 필터링 로직 구현
        return stores.stream()
            .filter(store -> {
                if (tasteProfile.getPreferredTags() != null) {
                    return store.getTags().stream()
                        .anyMatch(tag -> tasteProfile.getPreferredTags().contains(tag));
                }
                return true;
            })
            .toList();
    }

    @Override
    public String getStoreSummary(Long storeId) {
        log.info("매장 AI 요약 조회: storeId={}", storeId);
        return "AI가 분석한 매장 요약: 고객들이 맛과 서비스를 높이 평가하는 매장입니다.";
    }
}