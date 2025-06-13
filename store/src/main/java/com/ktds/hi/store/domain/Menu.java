package com.ktds.hi.store.biz.domain;

import lombok.Builder;
import lombok.Getter;

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
     * 메뉴 판매 상태 변경
     */
    public void setAvailable(Boolean available) {
        this.available = available;
    }

    /**
     * 메뉴 이용 가능 여부 확인
     */
    public boolean isAvailable() {
        return this.available != null && this.available;
    }
}