package com.ktds.hi.analytics.biz.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 실행 계획 도메인 클래스
 * 점주가 수립한 개선 실행 계획을 나타냄
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActionPlan {
    
    private Long id;
    private Long storeId;
    private Long userId;
    private String title;
    private String description;
    private String period;
    private PlanStatus status;
    private List<String> tasks;
    private String note;
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;
    private LocalDateTime updatedAt;
}
