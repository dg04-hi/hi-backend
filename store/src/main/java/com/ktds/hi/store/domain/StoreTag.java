package com.ktds.hi.store.biz.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 매장 태그 도메인 엔티티
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StoreTag {

    private Long id;
    private Long storeId;
    private String tagName;
}