package com.ktds.hi.store.infra.controller;

import com.ktds.hi.store.biz.usecase.in.TagUseCase;
import com.ktds.hi.store.infra.dto.response.AllTagResponse;
import com.ktds.hi.store.infra.dto.response.TopClickedTagResponse;
import com.ktds.hi.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 태그 컨트롤러 클래스
 * 태그 관련 API 엔드포인트를 제공
 *
 * @author 하이오더 개발팀
 * @version 1.0.0
 */
@RestController
@RequestMapping("/api/stores/tags")
@RequiredArgsConstructor
@Tag(name = "태그 관리 API", description = "매장 태그 조회 및 통계 관련 API")
public class TagController {

    private final TagUseCase tagUseCase;

    /**
     * 모든 활성화된 태그 목록 조회 API
     */
    @GetMapping
    @Operation(summary = "모든 태그 조회", description = "활성화된 모든 태그 목록을 조회합니다.")
    public ResponseEntity<ApiResponse<List<AllTagResponse>>> getAllTags() {

        List<AllTagResponse> tags = tagUseCase.getAllTags();

        return ResponseEntity.ok(ApiResponse.success(tags));
    }

    /**
     * 가장 많이 클릭된 상위 5개 태그 조회 API
     */
    @GetMapping("/top-clicked")
    @Operation(summary = "인기 태그 조회", description = "가장 많이 클릭된 상위 5개 태그를 조회합니다.")
    public ResponseEntity<ApiResponse<List<TopClickedTagResponse>>> getTopClickedTags() {

        List<TopClickedTagResponse> topTags = tagUseCase.getTopClickedTags();

        return ResponseEntity.ok(ApiResponse.success(topTags));
    }

    /**
     * 태그 클릭 이벤트 기록 API
     */
    @PostMapping("/{tagId}/click")
    @Operation(summary = "태그 클릭 기록", description = "태그 클릭 이벤트를 기록하고 클릭 수를 증가시킵니다.")
    public ResponseEntity<ApiResponse<Void>> recordTagClick(@PathVariable Long tagId) {

        tagUseCase.recordTagClick(tagId);

        return ResponseEntity.ok(ApiResponse.success());
    }
}