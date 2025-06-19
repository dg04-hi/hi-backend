package com.ktds.hi.store.infra.gateway.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.util.HashSet;
import java.util.Set;
import java.time.LocalDateTime;

/**
 * 매장 엔티티 클래스
 * 데이터베이스 stores 테이블과 매핑되는 JPA 엔티티
 *
 * @author 하이오더 개발팀
 * @version 1.0.0
 */
@Entity
@Table(name = "stores")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class StoreEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "owner_id", nullable = false)
    private Long ownerId;

    @Column(name = "store_name", nullable = false, length = 100)
    private String storeName;

    @Column(nullable = false, length = 300)
    private String address;

    @Column
    private Double latitude;

    @Column
    private Double longitude;

    @Column(length = 500)
    private String description;

    @Column(length = 20)
    private String phone;

    @Column(name = "operating_hours", length = 200)
    private String operatingHours;

    @Column(length = 50)
    private String category;

    @Column(name = "tags_json", columnDefinition = "TEXT")
    private String tagsJson;

    @Column(length = 20)
    @Builder.Default
    private String status = "INACTIVE";

    @Column
    @Builder.Default
    private Double rating = 0.0;

    @Column(name = "review_count")
    @Builder.Default
    private Integer reviewCount = 0;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @ManyToMany
    @JoinTable(
            name = "store_tags",
            joinColumns = @JoinColumn(name = "store_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<TagEntity> tags = new HashSet<>();

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * 매장 상태 업데이트
     */
    public void updateStatus(String status) {
        this.status = status;
    }

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
    }

    /**
     * 매장 위치 정보 업데이트
     */
    public void updateLocation(Double latitude, Double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    /**
     * 매장 평점 및 리뷰 수 업데이트
     */
    public void updateRating(Double rating, Integer reviewCount) {
        this.rating = rating;
        this.reviewCount = reviewCount;
    }

    /**
            * 매장 기본 정보 업데이트
    */
    public void updateInfo(String storeName, String address, String description,
                           String phone, String operatingHours, String imageUrl) {
        this.storeName = storeName;
        this.address = address;
        this.description = description;
        this.phone = phone;
        this.operatingHours = operatingHours;
        this.updatedAt = LocalDateTime.now();
        this.imageUrl = imageUrl;
    }

    /**
     * 매장 태그 업데이트
     */
    public void updateTags(String tagsJson) {
        this.tagsJson = tagsJson;
    }

    /**
     * 매장 카테고리 업데이트
     */
    public void updateCategory(String category) {
        this.category = category;
    }

    /**
     * 매장 이미지 URL 업데이트
     */
    public void updateImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    /**
     * 매장이 활성 상태인지 확인
     */
    public boolean isActive() {
        return "ACTIVE".equals(this.status);
    }

    /**
     * 특정 점주의 매장인지 확인
     */
    public boolean isOwnedBy(Long ownerId) {
        return this.ownerId != null && this.ownerId.equals(ownerId);
    }

    /**
     * 매장 위치 정보가 있는지 확인
     */
    public boolean hasLocationInfo() {
        return this.latitude != null && this.longitude != null;
    }

    /**
     * 매장 평점이 설정되어 있는지 확인
     */
    public boolean hasRating() {
        return this.rating != null && this.rating > 0;
    }

    /**
     * 매장에 리뷰가 있는지 확인
     */
    public boolean hasReviews() {
        return this.reviewCount != null && this.reviewCount > 0;
    }


}