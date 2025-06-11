package com.ktds.hi.member.service;

import com.ktds.hi.member.dto.*;

/**
 * 인증 서비스 인터페이스
 * 로그인, 로그아웃, 토큰 관리 등 인증 관련 기능을 정의
 */
public interface AuthService {
    
    /**
     * 로그인 처리
     */
    TokenResponse login(LoginRequest request);
    
    /**
     * 로그아웃 처리
     */
    void logout(LogoutRequest request);
    
    /**
     * 토큰 갱신
     */
    TokenResponse refreshToken(String refreshToken);
    
    /**
     * 아이디 찾기
     */
    String findUserId(FindUserIdRequest request);
    
    /**
     * 비밀번호 찾기 (SMS 발송)
     */
    void findPassword(FindUserIdRequest request);
    
    /**
     * SMS 인증번호 발송
     */
    void sendSmsVerification(String phone);
    
    /**
     * SMS 인증번호 확인
     */
    boolean verifySmsCode(String phone, String code);
}
