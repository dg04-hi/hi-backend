package com.ktds.hi.member.controller;

import com.ktds.hi.member.dto.*;
import com.ktds.hi.member.service.AuthService;
import com.ktds.hi.common.dto.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 인증 컨트롤러 클래스
 * 로그인, 로그아웃, 토큰 갱신 등 인증 관련 API를 제공
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "인증 API", description = "로그인, 로그아웃, 토큰 관리 등 인증 관련 API")
public class AuthController {
    
    private final AuthService authService;
    
    /**
     * 로그인 API
     */
    @PostMapping("/login")
    @Operation(summary = "로그인", description = "사용자명과 비밀번호로 로그인을 수행합니다.")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
        TokenResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }
    
    /**
     * 로그아웃 API
     */
    @PostMapping("/logout")
    @Operation(summary = "로그아웃", description = "현재 로그인된 사용자를 로그아웃 처리합니다.")
    public ResponseEntity<SuccessResponse> logout(@Valid @RequestBody LogoutRequest request) {
        authService.logout(request);
        return ResponseEntity.ok(SuccessResponse.of("로그아웃이 완료되었습니다"));
    }
    
    /**
     * 토큰 갱신 API
     */
    @PostMapping("/refresh")
    @Operation(summary = "토큰 갱신", description = "리프레시 토큰을 사용해 새로운 액세스 토큰을 발급받습니다.")
    public ResponseEntity<TokenResponse> refreshToken(@RequestParam String refreshToken) {
        TokenResponse response = authService.refreshToken(refreshToken);
        return ResponseEntity.ok(response);
    }
    
    /**
     * 아이디 찾기 API
     */
    @PostMapping("/find-username")
    @Operation(summary = "아이디 찾기", description = "전화번호를 사용해 아이디를 찾습니다.")
    public ResponseEntity<SuccessResponse> findUsername(@Valid @RequestBody FindUserIdRequest request) {
        String username = authService.findUserId(request);
        return ResponseEntity.ok(SuccessResponse.of("아이디: " + username));
    }
    
    /**
     * 비밀번호 찾기 API
     */
    @PostMapping("/find-password")
    @Operation(summary = "비밀번호 찾기", description = "전화번호로 임시 비밀번호를 SMS로 발송합니다.")
    public ResponseEntity<SuccessResponse> findPassword(@Valid @RequestBody FindUserIdRequest request) {
        authService.findPassword(request);
        return ResponseEntity.ok(SuccessResponse.of("임시 비밀번호가 SMS로 발송되었습니다"));
    }
    
    /**
     * SMS 인증번호 발송 API
     */
    @PostMapping("/sms/send")
    @Operation(summary = "SMS 인증번호 발송", description = "입력한 전화번호로 인증번호를 발송합니다.")
    public ResponseEntity<SuccessResponse> sendSmsVerification(@RequestParam String phone) {
        authService.sendSmsVerification(phone);
        return ResponseEntity.ok(SuccessResponse.of("인증번호가 발송되었습니다"));
    }
    
    /**
     * SMS 인증번호 확인 API
     */
    @PostMapping("/sms/verify")
    @Operation(summary = "SMS 인증번호 확인", description = "입력한 인증번호가 올바른지 확인합니다.")
    public ResponseEntity<SuccessResponse> verifySmsCode(@RequestParam String phone, @RequestParam String code) {
        boolean isValid = authService.verifySmsCode(phone, code);
        
        if (isValid) {
            return ResponseEntity.ok(SuccessResponse.of("인증이 완료되었습니다"));
        } else {
            return ResponseEntity.badRequest().body(SuccessResponse.of("인증번호가 올바르지 않습니다"));
        }
    }
}
