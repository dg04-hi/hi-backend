package com.ktds.hi.analytics.infra.controller;

import com.ktds.hi.analytics.biz.usecase.in.AnalyticsUseCase;
import com.ktds.hi.analytics.infra.dto.*;
import com.ktds.hi.common.dto.ErrorResponse;
import com.ktds.hi.common.dto.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.util.List;

/**
 * 분석 서비스 컨트롤러 클래스
 * 매장 분석, AI 피드백, 통계 조회 API를 제공
 */
@Slf4j
@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
@Tag(name = "Analytics API", description = "매장 분석 및 AI 피드백 API")
public class AnalyticsController {
    
    private final AnalyticsUseCase analyticsUseCase;
    
    /**
     * 매장 분석 데이터 조회
     */
    @Operation(summary = "매장 분석 데이터 조회", description = "매장의 전반적인 분석 데이터를 조회합니다.")
    @GetMapping("/stores/{storeId}")
    public ResponseEntity<SuccessResponse<StoreAnalyticsResponse>> getStoreAnalytics(
            @Parameter(description = "매장 ID", required = true)
            @PathVariable @NotNull Long storeId) {
        
        log.info("매장 분석 데이터 조회 요청: storeId={}", storeId);
        
        StoreAnalyticsResponse response = analyticsUseCase.getStoreAnalytics(storeId);
        
        return ResponseEntity.ok(SuccessResponse.of(response, "매장 분석 데이터 조회 성공"));
    }
    
    /**
     * AI 피드백 상세 조회
     */
    @Operation(summary = "AI 피드백 상세 조회", description = "매장의 AI 피드백 상세 정보를 조회합니다.")
    @GetMapping("/stores/{storeId}/ai-feedback")
    public ResponseEntity<SuccessResponse<AiFeedbackDetailResponse>> getAIFeedbackDetail(
            @Parameter(description = "매장 ID", required = true)
            @PathVariable @NotNull Long storeId) {
        
        log.info("AI 피드백 상세 조회 요청: storeId={}", storeId);
        
        AiFeedbackDetailResponse response = analyticsUseCase.getAIFeedbackDetail(storeId);
        
        return ResponseEntity.ok(SuccessResponse.of(response, "AI 피드백 상세 조회 성공"));
    }
    
    /**
     * 매장 통계 조회
     */
    @Operation(summary = "매장 통계 조회", description = "기간별 매장 주문 통계를 조회합니다.")
    @GetMapping("/stores/{storeId}/statistics")
    public ResponseEntity<SuccessResponse<StoreStatisticsResponse>> getStoreStatistics(
            @Parameter(description = "매장 ID", required = true)
            @PathVariable @NotNull Long storeId,
            
            @Parameter(description = "시작 날짜 (YYYY-MM-DD)", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            
            @Parameter(description = "종료 날짜 (YYYY-MM-DD)", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        log.info("매장 통계 조회 요청: storeId={}, period={} ~ {}", storeId, startDate, endDate);
        
        StoreStatisticsResponse response = analyticsUseCase.getStoreStatistics(storeId, startDate, endDate);
        
        return ResponseEntity.ok(SuccessResponse.of(response, "매장 통계 조회 성공"));
    }
    
    /**
     * AI 피드백 요약 조회
     */
    @Operation(summary = "AI 피드백 요약 조회", description = "매장의 AI 피드백 요약 정보를 조회합니다.")
    @GetMapping("/stores/{storeId}/ai-feedback/summary")
    public ResponseEntity<SuccessResponse<AiFeedbackSummaryResponse>> getAIFeedbackSummary(
            @Parameter(description = "매장 ID", required = true)
            @PathVariable @NotNull Long storeId) {
        
        log.info("AI 피드백 요약 조회 요청: storeId={}", storeId);
        
        AiFeedbackSummaryResponse response = analyticsUseCase.getAIFeedbackSummary(storeId);
        
        return ResponseEntity.ok(SuccessResponse.of(response, "AI 피드백 요약 조회 성공"));
    }
    
    /**
     * 리뷰 분석 조회
     */
    @Operation(summary = "리뷰 분석 조회", description = "매장의 리뷰 감정 분석 결과를 조회합니다.")
    @GetMapping("/stores/{storeId}/review-analysis")
    public ResponseEntity<SuccessResponse<ReviewAnalysisResponse>> getReviewAnalysis(
            @Parameter(description = "매장 ID", required = true)
            @PathVariable @NotNull Long storeId) {
        
        log.info("리뷰 분석 조회 요청: storeId={}", storeId);
        
        ReviewAnalysisResponse response = analyticsUseCase.getReviewAnalysis(storeId);
        
        return ResponseEntity.ok(SuccessResponse.of(response, "리뷰 분석 조회 성공"));
    }


    /**
     * AI 리뷰 분석 및 실행계획 생성
     */
    @Operation(summary = "AI 리뷰 분석", description = "매장 리뷰를 AI로 분석하고 실행계획을 생성합니다.")
    @PostMapping("/stores/{storeId}/ai-analysis")
    public ResponseEntity<SuccessResponse<AiAnalysisResponse>> generateAIAnalysis(
        @Parameter(description = "매장 ID", required = true)
        @PathVariable @NotNull Long storeId,

        @Parameter(description = "분석 요청 정보")
        @RequestBody(required = false) @Valid AiAnalysisRequest request) {

        log.info("AI 리뷰 분석 요청: storeId={}", storeId);

        if (request == null) {
            request = AiAnalysisRequest.builder().build();
        }

        AiAnalysisResponse response = analyticsUseCase.generateAIAnalysis(storeId, request);

        return ResponseEntity.ok(SuccessResponse.of(response, "AI 분석 완료"));
    }

    /**
     * AI 피드백 기반 실행계획 생성
     */
    @Operation(summary = "실행계획 생성", description = "AI 피드백을 기반으로 실행계획을 생성합니다.")
    @PostMapping("/ai-feedback/{feedbackId}/action-plans")
    public ResponseEntity<SuccessResponse<Void>> generateActionPlans(
        @Parameter(description = "AI 피드백 ID", required = true)
        @PathVariable @NotNull Long feedbackId,
        @RequestBody ActionPlanCreateRequest request) {


        // validation 체크
        if (request.getActionPlanSelect() == null || request.getActionPlanSelect().isEmpty()) {
            throw new IllegalArgumentException("실행계획을 생성하려면 개선포인트를 선택해주세요.");
        }

        List<String> actionPlans = analyticsUseCase.generateActionPlansFromFeedback(request,feedbackId);

        return ResponseEntity.ok(SuccessResponse.of("실행계획 생성 완료"));
    }
}
