// store/src/main/java/com/ktds/hi/store/biz/service/StoreService.java
package com.ktds.hi.store.biz.service;

import com.ktds.hi.store.biz.usecase.in.StoreUseCase;
import com.ktds.hi.store.infra.dto.*;
import com.ktds.hi.store.infra.dto.response.StoreListResponse;
import com.ktds.hi.store.infra.gateway.entity.StoreEntity;
import com.ktds.hi.store.infra.gateway.entity.TagEntity;
import com.ktds.hi.store.infra.gateway.repository.StoreJpaRepository;
import com.ktds.hi.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 매장 서비스 구현체 (간단 버전)
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StoreService implements StoreUseCase {

    private final StoreJpaRepository storeJpaRepository;

    @Override
    @Transactional
    public StoreCreateResponse createStore(Long ownerId, StoreCreateRequest request) {
        log.info("매장 등록: ownerId={}, storeName={}", ownerId, request.getStoreName());

        // 기본 검증
        if (request.getStoreName() == null || request.getStoreName().trim().isEmpty()) {
            throw new BusinessException("INVALID_STORE_NAME", "매장명은 필수입니다.");
        }
        if (request.getAddress() == null || request.getAddress().trim().isEmpty()) {
            throw new BusinessException("INVALID_ADDRESS", "주소는 필수입니다.");
        }

        // 매장 엔티티 생성
        StoreEntity store = StoreEntity.builder()
                .ownerId(ownerId)
                .storeName(request.getStoreName())
                .address(request.getAddress())
                .latitude(37.5665) // 기본 좌표 (서울시청)
                .longitude(126.9780)
                .description(request.getDescription())
                .phone(request.getPhone())
                .operatingHours(request.getOperatingHours())
                .category(request.getCategory())
                .tagsJson(String.join(",", request.getTags()))
                .status("ACTIVE")
                .rating(0.0)
                .reviewCount(0)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        StoreEntity savedStore = storeJpaRepository.save(store);

        log.info("매장 등록 완료: storeId={}", savedStore.getId());



        return StoreCreateResponse.builder()
                .storeId(savedStore.getId())
                .storeName(savedStore.getStoreName())
                .message("매장이 성공적으로 등록되었습니다.")
                .build();
    }

    @Override
    public List<MyStoreListResponse> getMyStores(Long ownerId) {
        log.info("내 매장 목록 조회: ownerId={}", ownerId);

        List<StoreEntity> stores = storeJpaRepository.findByOwnerId(ownerId);

        return stores.stream()
                .map(store -> MyStoreListResponse.builder()
                        .storeId(store.getId())
                        .storeName(store.getStoreName())
                        .address(store.getAddress())
                        .category(store.getCategory())
                        .rating(store.getRating())
                        .reviewCount(store.getReviewCount())
                        .status("운영중")
                        .imageUrl(store.getImageUrl())
                        .operatingHours(store.getOperatingHours())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public String getAllTags(Long storeId){
        return storeJpaRepository.findById(storeId).get().getTagsJson();
    }
    @Override
    public List<StoreListResponse> getAllStores() {

        List<StoreEntity> stores = storeJpaRepository.findAll();

        return stores.stream()
                .map(store -> StoreListResponse.builder()
                        .storeId(store.getId())
                        .storeName(store.getStoreName())
                        .address(store.getAddress())
                        .category(store.getCategory())
                        .rating(store.getRating())
                        .reviewCount(store.getReviewCount())
                        .status("운영중")
                        .tagJson(store.getTagsJson())
                        .imageUrl(store.getImageUrl())
                        .operatingHours(store.getOperatingHours())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public StoreDetailResponse getStoreDetail(Long storeId) {

        StoreEntity store = storeJpaRepository.findById(storeId)
                .orElseThrow(() -> new BusinessException("STORE_NOT_FOUND", "매장을 찾을 수 없습니다."));

        List<String> tagNameList = store.getTags().stream()
            .map(TagEntity::getTagName)
            .toList();

        return StoreDetailResponse.builder()
                .storeId(store.getId())
                .storeName(store.getStoreName())
                .address(store.getAddress())
                .latitude(store.getLatitude())
                .longitude(store.getLongitude())
                .description(store.getDescription())
                .phone(store.getPhone())
                .operatingHours(store.getOperatingHours())
                .category(store.getCategory())
                .rating(store.getRating())
                .reviewCount(store.getReviewCount())
                .status(store.getStatus())
                .imageUrl(store.getImageUrl())
                .tags(tagNameList)
                .build();
    }

    @Override
    @Transactional
    public StoreUpdateResponse updateStore(Long storeId, Long ownerId, StoreUpdateRequest request) {
        log.info("매장 수정: storeId={}, ownerId={}", storeId, ownerId);

        StoreEntity store = storeJpaRepository.findByIdAndOwnerId(storeId, ownerId)
                .orElseThrow(() -> new BusinessException("STORE_ACCESS_DENIED", "매장에 대한 권한이 없습니다."));

        store.updateInfo(request.getStoreName(), request.getAddress(), request.getDescription(),
                request.getPhone(), request.getOperatingHours(), request.getImageUrl());

        storeJpaRepository.save(store);

        return StoreUpdateResponse.builder()
                .storeId(storeId)
                .message("매장 정보가 수정되었습니다.")
                .build();
    }

    @Override
    @Transactional
    public StoreDeleteResponse deleteStore(Long storeId, Long ownerId) {
        log.info("매장 삭제: storeId={}, ownerId={}", storeId, ownerId);

        StoreEntity store = storeJpaRepository.findByIdAndOwnerId(storeId, ownerId)
                .orElseThrow(() -> new BusinessException("STORE_ACCESS_DENIED", "매장에 대한 권한이 없습니다."));

        store.updateStatus("DELETED");
        storeJpaRepository.save(store);

        return StoreDeleteResponse.builder()
                .storeId(storeId)
                .message("매장이 삭제되었습니다.")
                .build();
    }





    @Override
    public List<StoreSearchResponse> searchStores(String keyword, String category, String tags,
                                                  Double latitude, Double longitude, Integer radius,
                                                  Integer page, Integer size) {
        log.info("매장 검색: keyword={}, category={}", keyword, category);

        List<StoreEntity> stores;
        if (keyword != null && !keyword.trim().isEmpty()) {
            stores = storeJpaRepository.findByStoreNameContainingOrAddressContaining(keyword, keyword);
        } else if (category != null && !category.trim().isEmpty()) {
            stores = storeJpaRepository.findByCategory(category);
        } else {
            stores = storeJpaRepository.findAll();
        }

        return stores.stream()
                .map(store -> StoreSearchResponse.builder()
                        .storeId(store.getId())
                        .storeName(store.getStoreName())
                        .address(store.getAddress())
                        .category(store.getCategory())
                        .rating(store.getRating())
                        .reviewCount(store.getReviewCount())
                        .distance(1.5) // 더미 거리
                        .build())
                .collect(Collectors.toList());
    }
}
