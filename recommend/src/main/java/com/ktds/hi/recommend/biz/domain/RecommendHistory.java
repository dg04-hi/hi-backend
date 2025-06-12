package com.ktds.hi.recommend.biz.domain;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class RecommendHistory {
    private Long id;
    private Long memberId;
    private Long storeId;
    private String recommendType;
    private Double score;
    private String reason;
    private LocalDateTime recommendedAt;
    private Boolean clicked;
    private Boolean visited;
}