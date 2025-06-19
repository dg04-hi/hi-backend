package com.ktds.hi.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 회원가입 요청 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "회원가입 요청")
public class SignupRequest {
    
    @NotBlank(message = "사용자명은 필수입니다")
    @Schema(description = "사용자명", example = "test@example.com")
    private String username;
    
    @NotBlank(message = "비밀번호는 필수입니다")
    @Size(min = 8, message = "비밀번호는 8자 이상이어야 합니다")
    @Schema(description = "비밀번호", example = "password123")
    private String password;
    
    @NotBlank(message = "닉네임은 필수입니다")
    @Size(min = 2, max = 20, message = "닉네임은 2-20자 사이여야 합니다")
    @Schema(description = "닉네임", example = "홍길동")
    private String nickname;
    
    @Pattern(regexp = "^010-\\d{4}-\\d{4}$", message = "전화번호 형식이 올바르지 않습니다")
    @Schema(description = "전화번호", example = "010-1234-5678")
    private String phone;


    @NotBlank(message = "역할분류는 필수입니다")
    @Size(min = 2, max = 20, message = "역할분류는 2-20자 사이여야 합니다")
    @Schema(description = "역할", example = "OWNER")
    private String role;
}
