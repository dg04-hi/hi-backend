package com.ktds.hi.recommend.infra.controller;

import com.ktds.hi.recommend.biz.usecase.in.TasteAnalysisUseCase;
import com.ktds.hi.recommend.infra.dto.request.TasteUpdateRequest;
import com.ktds.hi.recommend.infra.dto.response.TasteAnalysisResponse;
import com.ktds.hi.recommend.infra.dto.response.PreferenceTagResponse;
import com.ktds.hi.common.dto.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 취향 분석 컨트롤러
 */
@RestController
@RequestMapping("/api/recommend/taste")
@RequiredArgsConstructor
@Tag(name = "취향 분석 API", description = "사용자 취향 분석 관련 API")
public class TasteAnalysisController {

    private final TasteAnalysisUseCase tasteAnalysisUseCase;

    /**
     * 사용자 취향 분석 조회
     */
    @GetMapping("/analysis")
    @Operation(summary = "취향 분석 조회", description = "현재 로그인한 사용자의 취향 분석 결과를 조회합니다.")
    public ResponseEntity<ApiResponse<TasteAnalysisResponse>> getMemberTasteAnalysis(
            Authentication authentication) {

        Long memberId = Long.valueOf(authentication.getName());
        TasteAnalysisResponse analysis = tasteAnalysisUseCase.analyzeMemberTaste(memberId);

        return ResponseEntity.ok(ApiResponse.success(analysis));
    }

    /**
     * 취향 프로필 업데이트
     */
    @PostMapping("/update")
    @Operation(summary = "취향 프로필 업데이트", description = "사용자의 리뷰 데이터를 기반으로 취향 프로필을 업데이트합니다.")
    public ResponseEntity<ApiResponse<Void>> updateTasteProfile(
            Authentication authentication,
            @Valid @RequestBody TasteUpdateRequest request) {

        Long memberId = Long.valueOf(authentication.getName());
        tasteAnalysisUseCase.updateTasteProfile(memberId, request);

        return ResponseEntity.ok(ApiResponse.success("취향 프로필이 업데이트되었습니다"));
    }

    /**
     * 가용한 취향 태그 조회
     */
    @GetMapping("/tags")
    @Operation(summary = "취향 태그 목록 조회", description = "선택 가능한 취향 태그 목록을 조회합니다.")
    public ResponseEntity<ApiResponse<List<PreferenceTagResponse>>> getAvailablePreferenceTags() {

        List<PreferenceTagResponse> tags = tasteAnalysisUseCase.getAvailablePreferenceTags();

        return ResponseEntity.ok(ApiResponse.success(tags));
    }
}