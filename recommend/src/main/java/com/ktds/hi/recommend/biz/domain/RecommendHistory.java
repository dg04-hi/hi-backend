package com.ktds.hi.recommend.biz.domain;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class RecommendHistory {
    private Long id;
    private Long memberId;
    private Long storeId;               // 기존 필드
    private String recommendType;       // 기존 필드 (String)
    private Double score;
    private String reason;
    private LocalDateTime recommendedAt;
    private Boolean clicked;
    private Boolean visited;

    // 추가 필드들 (Adapter에서 사용)
    private List<Long> recommendedStoreIds;
    private RecommendType recommendTypeEnum;
    private String criteria;
    private LocalDateTime createdAt;
}