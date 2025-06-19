package com.ktds.hi.member.service;

import com.ktds.hi.member.dto.*;
import com.ktds.hi.member.repository.entity.MemberEntity;
import com.ktds.hi.member.repository.entity.PreferenceEntity;
import com.ktds.hi.member.repository.jpa.MemberRepository;
import com.ktds.hi.member.repository.jpa.PreferenceRepository;
import com.ktds.hi.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

/**
 * 회원 서비스 구현체
 * 회원 가입, 정보 조회/수정 등 회원 관리 기능을 구현
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class MemberServiceImpl implements MemberService {
    
    private final MemberRepository memberRepository;
    private final PreferenceRepository preferenceRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Override
    public Long registerMember(SignupRequest request) {
        // 중복 검사
        if (memberRepository.existsByUsername(request.getUsername())) {
            throw new BusinessException("이미 사용 중인 사용자명입니다");
        }
        
        if (memberRepository.existsByNickname(request.getNickname())) {
            throw new BusinessException("이미 사용 중인 닉네임입니다");
        }
        
        // 회원 생성
        MemberEntity member = MemberEntity.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .nickname(request.getNickname())
                .phone(request.getPhone())
                .role(request.getRole())
                .build();
        
        MemberEntity savedMember = memberRepository.save(member);
        
        log.info("회원 가입 완료: memberId={}, username={}", savedMember.getId(), savedMember.getUsername());
        
        return savedMember.getId();
    }
    
    @Override
    @Transactional(readOnly = true)
    public MyPageResponse getMyPageInfo(Long memberId) {
        MemberEntity member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException("존재하지 않는 회원입니다"));
        
        PreferenceEntity preference = preferenceRepository.findByMemberId(memberId)
                .orElse(null);
        
        return MyPageResponse.builder()
                .username(member.getUsername())
                .nickname(member.getNickname())
                .phone(member.getPhone())
                .preferences(preference != null ? preference.getTags() : Collections.emptyList())
                .healthInfo(preference != null ? preference.getHealthInfo() : null)
                .spicyLevel(preference != null ? preference.getSpicyLevel() : null)
                .build();
    }
    
    @Override
    public void updateNickname(Long memberId, UpdateNicknameRequest request) {
        MemberEntity member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException("존재하지 않는 회원입니다"));
        
        // 닉네임 중복 검사
        if (memberRepository.existsByNickname(request.getNickname())) {
            throw new BusinessException("이미 사용 중인 닉네임입니다");
        }
        
        member.updateNickname(request.getNickname());
        memberRepository.save(member);
        
        log.info("닉네임 변경 완료: memberId={}, newNickname={}", memberId, request.getNickname());
    }
    
    @Override
    public void updateUsername(Long memberId, String newUsername) {
        MemberEntity member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException("존재하지 않는 회원입니다"));
        
        // 아이디 중복 검사
        if (memberRepository.existsByUsername(newUsername)) {
            throw new BusinessException("이미 사용 중인 사용자명입니다");
        }
        
        member.updateUsername(newUsername);
        memberRepository.save(member);
        
        log.info("아이디 변경 완료: memberId={}, newUsername={}", memberId, newUsername);
    }
    
    @Override
    public void updatePassword(Long memberId, String newPassword) {
        MemberEntity member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException("존재하지 않는 회원입니다"));
        
        member.updatePassword(passwordEncoder.encode(newPassword));
        memberRepository.save(member);
        
        log.info("비밀번호 변경 완료: memberId={}", memberId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean checkUsernameAvailability(String username) {
        return !memberRepository.existsByUsername(username);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean checkNicknameAvailability(String nickname) {
        return !memberRepository.existsByNickname(nickname);
    }
}
