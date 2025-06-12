package com.ktds.hi.store.infra.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 외부 플랫폼 계정 연동 요청 DTO
 */
@Getter
@Setter
@NoArgsConstructor
@Schema(description = "외부 플랫폼 계정 연동 요청")
public class ExternalConnectRequest {

    @NotBlank(message = "플랫폼 정보는 필수입니다")
    @Schema(description = "플랫폼 타입", example = "NAVER", allowableValues = {"NAVER", "KAKAO", "GOOGLE", "HIORDER"})
    private String platform;

    @NotBlank(message = "사용자명은 필수입니다")
    @Schema(description = "외부 플랫폼 사용자명", example = "store_owner@example.com")
    private String username;

    @NotBlank(message = "비밀번호는 필수입니다")
    @Schema(description = "외부 플랫폼 비밀번호", example = "password123")
    private String password;

    @Schema(description = "추가 인증 정보", example = "api_key_or_token")
    private String additionalAuth;
}