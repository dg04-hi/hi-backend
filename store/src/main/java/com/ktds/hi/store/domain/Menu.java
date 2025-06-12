package com.ktds.hi.store.biz.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 메뉴 도메인 클래스
 * 메뉴 정보를 담는 도메인 객체
 *
 * @author 하이오더 개발팀
 * @version 1.0.0
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Menu {

    private Long id;
    private Long storeId;
    private String menuName;
    private String description;
    private Integer price;
    private String category;
    private String imageUrl;
    private Boolean isAvailable;
    private Integer orderCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * 메뉴 기본 정보 업데이트
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
                .isAvailable(this.isAvailable)
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
                .isAvailable(this.isAvailable)
                .orderCount(this.orderCount)
                .createdAt(this.createdAt)
                .updatedAt(LocalDateTime.now())
                .build();
    }

    /**
     * 메뉴 판매 가능 상태 설정
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
                .isAvailable(available)
                .orderCount(this.orderCount)
                .createdAt(this.createdAt)
                .updatedAt(LocalDateTime.now())
                .build();
    }

    /**
     * 주문 수 증가
     */
    public Menu incrementOrderCount() {
        return Menu.builder()
                .id(this.id)
                .storeId(this.storeId)
                .menuName(this.menuName)
                .description(this.description)
                .price(this.price)
                .category(this.category)
                .imageUrl(this.imageUrl)
                .isAvailable(this.isAvailable)
                .orderCount(this.orderCount != null ? this.orderCount + 1 : 1)
                .createdAt(this.createdAt)
                .updatedAt(LocalDateTime.now())
                .build();
    }

    /**
     * 메뉴 판매 가능 여부 확인
     */
    public Boolean isAvailable() {
        return this.isAvailable != null && this.isAvailable;
    }

    /**
     * 특정 매장에 속하는지 확인
     */
    public boolean belongsToStore(Long storeId) {
        return this.storeId != null && this.storeId.equals(storeId);
    }

    /**
     * 메뉴가 유효한지 확인
     */
    public boolean isValid() {
        return this.menuName != null && !this.menuName.trim().isEmpty() &&
                this.price != null && this.price > 0 &&
                this.storeId != null;
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
                .isAvailable(this.isAvailable)
                .orderCount(this.orderCount)
                .createdAt(this.createdAt)
                .updatedAt(LocalDateTime.now())
                .build();
    }

    /**
     * 메뉴 가격 할인 적용
     */
    public Menu applyDiscount(double discountRate) {
        if (discountRate < 0 || discountRate > 1) {
            throw new IllegalArgumentException("할인율은 0~1 사이의 값이어야 합니다.");
        }

        int discountedPrice = (int) (this.price * (1 - discountRate));

        return Menu.builder()
                .id(this.id)
                .storeId(this.storeId)
                .menuName(this.menuName)
                .description(this.description)
                .price(discountedPrice)
                .category(this.category)
                .imageUrl(this.imageUrl)
                .isAvailable(this.isAvailable)
                .orderCount(this.orderCount)
                .createdAt(this.createdAt)
                .updatedAt(LocalDateTime.now())
                .build();
    }

    /**
     * 메뉴 정보가 변경되었는지 확인
     */
    public boolean hasChanges(Menu other) {
        if (other == null) return true;

        return !this.menuName.equals(other.menuName) ||
                !this.description.equals(other.description) ||
                !this.price.equals(other.price) ||
                !this.category.equals(other.category) ||
                !this.isAvailable.equals(other.isAvailable);
    }
}