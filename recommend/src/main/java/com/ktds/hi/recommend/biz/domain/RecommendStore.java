package com.ktds.hi.recommend.biz.domain;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class RecommendStore {
    private Long storeId;
    private String storeName;
    private String address;
    private String category;
    private Double latitude;
    private Double longitude;
    private Double rating;
    private Integer reviewCount;
    private List<String> tags;
    private String recommendReason;
    private Double recommendScore;
    private Double distance;
    private String priceRange;
}