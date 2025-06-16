package com.ktds.hi.store.infra.gateway.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * 매장별 태그 클릭 통계 엔티티
 * 어떤 매장에서 어떤 태그가 얼마나 클릭되었는지 저장
 */
@Entity
@Table(name = "store_tag_clicks")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StoreTagClickEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "store_id", nullable = false)
    private Long storeId;

    @Column(name = "tag_id", nullable = false)
    private Long tagId;

    @Column(name = "click_count")
    @Builder.Default
    private Long clickCount = 0L;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * 클릭 수 증가
     */
    public void incrementClickCount() {
        this.clickCount = this.clickCount != null ? this.clickCount + 1 : 1L;
        this.updatedAt = LocalDateTime.now();
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
}