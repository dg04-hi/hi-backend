package com.ktds.hi.analytics.infra.dto;

import com.ktds.hi.analytics.biz.domain.PlanStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 실행 계획 목록 응답 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActionPlanListResponse {
    
    private Long id;
    private String title;
    private PlanStatus status;
    private String description;
    private String period;
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;
}
