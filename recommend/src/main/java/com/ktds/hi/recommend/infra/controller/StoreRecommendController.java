package com.ktds.hi.recommend.infra.controller;

import com.ktds.hi.recommend.biz.usecase.in.StoreRecommendUseCase;
import com.ktds.hi.recommend.infra.dto.request.RecommendStoreRequest;
import com.ktds.hi.recommend.infra.dto.response.RecommendStoreResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 매장 추천 컨트롤러 클래스
 * 매장 추천 관련 API를 제공
 */
@RestController
@RequestMapping("/api/recommend")
@RequiredArgsConstructor
@Tag(name = "매장 추천 API", description = "사용자 취향 기반 매장 추천 관련 API")
public class StoreRecommendController {
    
    private final StoreRecommendUseCase storeRecommendUseCase;
    
    /**
     * 사용자 취향 기반 매장 추천 API
     */
    @PostMapping("/stores")
    @Operation(summary = "매장 추천", description = "사용자 취향과 위치를 기반으로 매장을 추천합니다.")
    public ResponseEntity<List<RecommendStoreResponse>> recommendStores(Authentication authentication,
                                                                       @Valid @RequestBody RecommendStoreRequest request) {
        Long memberId = Long.valueOf(authentication.getName());
        List<RecommendStoreResponse> recommendations = storeRecommendUseCase.recommendStores(memberId, request);
        return ResponseEntity.ok(recommendations);
    }
    
    /**
     * 위치 기반 매장 추천 API
     */
    @GetMapping("/stores/nearby")
    @Operation(summary = "주변 매장 추천", description = "현재 위치 기반으로 주변 매장을 추천합니다.")
    public ResponseEntity<List<RecommendStoreResponse>> recommendNearbyStores(
            @RequestParam Double latitude,
            @RequestParam Double longitude,
            @RequestParam(defaultValue = "5000") Integer radius) {
        
        List<RecommendStoreResponse> recommendations = storeRecommendUseCase
                .recommendStoresByLocation(latitude, longitude, radius);
        return ResponseEntity.ok(recommendations);
    }
    
    /**
     * 인기 매장 추천 API
     */
    @GetMapping("/stores/popular")
    @Operation(summary = "인기 매장 추천", description = "카테고리별 인기 매장을 추천합니다.")
    public ResponseEntity<List<RecommendStoreResponse>> recommendPopularStores(
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "10") Integer limit) {
        
        List<RecommendStoreResponse> recommendations = storeRecommendUseCase
                .recommendPopularStores(category, limit);
        return ResponseEntity.ok(recommendations);
    }
}
