package com.ktds.hi.store.infra.gateway.entity;

import jakarta.persistence.*;
import lombok.*;
import com.ktds.hi.store.domain.TagCategory;
import com.ktds.hi.store.infra.gateway.entity.StoreEntity;

import java.util.HashSet;
import java.util.Set;

/**
 * 태그 엔티티 클래스
 * 매장 태그 정보를 저장
 *
 * @author 하이오더 개발팀
 * @version 1.0.0
 */
@Entity
@Table(name = "tags")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TagEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToMany(mappedBy = "tags")
    private Set<StoreEntity> stores = new HashSet<>();

    @Column(name = "tag_name", nullable = false, length = 50)
    private String tagName; // 매운맛, 깨끗한, 유제품 등

    @Enumerated(EnumType.STRING)
    @Column(name = "tag_category", nullable = false)
    private TagCategory tagCategory; // TASTE, ATMOSPHERE, ALLERGY 등

    @Column(name = "tag_color", length = 7)
    private String tagColor; // #FF5722

    @Column(name = "sort_order")
    private Integer sortOrder;

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "click_count")
    @Builder.Default
    private Long clickCount = 0L;

    /**
     * 클릭 수 증가
     */
    public void incrementClickCount() {
        this.clickCount = this.clickCount != null ? this.clickCount + 1 : 1L;
    }
}