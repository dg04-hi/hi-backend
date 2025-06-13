// store/src/main/java/com/ktds/hi/store/biz/service/StoreService.java
package com.ktds.hi.store.biz.service;

import com.ktds.hi.store.biz.usecase.in.StoreUseCase;
import com.ktds.hi.store.biz.usecase.out.*;
import com.ktds.hi.store.biz.domain.Store;
import com.ktds.hi.store.biz.domain.StoreStatus;
import com.ktds.hi.store.infra.dto.*;
import com.ktds.hi.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 매장 서비스 구현체
 * Clean Architecture의 Application Service Layer
 *
 * @author 하이오더 개발팀
 * @version 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StoreService implements StoreUseCase {

    private final StoreRepositoryPort storeRepositoryPort;
    private final MenuRepositoryPort menuRepositoryPort;
    private final StoreTagRepositoryPort storeTagRepositoryPort;
    private final GeocodingPort geocodingPort;
    private final CachePort cachePort;
    private final EventPort eventPort;

    @Override
    @Transactional
    public StoreCreateResponse createStore(Long ownerId, StoreCreateRequest request) {
        log.info("매장 등록 시작: ownerId={}, storeName={}", ownerId, request.getStoreName());

        try {
            // 1. 입력값 검증
            validateStoreCreateRequest(request);

            // 2. 점주 매장 개수 제한 확인 (예: 최대 10개)
            validateOwnerStoreLimit(ownerId);

            // 3. 주소 지오코딩 (좌표 변환)
            Coordinates coordinates = geocodingPort.getCoordinates(request.getAddress());

            // 4. Store 도메인 객체 생성
            Store store = Store.builder()
                    .ownerId(ownerId)
                    .storeName(request.getStoreName())
                    .address(request.getAddress())
                    .latitude(coordinates.getLatitude())
                    .longitude(coordinates.getLongitude())
                    .description(request.getDescription())
                    .phone(request.getPhone())
                    .operatingHours(request.getOperatingHours())
                    .category(request.getCategory())
                    .status(StoreStatus.ACTIVE)
                    .rating(0.0)
                    .reviewCount(0)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();

            // 5. 매장 저장
            Store savedStore = storeRepositoryPort.saveStore(store);

            // 6. 매장 태그 저장
            if (request.getTags() != null && !request.getTags().isEmpty()) {
                storeTagRepositoryPort.saveStoreTags(savedStore.getId(), request.getTags());
            }

            // 7. 메뉴 정보 저장
            if (request.getMenus() != null && !request.getMenus().isEmpty()) {
                menuRepositoryPort.saveMenus(savedStore.getId(),
                        request.getMenus().stream()
                                .map(menuReq -> menuReq.toDomain(savedStore.getId()))
                                .collect(Collectors.toList()));
            }

            // 8. 매장 생성 이벤트 발행
            eventPort.publishStoreCreatedEvent(savedStore);

            // 9. 캐시 무효화
            cachePort.invalidateStoreCache(ownerId);

            log.info("매장 등록 완료: storeId={}", savedStore.getId());

            return StoreCreateResponse.builder()
                    .storeId(savedStore.getId())
                    .storeName(savedStore.getStoreName())
                    .message("매장이 성공적으로 등록되었습니다.")
                    .build();

        } catch (Exception e) {
            log.error("매장 등록 실패: ownerId={}, error={}", ownerId, e.getMessage(), e);
            throw new BusinessException("STORE_CREATE_FAILED", "매장 등록에 실패했습니다: " + e.getMessage());
        }
    }

    @Override
    public List<MyStoreListResponse> getMyStores(Long ownerId) {
        log.info("내 매장 목록 조회: ownerId={}", ownerId);

        // 1. 캐시 확인
        String cacheKey = "stores:owner:" + ownerId;
        List<MyStoreListResponse> cachedStores = cachePort.getStoreCache(cacheKey);
        if (cachedStores != null) {
            log.info("캐시에서 매장 목록 반환: ownerId={}, count={}", ownerId, cachedStores.size());
            return cachedStores;
        }

        // 2. DB에서 매장 목록 조회
        List<Store> stores = storeRepositoryPort.findStoresByOwnerId(ownerId);

        // 3. 응답 DTO 변환
        List<MyStoreListResponse> responses = stores.stream()
                .map(store -> {
                    String status = calculateStoreStatus(store);
                    return MyStoreListResponse.builder()
                            .storeId(store.getId())
                            .storeName(store.getStoreName())
                            .address(store.getAddress())
                            .category(store.getCategory())
                            .rating(store.getRating())
                            .reviewCount(store.getReviewCount())
                            .status(status)
                            .operatingHours(store.getOperatingHours())
                            .build();
                })
                .collect(Collectors.toList());

        // 4. 캐시 저장 (1시간)
        cachePort.putStoreCache(cacheKey, responses, 3600);

        log.info("내 매장 목록 조회 완료: ownerId={}, count={}", ownerId, responses.size());
        return responses;
    }

    @Override
    public StoreDetailResponse getStoreDetail(Long storeId) {
        log.info("매장 상세 조회: storeId={}", storeId);

        // 1. 매장 기본 정보 조회
        Store store = storeRepositoryPort.findStoreById(storeId)
                .orElseThrow(() -> new BusinessException("STORE_NOT_FOUND", "매장을 찾을 수 없습니다."));

        // 2. 매장 태그 조회
        List<String> tags = storeTagRepositoryPort.findTagsByStoreId(storeId);

        // 3. 메뉴 정보 조회
        List<MenuResponse> menus = menuRepositoryPort.findMenusByStoreId(storeId)
                .stream()
                .map(MenuResponse::from)
                .collect(Collectors.toList());

        // 4. AI 요약 정보 조회 (외부 서비스)
        String aiSummary = getAISummary(storeId);

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
                .status(store.getStatus().name())
                .tags(tags)
                .menus(menus)
                .aiSummary(aiSummary)
                .build();
    }

    @Override
    @Transactional
    public StoreUpdateResponse updateStore(Long storeId, Long ownerId, StoreUpdateRequest request) {
        log.info("매장 정보 수정: storeId={}, ownerId={}", storeId, ownerId);

        // 1. 매장 소유권 확인
        Store store = validateStoreOwnership(storeId, ownerId);

        // 2. 주소 변경 시 지오코딩
        Coordinates coordinates = null;
        if (!store.getAddress().equals(request.getAddress())) {
            coordinates = geocodingPort.getCoordinates(request.getAddress());
        }

        // 3. 매장 정보 업데이트
        store.updateBasicInfo(
                request.getStoreName(),
                request.getAddress(),
                request.getDescription(),
                request.getPhone(),
                request.getOperatingHours()
        );

        if (coordinates != null) {
            store.updateLocation(coordinates);
        }

        Store updatedStore = storeRepositoryPort.saveStore(store);

        // 4. 태그 업데이트
        if (request.getTags() != null) {
            storeTagRepositoryPort.deleteTagsByStoreId(storeId);
            storeTagRepositoryPort.saveStoreTags(storeId, request.getTags());
        }

        // 5. 매장 수정 이벤트 발행
        eventPort.publishStoreUpdatedEvent(updatedStore);

        // 6. 캐시 무효화
        cachePort.invalidateStoreCache(storeId);
        cachePort.invalidateStoreCache(ownerId);

        return StoreUpdateResponse.builder()
                .storeId(storeId)
                .message("매장 정보가 성공적으로 수정되었습니다.")
                .build();
    }

    @Override
    @Transactional
    public StoreDeleteResponse deleteStore(Long storeId, Long ownerId) {
        log.info("매장 삭제: storeId={}, ownerId={}", storeId, ownerId);

        // 1. 매장 소유권 확인
        Store store = validateStoreOwnership(storeId, ownerId);

        // 2. 소프트 삭제 (상태 변경)
        store.delete();
        storeRepositoryPort.saveStore(store);

        // 3. 매장 삭제 이벤트 발행
        eventPort.publishStoreDeletedEvent(storeId);

        // 4. 캐시 무효화
        cachePort.invalidateStoreCache(storeId);
        cachePort.invalidateStoreCache(ownerId);

        return StoreDeleteResponse.builder()
                .storeId(storeId)
                .message("매장이 성공적으로 삭제되었습니다.")
                .build();
    }

    @Override
    public List<StoreSearchResponse> searchStores(String keyword, String category, String tags,
                                                  Double latitude, Double longitude, Integer radius,
                                                  Integer page, Integer size) {
        log.info("매장 검색: keyword={}, category={}, location=({}, {})", keyword, category, latitude, longitude);

        StoreSearchCriteria criteria = StoreSearchCriteria.builder()
                .keyword(keyword)
                .category(category)
                .tags(tags)
                .latitude(latitude)
                .longitude(longitude)
                .radius(radius)
                .page(page)
                .size(size)
                .build();

        List<Store> stores = storeRepositoryPort.searchStores(criteria);

        return stores.stream()
                .map(store -> StoreSearchResponse.builder()
                        .storeId(store.getId())
                        .storeName(store.getStoreName())
                        .address(store.getAddress())
                        .category(store.getCategory())
                        .rating(store.getRating())
                        .reviewCount(store.getReviewCount())
                        .distance(calculateDistance(latitude, longitude, store.getLatitude(), store.getLongitude()))
                        .build())
                .collect(Collectors.toList());
    }

    // === Private Helper Methods ===

    private void validateStoreCreateRequest(StoreCreateRequest request) {
        if (request.getStoreName() == null || request.getStoreName().trim().isEmpty()) {
            throw new BusinessException("INVALID_STORE_NAME", "매장명은 필수입니다.");
        }
        if (request.getStoreName().length() > 100) {
            throw new BusinessException("INVALID_STORE_NAME", "매장명은 100자를 초과할 수 없습니다.");
        }
        if (request.getAddress() == null || request.getAddress().trim().isEmpty()) {
            throw new BusinessException("INVALID_ADDRESS", "주소는 필수입니다.");
        }
        if (request.getPhone() != null && !request.getPhone().matches("^\\d{2,3}-\\d{3,4}-\\d{4}$")) {
            throw new BusinessException("INVALID_PHONE", "전화번호 형식이 올바르지 않습니다.");
        }
    }

    private void validateOwnerStoreLimit(Long ownerId) {
        Long storeCount = storeRepositoryPort.countStoresByOwnerId(ownerId);
        if (storeCount >= 10) {
            throw new BusinessException("STORE_LIMIT_EXCEEDED", "매장은 최대 10개까지 등록할 수 있습니다.");
        }
    }

    private Store validateStoreOwnership(Long storeId, Long ownerId) {
        return storeRepositoryPort.findStoreByIdAndOwnerId(storeId, ownerId)
                .orElseThrow(() -> new BusinessException("STORE_ACCESS_DENIED", "매장에 대한 권한이 없습니다."));
    }

    private String calculateStoreStatus(Store store) {
        if (!store.isActive()) {
            return "비활성";
        }
        // 운영시간 기반 현재 상태 계산 로직
        return "운영중";
    }

    private String getAISummary(Long storeId) {
        // TODO: AI 분석 서비스 연동 구현
        return "AI 요약 정보가 준비 중입니다.";
    }

    private Double calculateDistance(Double lat1, Double lon1, Double lat2, Double lon2) {
        if (lat1 == null || lon1 == null || lat2 == null || lon2 == null) {
            return null;
        }
        return geocodingPort.calculateDistance(
                new Coordinates(lat1, lon1),
                new Coordinates(lat2, lon2)
        );
    }
}
