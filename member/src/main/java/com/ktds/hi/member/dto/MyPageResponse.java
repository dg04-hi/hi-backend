package com.ktds.hi.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 마이페이지 응답 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "마이페이지 정보")
public class MyPageResponse {
    
    @Schema(description = "사용자명")
    private String username;
    
    @Schema(description = "닉네임")
    private String nickname;
    
    @Schema(description = "전화번호")
    private String phone;
    
    @Schema(description = "취향 태그 목록")
    private List<String> preferences;
    
    @Schema(description = "건강 정보")
    private String healthInfo;
    
    @Schema(description = "매운맛 선호도")
    private String spicyLevel;
}
