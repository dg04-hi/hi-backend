package com.ktds.hi.recommend.infra.gateway;

import com.ktds.hi.recommend.biz.usecase.out.AiRecommendRepository;
import com.ktds.hi.recommend.biz.domain.RecommendStore;
import com.ktds.hi.recommend.biz.domain.RecommendType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * AI 추천 어댑터 클래스
 * AI 기반 추천 기능을 구현 (현재는 Mock 구현)
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class AiRecommendAdapter implements AiRecommendRepository {
    
    @Override
    public List<RecommendStore> recommendStoresByAI(Long memberId, Map<String, Object> preferences) {
        log.info("AI 기반 매장 추천 요청: memberId={}, preferences={}", memberId, preferences);
        
        // Mock 구현 - 실제로는 AI 모델 API 호출
        return List.of(
                RecommendStore.builder()
                        .storeId(1L)
                        .storeName("AI 추천 매장 1")
                        .address("서울시 강남구 역삼동")
                        .category("한식")
                        .tags(List.of("맛집", "깔끔", "한식"))
                        .rating(4.5)
                        .reviewCount(150)
                        .distance(500.0)
                        .recommendScore(92.0)
                        .recommendType(RecommendType.AI_RECOMMENDATION)
                        .recommendReason("사용자 취향과 92% 일치")
                        .build(),
                
                RecommendStore.builder()
                        .storeId(2L)
                        .storeName("AI 추천 매장 2")
                        .address("서울시 강남구 논현동")
                        .category("일식")
                        .tags(List.of("초밥", "신선", "일식"))
                        .rating(4.3)
                        .reviewCount(89)
                        .distance(800.0)
                        .recommendScore(87.0)
                        .recommendType(RecommendType.AI_RECOMMENDATION)
                        .recommendReason("사용자가 선호하는 일식 카테고리")
                        .build()
        );
    }
    
    @Override
    public List<RecommendStore> recommendStoresBySimilarUsers(Long memberId) {
        log.info("유사 사용자 기반 추천 요청: memberId={}", memberId);
        
        // Mock 구현
        return List.of(
                RecommendStore.builder()
                        .storeId(3L)
                        .storeName("유사 취향 추천 매장")
                        .address("서울시 서초구 서초동")
                        .category("양식")
                        .tags(List.of("파스타", "분위기", "양식"))
                        .rating(4.4)
                        .reviewCount(203)
                        .distance(1200.0)
                        .recommendScore(85.0)
                        .recommendType(RecommendType.SIMILAR_USER)
                        .recommendReason("비슷한 취향의 사용자들이 좋아하는 매장")
                        .build()
        );
    }
    
    @Override
    public List<RecommendStore> recommendStoresByCollaborativeFiltering(Long memberId) {
        log.info("협업 필터링 추천 요청: memberId={}", memberId);
        
        // Mock 구현
        return List.of(
                RecommendStore.builder()
                        .storeId(4L)
                        .storeName("협업 필터링 추천 매장")
                        .address("서울시 마포구 홍대입구")
                        .category("카페")
                        .tags(List.of("커피", "디저트", "분위기"))
                        .rating(4.2)
                        .reviewCount(127)
                        .distance(2500.0)
                        .recommendScore(82.0)
                        .recommendType(RecommendType.COLLABORATIVE_FILTERING)
                        .recommendReason("사용자 행동 패턴 기반 추천")
                        .build()
        );
    }
}
