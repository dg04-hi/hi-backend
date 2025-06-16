package com.ktds.hi.store.infra.gateway.entity;

import com.ktds.hi.store.domain.Menu;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * 메뉴 엔티티
 * 메뉴 정보를 데이터베이스에 저장하기 위한 JPA 엔티티
 *
 * @author 하이오더 개발팀
 * @version 1.0.0
 */
@Entity
@Table(name = "menus")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class MenuEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "menu_id")
    private Long id;

    @Column(name = "store_id", nullable = false)
    private Long storeId;

    @Column(name = "menu_name", nullable = false, length = 100)
    private String menuName;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "price", nullable = false)
    private Integer price;

    @Column(name = "category", length = 50)
    private String category;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Column(name = "is_available", nullable = false)
    private Boolean isAvailable;

    @Column(name = "order_count", nullable = false)
    @Builder.Default
    private Integer orderCount = 0;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * 엔티티를 도메인 객체로 변환
     */
    public Menu toDomain() {
        return Menu.builder()
                .id(this.id)
                .storeId(this.storeId)
                .menuName(this.menuName)
                .description(this.description)
                .price(this.price)
                .category(this.category)
                .imageUrl(this.imageUrl)
                .available(this.isAvailable)
                .orderCount(this.orderCount)
                .createdAt(this.createdAt)
                .updatedAt(this.updatedAt)
                .build();
    }

    /**
     * 도메인 객체를 엔티티로 변환
     */
    public static MenuEntity fromDomain(Menu menu) {
        return MenuEntity.builder()
                .id(menu.getId())
                .storeId(menu.getStoreId())
                .menuName(menu.getMenuName())
                .description(menu.getDescription())
                .price(menu.getPrice())
                .category(menu.getCategory())
                .imageUrl(menu.getImageUrl())
                .isAvailable(menu.getAvailable())
                .orderCount(menu.getOrderCount())
                .createdAt(menu.getCreatedAt())
                .updatedAt(menu.getUpdatedAt())
                .build();
    }
}