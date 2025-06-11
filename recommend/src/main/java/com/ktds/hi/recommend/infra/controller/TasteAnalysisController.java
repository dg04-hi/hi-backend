package com.ktds.hi.recommend.infra.controller;

import com.ktds.hi.recommend.biz.usecase.in.TasteAnalysisUseCase;
import com.ktds.hi.recommend.infra.dto.response.TasteAnalysisResponse;
import com.ktds.hi.common.dto.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * 취향 분석 컨트롤러 클래스
 * 사용자 취향 분석 관련 API를 제공
 */
@RestController
@RequestMapping("/api/recommend/taste")
@RequiredArgsConstructor
@Tag(name = "취향 분석 API", description = "사용자 취향 분석 관련 API")
public class TasteAnalysisController {
    
    private final TasteAnalysisUseCase tasteAnalysisUseCase;
    
    /**
     * 사용자 취향 분석 조회 API
     */
    @GetMapping("/analysis")
    @Operation(summary = "취향 분석 조회", description = "현재 로그인한 사용자의 취향 분석 결과를 조회합니다.")
    public ResponseEntity<TasteAnalysisResponse> getMemberTasteAnalysis(Authentication authentication) {
        Long memberId = Long.valueOf(authentication.getName());
        TasteAnalysisResponse analysis = tasteAnalysisUseCase.analyzeMemberTaste(memberId);
        return ResponseEntity.ok(analysis);
    }
    
    /**
     * 취향 프로필 업데이트 API
     */
    @PostMapping("/update")
    @Operation(summary = "취향 프로필 업데이트", description = "사용자의 리뷰 데이터를 기반으로 취향 프로필을 업데이트합니다.")
    public ResponseEntity<SuccessResponse> updateTasteProfile(Authentication authentication) {
        Long memberId = Long.valueOf(authentication.getName());
        tasteAnalysisUseCase.updateTasteProfile(memberId);
        return ResponseEntity.ok(SuccessResponse.of("취향 프로필이 업데이트되었습니다"));
    }
}
