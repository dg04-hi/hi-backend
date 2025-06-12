package com.ktds.hi.store.biz.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 매장 도메인 클래스
 * 매장 정보를 담는 도메인 객체
 *
 * @author 하이오더 개발팀
 * @version 1.0.0
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Store {

    private Long id;
    private Long ownerId;
    private String storeName;
    private String address;
    private Double latitude;
    private Double longitude;
    private String category;
    private String description;
    private String phone;
    private String operatingHours;
    private List<String> tags;
    private StoreStatus status;
    private Double rating;
    private Integer reviewCount;
    private String imageUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * 매장 기본 정보 업데이트
     */
    public Store updateBasicInfo(String storeName, String address, String description,
                                 String phone, String operatingHours) {
        return Store.builder()
                .id(this.id)
                .ownerId(this.ownerId)
                .storeName(storeName)
                .address(address)
                .latitude(this.latitude)
                .longitude(this.longitude)
                .category(this.category)
                .description(description)
                .phone(phone)
                .operatingHours(operatingHours)
                .tags(this.tags)
                .status(this.status)
                .rating(this.rating)
                .reviewCount(this.reviewCount)
                .imageUrl(this.imageUrl)
                .createdAt(this.createdAt)
                .updatedAt(LocalDateTime.now())
                .build();
    }

    /**
     * 매장 위치 정보 업데이트
     */
    public Store updateLocation(Double latitude, Double longitude) {
        return Store.builder()
                .id(this.id)
                .ownerId(this.ownerId)
                .storeName(this.storeName)
                .address(this.address)
                .latitude(latitude)
                .longitude(longitude)
                .category(this.category)
                .description(this.description)
                .phone(this.phone)
                .operatingHours(this.operatingHours)
                .tags(this.tags)
                .status(this.status)
                .rating(this.rating)
                .reviewCount(this.reviewCount)
                .imageUrl(this.imageUrl)
                .createdAt(this.createdAt)
                .updatedAt(LocalDateTime.now())
                .build();
    }

    /**
     * 매장 평점 및 리뷰 수 업데이트
     */
    public Store updateRating(Double rating, Integer reviewCount) {
        return Store.builder()
                .id(this.id)
                .ownerId(this.ownerId)
                .storeName(this.storeName)
                .address(this.address)
                .latitude(this.latitude)
                .longitude(this.longitude)
                .category(this.category)
                .description(this.description)
                .phone(this.phone)
                .operatingHours(this.operatingHours)
                .tags(this.tags)
                .status(this.status)
                .rating(rating)
                .reviewCount(reviewCount)
                .imageUrl(this.imageUrl)
                .createdAt(this.createdAt)
                .updatedAt(LocalDateTime.now())
                .build();
    }

    /**
     * 매장 활성화
     */
    public Store activate() {
        return Store.builder()
                .id(this.id)
                .ownerId(this.ownerId)
                .storeName(this.storeName)
                .address(this.address)
                .latitude(this.latitude)
                .longitude(this.longitude)
                .category(this.category)
                .description(this.description)
                .phone(this.phone)
                .operatingHours(this.operatingHours)
                .tags(this.tags)
                .status(StoreStatus.ACTIVE)
                .rating(this.rating)
                .reviewCount(this.reviewCount)
                .imageUrl(this.imageUrl)
                .createdAt(this.createdAt)
                .updatedAt(LocalDateTime.now())
                .build();
    }

    /**
     * 매장 비활성화
     */
    public Store deactivate() {
        return Store.builder()
                .id(this.id)
                .ownerId(this.ownerId)
                .storeName(this.storeName)
                .address(this.address)
                .latitude(this.latitude)
                .longitude(this.longitude)
                .category(this.category)
                .description(this.description)
                .phone(this.phone)
                .operatingHours(this.operatingHours)
                .tags(this.tags)
                .status(StoreStatus.INACTIVE)
                .rating(this.rating)
                .reviewCount(this.reviewCount)
                .imageUrl(this.imageUrl)
                .createdAt(this.createdAt)
                .updatedAt(LocalDateTime.now())
                .build();
    }

    /**
     * 매장 활성 상태 확인
     */
    public boolean isActive() {
        return StoreStatus.ACTIVE.equals(this.status);
    }

    /**
     * 매장 소유권 확인
     */
    public boolean isOwnedBy(Long ownerId) {
        return this.ownerId != null && this.ownerId.equals(ownerId);
    }

    /**
     * 두 좌표 간의 거리 계산 (킬로미터)
     */
    public Double calculateDistance(Double targetLatitude, Double targetLongitude) {
        if (this.latitude == null || this.longitude == null ||
                targetLatitude == null || targetLongitude == null) {
            return null;
        }

        final int EARTH_RADIUS = 6371; // 지구 반지름 (킬로미터)

        double latDistance = Math.toRadians(targetLatitude - this.latitude);
        double lonDistance = Math.toRadians(targetLongitude - this.longitude);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(this.latitude)) * Math.cos(Math.toRadians(targetLatitude))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS * c;
    }
}