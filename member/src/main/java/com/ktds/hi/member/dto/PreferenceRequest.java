package com.ktds.hi.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 취향 정보 등록 요청 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "취향 정보 등록 요청")
public class PreferenceRequest {
    
    @NotEmpty(message = "취향 태그는 최소 1개 이상 선택해야 합니다")
    @Schema(description = "취향 태그 목록", example = "[\"한식\", \"매운맛\", \"저칼로리\"]")
    private List<String> tags;
    
    @Schema(description = "건강 정보", example = "당뇨 있음")
    private String healthInfo;
    
    @Schema(description = "매운맛 선호도", example = "보통")
    private String spicyLevel;
}
