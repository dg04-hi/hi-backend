package com.ktds.hi.analytics.infra.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 실행 계획 삭제 응답 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActionPlanDeleteResponse {
    
    private Long planId;
    private Boolean deleted;
    private LocalDateTime deletedAt;
}
