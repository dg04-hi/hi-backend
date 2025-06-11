package com.ktds.hi.store.infra.gateway;

import com.ktds.hi.store.biz.domain.Store;
import com.ktds.hi.store.biz.usecase.out.StoreRepositoryPort;
import com.ktds.hi.store.infra.gateway.entity.StoreEntity;
import com.ktds.hi.store.infra.gateway.repository.StoreJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 매장 리포지토리 어댑터 클래스
 * Store Repository Port를 구현하여 데이터 영속성 기능을 제공
 */
@Component
@RequiredArgsConstructor
public class StoreRepositoryAdapter implements StoreRepositoryPort {
    
    private final StoreJpaRepository storeJpaRepository;
    
    @Override
    public List<Store> findStoresByOwnerId(Long ownerId) {
        return storeJpaRepository.findByOwnerId(ownerId)
                .stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    public Optional<Store> findStoreById(Long storeId) {
        return storeJpaRepository.findById(storeId)
                .map(this::toDomain);
    }
    
    @Override
    public Optional<Store> findStoreByIdAndOwnerId(Long storeId, Long ownerId) {
        return storeJpaRepository.findByIdAndOwnerId(storeId, ownerId)
                .map(this::toDomain);
    }
    
    @Override
    public Store saveStore(Store store) {
        StoreEntity entity = toEntity(store);
        StoreEntity saved = storeJpaRepository.save(entity);
        return toDomain(saved);
    }
    
    @Override
    public void deleteStore(Long storeId) {
        storeJpaRepository.deleteById(storeId);
    }
    
    /**
     * Entity를 Domain으로 변환
     */
    private Store toDomain(StoreEntity entity) {
        return Store.builder()
                .id(entity.getId())
                .ownerId(entity.getOwnerId())
                .storeName(entity.getStoreName())
                .address(entity.getAddress())
                .latitude(entity.getLatitude())
                .longitude(entity.getLongitude())
                .category(entity.getCategory())
                .description(entity.getDescription())
                .phone(entity.getPhone())
                .operatingHours(entity.getOperatingHours())
                .tags(entity.getTagsJson() != null ? parseTagsJson(entity.getTagsJson()) : List.of())
                .status(entity.getStatus())
                .rating(entity.getRating())
                .reviewCount(entity.getReviewCount())
                .imageUrl(entity.getImageUrl())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
    
    /**
     * Domain을 Entity로 변환
     */
    private StoreEntity toEntity(Store domain) {
        return StoreEntity.builder()
                .id(domain.getId())
                .ownerId(domain.getOwnerId())
                .storeName(domain.getStoreName())
                .address(domain.getAddress())
                .latitude(domain.getLatitude())
                .longitude(domain.getLongitude())
                .category(domain.getCategory())
                .description(domain.getDescription())
                .phone(domain.getPhone())
                .operatingHours(domain.getOperatingHours())
                .tagsJson(domain.getTags() != null ? String.join(",", domain.getTags()) : "")
                .status(domain.getStatus())
                .rating(domain.getRating())
                .reviewCount(domain.getReviewCount())
                .imageUrl(domain.getImageUrl())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .build();
    }
    
    /**
     * JSON 태그를 List로 파싱
     */
    private List<String> parseTagsJson(String tagsJson) {
        if (tagsJson == null || tagsJson.trim().isEmpty()) {
            return List.of();
        }
        return Arrays.asList(tagsJson.split(","));
    }
}
