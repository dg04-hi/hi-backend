package com.ktds.hi.member.service;

import com.ktds.hi.common.security.JwtTokenProvider;
import com.ktds.hi.member.dto.*;
import com.ktds.hi.member.repository.entity.MemberEntity;
import com.ktds.hi.member.repository.jpa.MemberRepository;
import com.ktds.hi.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

/**
 * 인증 서비스 구현체
 * 로그인, 로그아웃, 토큰 관리 등 인증 관련 기능을 구현
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {
    
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final SmsService smsService;
    private final RedisTemplate<String, String> redisTemplate;
    
    @Override
    public TokenResponse login(LoginRequest request) {
        // 회원 조회
        MemberEntity member = memberRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new BusinessException("존재하지 않는 사용자입니다"));
        
        // 비밀번호 검증
        if (!passwordEncoder.matches(request.getPassword(), member.getPassword())) {
            throw new BusinessException("비밀번호가 일치하지 않습니다");
        }
        
        // JWT 토큰 생성
        String accessToken = jwtTokenProvider.createAccessToken(
            member.getId().toString(),
            Collections.singletonList(member.getRole())
        );
        String refreshToken = jwtTokenProvider.createRefreshToken(member.getId().toString());


        // 리프레시 토큰 Redis 저장
        redisTemplate.opsForValue().set(
                "refresh_token:" + member.getId(),
                refreshToken,
                7, TimeUnit.DAYS
        );
        
        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .memberId(member.getId())
                .role(member.getRole())
                .build();
    }
    
    @Override
    public void logout(LogoutRequest request) {
        // 리프레시 토큰에서 사용자 ID 추출
        String userId = jwtTokenProvider.getUserIdFromToken(request.getRefreshToken());

        if (userId != null) {
            // Redis에서 리프레시 토큰 삭제
            redisTemplate.delete("refresh_token:" + userId);
        }
    }
    
    @Override
    public TokenResponse refreshToken(String refreshToken) {
        // 토큰 유효성 검증
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new BusinessException("유효하지 않은 리프레시 토큰입니다");
        }
        
        Long memberId = Long.parseLong(jwtTokenProvider.getUserIdFromToken(refreshToken));
        
        // Redis에서 리프레시 토큰 확인
        String storedToken = redisTemplate.opsForValue().get("refresh_token:" + memberId);
        if (!refreshToken.equals(storedToken)) {
            throw new BusinessException("유효하지 않은 리프레시 토큰입니다");
        }
        
        // 회원 정보 조회
        MemberEntity member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException("존재하지 않는 사용자입니다"));
        
        // 새 토큰 생성
        String newAccessToken = jwtTokenProvider.createAccessToken(
            member.getId().toString(),
            Collections.singletonList(member.getRole())
        );

        String newRefreshToken = jwtTokenProvider.createRefreshToken(member.getId().toString());
        
        // 새 리프레시 토큰 Redis 저장
        redisTemplate.opsForValue().set(
                "refresh_token:" + member.getId(),
                newRefreshToken,
                7, TimeUnit.DAYS
        );
        
        return TokenResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .memberId(member.getId())
                .role(member.getRole())
                .build();
    }
    
    @Override
    public String findUserId(FindUserIdRequest request) {
        MemberEntity member = memberRepository.findByPhone(request.getPhone())
                .orElseThrow(() -> new BusinessException("해당 전화번호로 가입된 계정이 없습니다"));
        
        return member.getUsername();
    }
    
    @Override
    public void findPassword(FindUserIdRequest request) {
        MemberEntity member = memberRepository.findByPhone(request.getPhone())
                .orElseThrow(() -> new BusinessException("해당 전화번호로 가입된 계정이 없습니다"));
        
        // 임시 비밀번호 생성 및 SMS 발송
        String tempPassword = generateTempPassword();
        smsService.sendTempPassword(request.getPhone(), tempPassword);
        
        // 임시 비밀번호로 업데이트
        member.updatePassword(passwordEncoder.encode(tempPassword));
        memberRepository.save(member);
    }
    
    @Override
    public void sendSmsVerification(String phone) {
        String verificationCode = generateVerificationCode();
        
        // SMS 발송
        smsService.sendVerificationCode(phone, verificationCode);
        
        // Redis에 인증코드 저장 (5분 만료)
        redisTemplate.opsForValue().set(
                "sms_code:" + phone,
                verificationCode,
                5, TimeUnit.MINUTES
        );
    }
    
    @Override
    public boolean verifySmsCode(String phone, String code) {
        String storedCode = redisTemplate.opsForValue().get("sms_code:" + phone);
        
        if (storedCode != null && storedCode.equals(code)) {
            // 인증 성공 시 코드 삭제
            redisTemplate.delete("sms_code:" + phone);
            return true;
        }
        
        return false;
    }
    
    /**
     * 임시 비밀번호 생성
     */
    private String generateTempPassword() {
        return "temp" + System.currentTimeMillis();
    }
    
    /**
     * SMS 인증코드 생성
     */
    private String generateVerificationCode() {
        return String.valueOf((int)(Math.random() * 900000) + 100000);
    }
}
