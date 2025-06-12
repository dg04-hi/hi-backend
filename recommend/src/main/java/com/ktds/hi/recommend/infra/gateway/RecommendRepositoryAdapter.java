package com.ktds.hi.recommend.infra.gateway;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ktds.hi.recommend.biz.usecase.out.RecommendRepository;
import com.ktds.hi.recommend.biz.domain.RecommendHistory;
import com.ktds.hi.recommend.biz.domain.RecommendStore;
import com.ktds.hi.recommend.biz.domain.TasteProfile;
import com.ktds.hi.recommend.biz.domain.RecommendType;
import com.ktds.hi.recommend.infra.gateway.repository.RecommendHistoryJpaRepository;
import com.ktds.hi.recommend.infra.gateway.repository.TasteProfileJpaRepository;
import com.ktds.hi.recommend.infra.gateway.entity.RecommendHistoryEntity;
import com.ktds.hi.recommend.infra.gateway.entity.TasteProfileEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class RecommendRepositoryAdapter implements RecommendRepository {

    private final RecommendHistoryJpaRepository recommendHistoryJpaRepository;
    private final TasteProfileJpaRepository tasteProfileJpaRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public RecommendHistory saveRecommendHistory(RecommendHistory history) {
        try {
            RecommendHistoryEntity entity = toRecommendHistoryEntity(history);
            RecommendHistoryEntity savedEntity = recommendHistoryJpaRepository.save(entity);
            return toRecommendHistory(savedEntity);
        } catch (Exception e) {
            log.error("추천 히스토리 저장 실패", e);
            throw new RuntimeException("추천 히스토리 저장 실패", e);
        }
    }

    @Override
    public List<RecommendHistory> findRecommendHistoriesByMemberId(Long memberId) {
        List<RecommendHistoryEntity> entities = recommendHistoryJpaRepository.findByMemberIdOrderByCreatedAtDesc(memberId);
        return entities.stream()
            .map(this::toRecommendHistory)
            .toList();
    }

    @Override
    public TasteProfile saveTasteProfile(TasteProfile profile) {
        try {
            TasteProfileEntity entity = toTasteProfileEntity(profile);
            TasteProfileEntity savedEntity = tasteProfileJpaRepository.save(entity);
            return toTasteProfile(savedEntity);
        } catch (Exception e) {
            log.error("취향 프로필 저장 실패", e);
            throw new RuntimeException("취향 프로필 저장 실패", e);
        }
    }

    @Override
    public Optional<TasteProfile> findTasteProfileByMemberId(Long memberId) {
        return tasteProfileJpaRepository.findByMemberId(memberId)
            .map(this::toTasteProfile);
    }

    @Override
    public Optional<RecommendStore> findStoreById(Long storeId) {
        // Mock 구현 - 실제로는 Store 서비스 호출 또는 캐시에서 조회
        return Optional.of(RecommendStore.builder()
            .storeId(storeId)
            .storeName("매장 " + storeId)
            .address("서울시 강남구")
            .category("한식")
            .rating(4.5)
            .tags(Arrays.asList("맛집", "친절"))
            .distance(500.0)
            .priceRange("중간")
            .build());
    }

    @Override
    public List<RecommendStore> findPopularStores(String category, Integer limit) {
        // Mock 구현
        return Arrays.asList(
            RecommendStore.builder()
                .storeId(1L)
                .storeName("인기 매장 1")
                .category(category != null ? category : "한식")
                .rating(4.8)
                .tags(Arrays.asList("인기", "맛집"))
                .build()
        );
    }

    @Override
    public void logStoreClick(Long memberId, Long storeId) {
        log.info("매장 클릭 로그: memberId={}, storeId={}", memberId, storeId);
        // 비동기 로깅 구현
    }

    @Override
    public void logRecommendation(Long memberId, List<Long> storeIds, String context) {
        try {
            RecommendHistory history = RecommendHistory.builder()
                .memberId(memberId)
                .recommendedStoreIds(storeIds)
                .criteria(context)
                .recommendTypeEnum(RecommendType.PREFERENCE_BASED)
                .createdAt(LocalDateTime.now())
                .build();
            saveRecommendHistory(history);
        } catch (Exception e) {
            log.error("추천 로그 저장 실패", e);
        }
    }

    // 변환 메서드들
    private RecommendHistory toRecommendHistory(RecommendHistoryEntity entity) {
        return RecommendHistory.builder()
            .id(entity.getId())
            .memberId(entity.getMemberId())
            .recommendedStoreIds(entity.getRecommendedStoreIdsList())
            .recommendTypeEnum(entity.getRecommendType())
            .criteria(entity.getCriteria())
            .createdAt(entity.getCreatedAt())
            .build();
    }

    private TasteProfile toTasteProfile(TasteProfileEntity entity) {
        return TasteProfile.builder()
            .id(entity.getId())
            .memberId(entity.getMemberId())
            .preferredCategories(entity.getPreferredCategoriesList())
            .categoryScores(entity.getCategoryScoresMap())
            .preferredTags(entity.getPreferredTagsList())
            .behaviorPatterns(entity.getBehaviorPatternsMap())
            .pricePreference(entity.getPricePreference())
            .distancePreferenceDouble(entity.getDistancePreference())
            .createdAt(entity.getCreatedAt())
            .updatedAt(entity.getUpdatedAt())
            .build();
    }

    private RecommendHistoryEntity toRecommendHistoryEntity(RecommendHistory domain) {
        try {
            return RecommendHistoryEntity.builder()
                .id(domain.getId())
                .memberId(domain.getMemberId())
                .recommendedStoreIdsJson(objectMapper.writeValueAsString(domain.getRecommendedStoreIds()))
                .recommendType(domain.getRecommendTypeEnum())
                .criteria(domain.getCriteria())
                .build();
        } catch (Exception e) {
            throw new RuntimeException("Entity 변환 실패", e);
        }
    }

    private TasteProfileEntity toTasteProfileEntity(TasteProfile domain) {
        try {
            return TasteProfileEntity.builder()
                .id(domain.getId())
                .memberId(domain.getMemberId())
                .preferredCategoriesJson(objectMapper.writeValueAsString(domain.getPreferredCategories()))
                .categoryScoresJson(objectMapper.writeValueAsString(domain.getCategoryScores()))
                .preferredTagsJson(objectMapper.writeValueAsString(domain.getPreferredTags()))
                .behaviorPatternsJson(objectMapper.writeValueAsString(domain.getBehaviorPatterns()))
                .pricePreference(domain.getPricePreference())
                .distancePreference(domain.getDistancePreferenceDouble())
                .build();
        } catch (Exception e) {
            throw new RuntimeException("Entity 변환 실패", e);
        }
    }
}
