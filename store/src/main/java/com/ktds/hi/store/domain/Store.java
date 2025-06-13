// store/src/main/java/com/ktds/hi/store/biz/domain/Store.java
package com.ktds.hi.store.domain;

import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;

/**
 * 매장 도메인 엔티티
 * Clean Architecture의 Domain Layer
 *
 * @author 하이오더 개발팀
 * @version 1.0.0
 */
@Getter
@Builder
public class Store {

    private Long id;
    private Long ownerId;
    private String storeName;
    private String address;
    private Double latitude;
    private Double longitude;
    private String description;
    private String phone;
    private String operatingHours;
    private String category;
    private Double rating;
    private Integer reviewCount;
    private StoreStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * 매장 기본 정보 업데이트
     */
    public void updateBasicInfo(String storeName, String address, String description,
                                String phone, String operatingHours) {
        this.storeName = storeName;
        this.address = address;
        this.description = description;
        this.phone = phone;
        this.operatingHours = operatingHours;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 매장 위치 업데이트
     */
    public void updateLocation(Coordinates coordinates) {
        this.latitude = coordinates.getLatitude();
        this.longitude = coordinates.getLongitude();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 매장 평점 업데이트
     */
    public void updateRating(Double rating, Integer reviewCount) {
        this.rating = rating;
        this.reviewCount = reviewCount;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 매장 활성화
     */
    public void activate() {
        this.status = StoreStatus.ACTIVE;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 매장 비활성화
     */
    public void deactivate() {
        this.status = StoreStatus.INACTIVE;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 매장 삭제 (소프트 삭제)
     */
    public void delete() {
        this.status = StoreStatus.DELETED;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 활성 상태 확인
     */
    public boolean isActive() {
        return this.status == StoreStatus.ACTIVE;
    }

    /**
     * 점주 소유 확인
     */
    public boolean isOwnedBy(Long ownerId) {
        return this.ownerId.equals(ownerId);
    }

    /**
     * 거리 계산
     */
    public Double calculateDistance(Double targetLatitude, Double targetLongitude) {
        if (this.latitude == null || this.longitude == null ||
                targetLatitude == null || targetLongitude == null) {
            return null;
        }

        // Haversine 공식을 사용한 거리 계산
        double earthRadius = 6371; // 지구 반지름 (km)

        double dLat = Math.toRadians(targetLatitude - this.latitude);
        double dLon = Math.toRadians(targetLongitude - this.longitude);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(this.latitude)) * Math.cos(Math.toRadians(targetLatitude)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return earthRadius * c;
    }
}