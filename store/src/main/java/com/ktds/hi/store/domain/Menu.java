package com.ktds.hi.store.biz.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 메뉴 도메인 엔티티
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Menu {

    private Long id;
    private Long storeId;
    private String name;
    private String description;
    private BigDecimal price;
    private String category;
    private String imageUrl;
    private Boolean isAvailable;
}
