package com.ktds.hi.member.controller;

import com.ktds.hi.member.dto.*;
import com.ktds.hi.member.service.MemberService;
import com.ktds.hi.common.dto.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * 회원 컨트롤러 클래스
 * 회원 가입, 정보 조회/수정 등 회원 관리 API를 제공
 */
@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
@Tag(name = "회원 관리 API", description = "회원 가입, 정보 조회/수정 등 회원 관리 관련 API")
public class MemberController {
    
    private final MemberService memberService;
    
    /**
     * 회원 가입 API
     */
    @PostMapping("/register")
    @Operation(summary = "회원 가입", description = "새로운 회원을 등록합니다.")
    public ResponseEntity<SuccessResponse> registerMember(@Valid @RequestBody SignupRequest request) {
        Long memberId = memberService.registerMember(request);
        return ResponseEntity.ok(SuccessResponse.of("회원 가입이 완료되었습니다. 회원ID: " + memberId));
    }
    
    /**
     * 마이페이지 정보 조회 API
     */
    @GetMapping("/profile")
    @Operation(summary = "마이페이지 조회", description = "현재 로그인한 회원의 정보를 조회합니다.")
    public ResponseEntity<MyPageResponse> getMyPageInfo(Authentication authentication) {
        Long memberId = Long.valueOf(authentication.getName());
        MyPageResponse response = memberService.getMyPageInfo(memberId);
        return ResponseEntity.ok(response);
    }
    
    /**
     * 닉네임 변경 API
     */
    @PutMapping("/nickname")
    @Operation(summary = "닉네임 변경", description = "현재 로그인한 회원의 닉네임을 변경합니다.")
    public ResponseEntity<SuccessResponse> updateNickname(Authentication authentication,
                                                         @Valid @RequestBody UpdateNicknameRequest request) {
        Long memberId = Long.valueOf(authentication.getName());
        memberService.updateNickname(memberId, request);
        return ResponseEntity.ok(SuccessResponse.of("닉네임이 변경되었습니다"));
    }
    
    /**
     * 아이디 변경 API
     */
    @PutMapping("/username")
    @Operation(summary = "아이디 변경", description = "현재 로그인한 회원의 아이디를 변경합니다.")
    public ResponseEntity<SuccessResponse> updateUsername(Authentication authentication,
                                                         @RequestParam String username) {
        Long memberId = Long.valueOf(authentication.getName());
        memberService.updateUsername(memberId, username);
        return ResponseEntity.ok(SuccessResponse.of("아이디가 변경되었습니다"));
    }
    
    /**
     * 비밀번호 변경 API
     */
    @PutMapping("/password")
    @Operation(summary = "비밀번호 변경", description = "현재 로그인한 회원의 비밀번호를 변경합니다.")
    public ResponseEntity<SuccessResponse> updatePassword(Authentication authentication,
                                                         @RequestParam String password) {
        Long memberId = Long.valueOf(authentication.getName());
        memberService.updatePassword(memberId, password);
        return ResponseEntity.ok(SuccessResponse.of("비밀번호가 변경되었습니다"));
    }
    
    /**
     * 아이디 중복 확인 API
     */
    @GetMapping("/check-username")
    @Operation(summary = "아이디 중복 확인", description = "사용 가능한 아이디인지 확인합니다.")
    public ResponseEntity<SuccessResponse> checkUsernameAvailability(@RequestParam String username) {
        boolean isAvailable = memberService.checkUsernameAvailability(username);
        String message = isAvailable ? "사용 가능한 아이디입니다" : "이미 사용 중인 아이디입니다";
        return ResponseEntity.ok(SuccessResponse.of(message));
    }
    
    /**
     * 닉네임 중복 확인 API
     */
    @GetMapping("/check-nickname")
    @Operation(summary = "닉네임 중복 확인", description = "사용 가능한 닉네임인지 확인합니다.")
    public ResponseEntity<SuccessResponse> checkNicknameAvailability(@RequestParam String nickname) {
        boolean isAvailable = memberService.checkNicknameAvailability(nickname);
        String message = isAvailable ? "사용 가능한 닉네임입니다" : "이미 사용 중인 닉네임입니다";
        return ResponseEntity.ok(SuccessResponse.of(message));
    }
}
