package com.ktds.hi.store.domain;

import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;

/**
 * 메뉴 도메인 엔티티
 */
@Getter
@Builder
public class Menu {

    private Long id;
    private Long storeId;
    private String menuName;
    private String description;
    private Integer price;
    private String category;
    private String imageUrl;
    private Boolean available;
    private LocalDateTime createdAt;    // 추가
    private LocalDateTime updatedAt;    // 추가
    private Integer orderCount;


    /**
     * 메뉴 정보 업데이트
     */
    public void updateMenuInfo(String menuName, String description, Integer price,
                               String category, String imageUrl) {
        this.menuName = menuName;
        this.description = description;
        this.price = price;
        this.category = category;
        this.imageUrl = imageUrl;
    }

    /**
     * 메뉴 카테고리 업데이트
     */
    public Menu updateCategory(String category) {
        return Menu.builder()
                .id(this.id)
                .storeId(this.storeId)
                .menuName(this.menuName)
                .description(this.description)
                .price(this.price)
                .category(category)
                .imageUrl(this.imageUrl)
                .available(this.available)
                .orderCount(this.orderCount)
                .createdAt(this.createdAt)
                .updatedAt(LocalDateTime.now())
                .build();
    }

    /**
     * 메뉴 이미지 업데이트
     */
    public Menu updateImage(String imageUrl) {
        return Menu.builder()
                .id(this.id)
                .storeId(this.storeId)
                .menuName(this.menuName)
                .description(this.description)
                .price(this.price)
                .category(this.category)
                .imageUrl(imageUrl)
                .available(this.available)
                .orderCount(this.orderCount)
                .createdAt(this.createdAt)
                .updatedAt(LocalDateTime.now())
                .build();
    }

    /**
     * 메뉴 정보 업데이트 (불변 객체 반환)
     */
    public Menu updateInfo(String menuName, String description, Integer price) {
        return Menu.builder()
                .id(this.id)
                .storeId(this.storeId)
                .menuName(menuName)
                .description(description)
                .price(price)
                .category(this.category)
                .imageUrl(this.imageUrl)
                .available(this.available)
                .orderCount(this.orderCount)
                .createdAt(this.createdAt)
                .updatedAt(LocalDateTime.now())
                .build();
    }

    /**
     * 메뉴 판매 상태 변경 (불변 객체 반환)
     */
    public Menu setAvailable(Boolean available) {
        return Menu.builder()
                .id(this.id)
                .storeId(this.storeId)
                .menuName(this.menuName)
                .description(this.description)
                .price(this.price)
                .category(this.category)
                .imageUrl(this.imageUrl)
                .available(available)
                .orderCount(this.orderCount)
                .createdAt(this.createdAt)
                .updatedAt(LocalDateTime.now())
                .build();
    }
    /**
     * 메뉴 이용 가능 여부 확인
     */
    public boolean isAvailable() {
        return this.available != null && this.available;
    }

    /**
     * 메뉴가 유효한지 확인
     */
    public boolean isValid() {
        return this.menuName != null && !this.menuName.trim().isEmpty() &&
                this.price != null && this.price > 0 &&
                this.storeId != null;
    }
}