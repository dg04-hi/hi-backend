package com.ktds.hi.analytics.infra.controller;

import com.ktds.hi.analytics.biz.usecase.in.ActionPlanUseCase;
import com.ktds.hi.analytics.infra.dto.*;
import com.ktds.hi.common.dto.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;

/**
 * 실행 계획 컨트롤러 클래스
 * 실행 계획 관련 API를 제공
 */
@Slf4j
@RestController
@RequestMapping("/api/action-plans")
@RequiredArgsConstructor
@Tag(name = "Action Plan API", description = "실행 계획 관리 API")
public class ActionPlanController {
    
    private final ActionPlanUseCase actionPlanUseCase;
    
    /**
     * 실행 계획 목록 조회
     */
    @Operation(summary = "실행 계획 목록 조회", description = "매장의 실행 계획 목록을 조회합니다.")
    @GetMapping("/stores/{storeId}")
    public ResponseEntity<SuccessResponse<List<ActionPlanListResponse>>> getActionPlans(
            @Parameter(description = "매장 ID", required = true)
            @PathVariable @NotNull Long storeId) {
        
        log.info("실행 계획 목록 조회 요청: storeId={}", storeId);
        
        List<ActionPlanListResponse> response = actionPlanUseCase.getActionPlans(storeId);
        
        return ResponseEntity.ok(SuccessResponse.of(response, "실행 계획 목록 조회 성공"));
    }
    
    /**
     * 실행 계획 상세 조회
     */
    @Operation(summary = "실행 계획 상세 조회", description = "실행 계획의 상세 정보를 조회합니다.")
    @GetMapping("/{planId}")
    public ResponseEntity<SuccessResponse<ActionPlanDetailResponse>> getActionPlanDetail(
            @Parameter(description = "실행 계획 ID", required = true)
            @PathVariable @NotNull Long planId) {
        
        log.info("실행 계획 상세 조회 요청: planId={}", planId);
        
        ActionPlanDetailResponse response = actionPlanUseCase.getActionPlanDetail(planId);
        
        return ResponseEntity.ok(SuccessResponse.of(response, "실행 계획 상세 조회 성공"));
    }
    
    /**
     * 실행 계획 저장
     */
    @Operation(summary = "실행 계획 저장", description = "새로운 실행 계획을 저장합니다.")
    @PostMapping
    public ResponseEntity<SuccessResponse<ActionPlanSaveResponse>> saveActionPlan(
            @Parameter(description = "실행 계획 저장 요청", required = true)
            @RequestBody @Valid ActionPlanSaveRequest request) {
        
        log.info("실행 계획 저장 요청: storeId={}, title={}", request.getStoreId(), request.getTitle());
        
        ActionPlanSaveResponse response = actionPlanUseCase.saveActionPlan(request);
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(SuccessResponse.of(response, "실행 계획 저장 성공"));
    }
    
    /**
     * 실행 계획 완료 처리
     */
    @Operation(summary = "실행 계획 완료 처리", description = "실행 계획을 완료 상태로 변경합니다.")
    @PutMapping("/{planId}/complete")
    public ResponseEntity<SuccessResponse<ActionPlanCompleteResponse>> completeActionPlan(
            @Parameter(description = "실행 계획 ID", required = true)
            @PathVariable @NotNull Long planId,
            
            @Parameter(description = "실행 계획 완료 요청", required = true)
            @RequestBody @Valid ActionPlanCompleteRequest request) {
        
        log.info("실행 계획 완료 처리 요청: planId={}", planId);
        
        ActionPlanCompleteResponse response = actionPlanUseCase.completeActionPlan(planId, request);
        
        return ResponseEntity.ok(SuccessResponse.of(response, "실행 계획 완료 처리 성공"));
    }
    
    /**
     * 실행 계획 삭제
     */
    @Operation(summary = "실행 계획 삭제", description = "실행 계획을 삭제합니다.")
    @DeleteMapping("/{planId}")
    public ResponseEntity<SuccessResponse<ActionPlanDeleteResponse>> deleteActionPlan(
            @Parameter(description = "실행 계획 ID", required = true)
            @PathVariable @NotNull Long planId) {
        
        log.info("실행 계획 삭제 요청: planId={}", planId);
        
        ActionPlanDeleteResponse response = actionPlanUseCase.deleteActionPlan(planId);
        
        return ResponseEntity.ok(SuccessResponse.of(response, "실행 계획 삭제 성공"));
    }
}
