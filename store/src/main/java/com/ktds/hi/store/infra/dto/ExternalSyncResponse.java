package com.ktds.hi.store.infra.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 외부 플랫폼 동기화 응답 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExternalSyncResponse {
    
    private Boolean success;
    private String message;
    private Integer syncedCount;
}
