package com.ktds.hi.recommend.infra.gateway;

import com.ktds.hi.recommend.biz.usecase.out.RecommendRepository;
import com.ktds.hi.recommend.biz.domain.RecommendHistory;
import com.ktds.hi.recommend.biz.domain.TasteProfile;
import com.ktds.hi.recommend.infra.gateway.repository.RecommendHistoryJpaRepository;
import com.ktds.hi.recommend.infra.gateway.repository.TasteProfileJpaRepository;
import com.ktds.hi.recommend.infra.gateway.entity.RecommendHistoryEntity;
import com.ktds.hi.recommend.infra.gateway.entity.TasteProfileEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 추천 리포지토리 어댑터 클래스
 * 도메인 리포지토리 인터페이스를 JPA 리포지토리에 연결
 */
@Component
@RequiredArgsConstructor
public class RecommendRepositoryAdapter implements RecommendRepository {
    
    private final RecommendHistoryJpaRepository recommendHistoryJpaRepository;
    private final TasteProfileJpaRepository tasteProfileJpaRepository;
    
    @Override
    public RecommendHistory saveRecommendHistory(RecommendHistory history) {
        RecommendHistoryEntity entity = toRecommendHistoryEntity(history);
        RecommendHistoryEntity savedEntity = recommendHistoryJpaRepository.save(entity);
        return toRecommendHistory(savedEntity);
    }
    
    @Override
    public List<RecommendHistory> findRecommendHistoriesByMemberId(Long memberId) {
        List<RecommendHistoryEntity> entities = recommendHistoryJpaRepository.findByMemberIdOrderByCreatedAtDesc(memberId);
        return entities.stream()
                .map(this::toRecommendHistory)
                .collect(Collectors.toList());
    }
    
    @Override
    public TasteProfile saveTasteProfile(TasteProfile profile) {
        TasteProfileEntity entity = toTasteProfileEntity(profile);
        TasteProfileEntity savedEntity = tasteProfileJpaRepository.save(entity);
        return toTasteProfile(savedEntity);
    }
    
    @Override
    public Optional<TasteProfile> findTasteProfileByMemberId(Long memberId) {
        return tasteProfileJpaRepository.findByMemberId(memberId)
                .map(this::toTasteProfile);
    }
    
    /**
     * 엔티티를 도메인으로 변환
     */
    private RecommendHistory toRecommendHistory(RecommendHistoryEntity entity) {
        return RecommendHistory.builder()
                .id(entity.getId())
                .memberId(entity.getMemberId())
                .recommendedStoreIds(entity.getRecommendedStoreIdsList())
                .recommendType(entity.getRecommendType())
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
                .distancePreference(entity.getDistancePreference())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
    
    /**
     * 도메인을 엔티티로 변환
     */
    private RecommendHistoryEntity toRecommendHistoryEntity(RecommendHistory domain) {
        return RecommendHistoryEntity.builder()
                .id(domain.getId())
                .memberId(domain.getMemberId())
                .recommendedStoreIdsJson(domain.getRecommendedStoreIds().toString()) // JSON 변환 필요
                .recommendType(domain.getRecommendType())
                .criteria(domain.getCriteria())
                .build();
    }
    
    private TasteProfileEntity toTasteProfileEntity(TasteProfile domain) {
        return TasteProfileEntity.builder()
                .id(domain.getId())
                .memberId(domain.getMemberId())
                .preferredCategoriesJson(domain.getPreferredCategories().toString()) // JSON 변환 필요
                .categoryScoresJson(domain.getCategoryScores().toString()) // JSON 변환 필요
                .preferredTagsJson(domain.getPreferredTags().toString()) // JSON 변환 필요
                .behaviorPatternsJson(domain.getBehaviorPatterns().toString()) // JSON 변환 필요
                .pricePreference(domain.getPricePreference())
                .distancePreference(domain.getDistancePreference())
                .build();
    }
}
