// recommend/src/main/java/com/ktds/hi/recommend/infra/controller/StoreRecommendController.java
package com.ktds.hi.recommend.infra.controller;

import com.ktds.hi.recommend.biz.usecase.in.StoreRecommendUseCase;
import com.ktds.hi.recommend.infra.dto.request.RecommendStoreRequest;
import com.ktds.hi.recommend.infra.dto.response.RecommendStoreResponse;
import com.ktds.hi.recommend.infra.dto.response.StoreDetailResponse;
import com.ktds.hi.common.dto.response.ApiResponse;
import com.ktds.hi.common.dto.response.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 매장 추천 컨트롤러
 */
@RestController
@RequestMapping("/api/recommend/stores")
@RequiredArgsConstructor
@Tag(name = "매장 추천 API", description = "사용자 취향 기반 매장 추천 관련 API")
public class StoreRecommendController {

    private final StoreRecommendUseCase storeRecommendUseCase;

    /**
     * 개인화 매장 추천
     */
    @PostMapping
    @Operation(summary = "개인화 매장 추천", description = "사용자 취향과 위치를 기반으로 매장을 추천합니다.")
    public ResponseEntity<ApiResponse<List<RecommendStoreResponse>>> recommendStores(
            Authentication authentication,
            @Valid @RequestBody RecommendStoreRequest request) {

        Long memberId = Long.valueOf(authentication.getName());
        List<RecommendStoreResponse> recommendations =
                storeRecommendUseCase.recommendPersonalizedStores(memberId, request);

        return ResponseEntity.ok(ApiResponse.success(recommendations));
    }

    /**
     * 위치 기반 매장 추천
     */
    @GetMapping("/nearby")
    @Operation(summary = "주변 매장 추천", description = "현재 위치 기반으로 주변 매장을 추천합니다.")
    public ResponseEntity<ApiResponse<PageResponse<RecommendStoreResponse>>> recommendNearbyStores(
            @RequestParam Double latitude,
            @RequestParam Double longitude,
            @RequestParam(defaultValue = "5000") Integer radius,
            @RequestParam(required = false) String category,
            @PageableDefault(size = 20) Pageable pageable) {

        PageResponse<RecommendStoreResponse> recommendations =
                storeRecommendUseCase.recommendStoresByLocation(latitude, longitude, radius, category, pageable);

        return ResponseEntity.ok(ApiResponse.success(recommendations));
    }

    /**
     * 인기 매장 추천
     */
    @GetMapping("/popular")
    @Operation(summary = "인기 매장 추천", description = "카테고리별 인기 매장을 추천합니다.")
    public ResponseEntity<ApiResponse<List<RecommendStoreResponse>>> recommendPopularStores(
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "10") Integer limit) {

        List<RecommendStoreResponse> recommendations =
                storeRecommendUseCase.recommendPopularStores(category, limit);

        return ResponseEntity.ok(ApiResponse.success(recommendations));
    }

    /**
     * 추천 매장 상세 조회
     */
    @GetMapping("/{storeId}")
    @Operation(summary = "추천 매장 상세 조회", description = "추천된 매장의 상세 정보를 조회합니다.")
    public ResponseEntity<ApiResponse<StoreDetailResponse>> getRecommendedStoreDetail(
            @PathVariable Long storeId,
            Authentication authentication) {

        Long memberId = authentication != null ? Long.valueOf(authentication.getName()) : null;
        StoreDetailResponse storeDetail =
                storeRecommendUseCase.getRecommendedStoreDetail(storeId, memberId);

        return ResponseEntity.ok(ApiResponse.success(storeDetail));
    }

    /**
     * 추천 클릭 로깅
     */
    @PostMapping("/{storeId}/click")
    @Operation(summary = "추천 클릭 로깅", description = "추천된 매장 클릭을 로깅합니다.")
    public ResponseEntity<ApiResponse<Void>> logRecommendClick(
            @PathVariable Long storeId,
            Authentication authentication) {

        Long memberId = Long.valueOf(authentication.getName());
        storeRecommendUseCase.logRecommendClick(memberId, storeId);

        return ResponseEntity.ok(ApiResponse.success());
    }
}