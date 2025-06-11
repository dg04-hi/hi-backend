package com.ktds.hi.member.controller;

import com.ktds.hi.member.dto.PreferenceRequest;
import com.ktds.hi.member.dto.TasteTagResponse;
import com.ktds.hi.member.domain.TagType;
import com.ktds.hi.member.service.PreferenceService;
import com.ktds.hi.common.dto.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 취향 관리 컨트롤러 클래스
 * 취향 정보 등록/수정 및 태그 관리 API를 제공
 */
@RestController
@RequestMapping("/api/members/preferences")
@RequiredArgsConstructor
@Tag(name = "취향 관리 API", description = "회원 취향 정보 등록/수정 및 태그 관리 관련 API")
public class PreferenceController {
    
    private final PreferenceService preferenceService;
    
    /**
     * 취향 정보 등록/수정 API
     */
    @PostMapping
    @Operation(summary = "취향 정보 등록", description = "회원의 취향 정보를 등록하거나 수정합니다.")
    public ResponseEntity<SuccessResponse> savePreference(Authentication authentication,
                                                         @Valid @RequestBody PreferenceRequest request) {
        Long memberId = Long.valueOf(authentication.getName());
        preferenceService.savePreference(memberId, request);
        return ResponseEntity.ok(SuccessResponse.of("취향 정보가 저장되었습니다"));
    }
    
    /**
     * 사용 가능한 취향 태그 목록 조회 API
     */
    @GetMapping("/tags")
    @Operation(summary = "취향 태그 목록 조회", description = "사용 가능한 모든 취향 태그 목록을 조회합니다.")
    public ResponseEntity<List<TasteTagResponse>> getAvailableTags() {
        List<TasteTagResponse> tags = preferenceService.getAvailableTags();
        return ResponseEntity.ok(tags);
    }
    
    /**
     * 태그 유형별 태그 목록 조회 API
     */
    @GetMapping("/tags/by-type")
    @Operation(summary = "유형별 태그 목록 조회", description = "특정 유형의 취향 태그 목록을 조회합니다.")
    public ResponseEntity<List<TasteTagResponse>> getTagsByType(@RequestParam TagType tagType) {
        List<TasteTagResponse> tags = preferenceService.getTagsByType(tagType);
        return ResponseEntity.ok(tags);
    }
}

