package com.ktds.hi.analytics.infra.dto;

import com.ktds.hi.analytics.biz.domain.PlanStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 실행 계획 상세 응답 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActionPlanDetailResponse {
    
    private Long id;
    private Long storeId;
    private String title;
    private String description;
    private String period;
    private PlanStatus status;
    private List<String> tasks;
    private String note;
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;
}
