package com.ktds.hi.store.infra.controller;

import com.ktds.hi.store.biz.usecase.in.ExternalIntegrationUseCase;
import com.ktds.hi.store.infra.dto.ExternalSyncRequest;
import com.ktds.hi.store.infra.dto.ExternalSyncResponse;
import com.ktds.hi.store.infra.dto.ExternalConnectRequest;
import com.ktds.hi.store.infra.dto.ExternalConnectResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 외부 연동 컨트롤러 클래스
 * 외부 플랫폼 연동 관련 API를 제공
 */
@RestController
@RequestMapping("/api/external")
@RequiredArgsConstructor
@Tag(name = "외부연동 API", description = "외부 플랫폼 연동 및 동기화 관련 API")
public class ExternalIntegrationController {
    
    private final ExternalIntegrationUseCase externalIntegrationUseCase;
    
    /**
     * 외부 플랫폼 리뷰 동기화 API
     */
    @PostMapping("/stores/{storeId}/sync-reviews")
    @Operation(summary = "외부 플랫폼 리뷰 동기화", description = "외부 플랫폼의 리뷰를 동기화합니다.")
    public ResponseEntity<ExternalSyncResponse> syncReviews(
            @PathVariable Long storeId,
            @Valid @RequestBody ExternalSyncRequest request) {
        ExternalSyncResponse response = externalIntegrationUseCase.syncReviews(storeId, request);
        return ResponseEntity.ok(response);
    }
    
    /**
     * 외부 플랫폼 계정 연동 API
     */
    @PostMapping("/stores/{storeId}/connect")
    @Operation(summary = "외부 플랫폼 계정 연동", description = "외부 플랫폼 계정을 연동합니다.")
    public ResponseEntity<ExternalConnectResponse> connectPlatform(
            @PathVariable Long storeId,
            @Valid @RequestBody ExternalConnectRequest request) {
        ExternalConnectResponse response = externalIntegrationUseCase.connectPlatform(storeId, request);
        return ResponseEntity.ok(response);
    }
}
