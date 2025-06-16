package com.ktds.hi.store.infra.gateway;

import com.ktds.hi.store.domain.Store;
import com.ktds.hi.store.domain.StoreStatus;
import com.ktds.hi.store.biz.usecase.out.StoreRepositoryPort;
import com.ktds.hi.store.biz.usecase.out.StoreSearchCriteria;
import com.ktds.hi.store.infra.gateway.entity.StoreEntity;
import com.ktds.hi.store.infra.gateway.repository.StoreJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 매장 리포지토리 어댑터 클래스
 * Store Repository Port를 구현하여 데이터 영속성 기능을 제공
 *
 * @author 하이오더 개발팀
 * @version 1.0.0
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
    public List<Store> findStoresByTagNames(List<String> tagNames) {
        return storeJpaRepository.findByTagNamesIn(tagNames)
                .stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Store> findStoresByAllTagNames(List<String> tagNames) {
        return storeJpaRepository.findByAllTagNames(tagNames, tagNames.size())
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

    @Override
    public List<Store> searchStores(StoreSearchCriteria searchCriteria) {
        // 복잡한 검색 로직을 단순화 - 실제로는 QueryDSL이나 Criteria API 사용 권장
        List<StoreEntity> entities;

        if (searchCriteria.hasKeywordFilter()) {
            entities = storeJpaRepository.findByStoreNameContainingOrAddressContaining(
                    searchCriteria.getKeyword(), searchCriteria.getKeyword());
        } else if (searchCriteria.hasCategoryFilter()) {
            entities = storeJpaRepository.findByCategory(searchCriteria.getCategory());
        } else {
            entities = storeJpaRepository.findAll();
        }

        return entities.stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Store> findStoresByCategory(String category) {
        return storeJpaRepository.findByCategory(category)
                .stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Store> findStoresWithinRadius(Double latitude, Double longitude, Double radiusKm) {
        // 실제로는 공간 데이터베이스 쿼리 사용 (PostGIS 등)
        // 여기서는 단순한 구현으로 대체
        return storeJpaRepository.findAll()
                .stream()
                .map(this::toDomain)
                .filter(store -> {
                    if (store.getLatitude() == null || store.getLongitude() == null) {
                        return false;
                    }
                    Double distance = store.calculateDistance(latitude, longitude);
                    return distance != null && distance <= radiusKm;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<Store> findStoresByKeyword(String keyword) {
        return storeJpaRepository.findByStoreNameContainingOrAddressContaining(keyword, keyword)
                .stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Store> findActiveStores() {
        return storeJpaRepository.findByStatus(StoreStatus.ACTIVE.name())
                .stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Store> findTopRatedStores(Integer limit) {
        Pageable pageable = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "rating"));
        return storeJpaRepository.findAllByOrderByRatingDesc(pageable)
                .stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsById(Long storeId) {
        return storeJpaRepository.existsById(storeId);
    }

    @Override
    public Long countStoresByOwnerId(Long ownerId) {
        return storeJpaRepository.countByOwnerId(ownerId);
    }

    @Override
    public Store updateStoreStatus(Long storeId, String status) {
        Optional<StoreEntity> entityOpt = storeJpaRepository.findById(storeId);
        if (entityOpt.isPresent()) {
            StoreEntity entity = entityOpt.get();
            entity.updateStatus(status);
            StoreEntity saved = storeJpaRepository.save(entity);
            return toDomain(saved);
        }
        return null;
    }

    @Override
    public List<Store> saveStores(List<Store> stores) {
        List<StoreEntity> entities = stores.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());

        List<StoreEntity> savedEntities = storeJpaRepository.saveAll(entities);

        return savedEntities.stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
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
                .status(StoreStatus.fromString(entity.getStatus()))
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
                .status(domain.getStatus() != null ? domain.getStatus().name() : StoreStatus.INACTIVE.name())
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