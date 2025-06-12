package com.ktds.hi.store.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 매장 검색 조건
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StoreSearchCriteria {

    private String keyword;
    private String category;
    private String tags;
    private Double latitude;
    private Double longitude;
    private Integer radius;
    private Integer page;
    private Integer size;
}