package com.ktds.hi.member.service;

import com.ktds.hi.member.dto.*;

/**
 * 회원 서비스 인터페이스
 * 회원 가입, 정보 조회/수정 등 회원 관리 기능을 정의
 */
public interface MemberService {
    
    /**
     * 회원 가입
     */
    Long registerMember(SignupRequest request);
    
    /**
     * 마이페이지 정보 조회
     */
    MyPageResponse getMyPageInfo(Long memberId);
    
    /**
     * 닉네임 변경
     */
    void updateNickname(Long memberId, UpdateNicknameRequest request);
    
    /**
     * 아이디 변경
     */
    void updateUsername(Long memberId, String newUsername);
    
    /**
     * 비밀번호 변경
     */
    void updatePassword(Long memberId, String newPassword);
    
    /**
     * 아이디 중복 확인
     */
    boolean checkUsernameAvailability(String username);
    
    /**
     * 닉네임 중복 확인
     */
    boolean checkNicknameAvailability(String nickname);
}
