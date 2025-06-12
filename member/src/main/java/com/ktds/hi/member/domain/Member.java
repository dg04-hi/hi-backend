package com.ktds.hi.member.domain;

import jakarta.persistence.EntityListeners;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * 회원 도메인 클래스
 * 회원의 기본 정보를 담는 도메인 객체
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Member {
    
    private Long id;
    private String username;
    private String password;
    private String nickname;
    private String phone;
    private String role;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    /**
     * 닉네임 변경
     */
    public Member updateNickname(String newNickname) {
        return Member.builder()
                .id(this.id)
                .username(this.username)
                .password(this.password)
                .nickname(newNickname)
                .phone(this.phone)
                .role(this.role)
                .createdAt(this.createdAt)
                .updatedAt(LocalDateTime.now())
                .build();
    }
    
    /**
     * 아이디 변경
     */
    public Member updateUsername(String newUsername) {
        return Member.builder()
                .id(this.id)
                .username(newUsername)
                .password(this.password)
                .nickname(this.nickname)
                .phone(this.phone)
                .role(this.role)
                .createdAt(this.createdAt)
                .updatedAt(LocalDateTime.now())
                .build();
    }
    
    /**
     * 비밀번호 변경
     */
    public Member updatePassword(String newPassword) {
        return Member.builder()
                .id(this.id)
                .username(this.username)
                .password(newPassword)
                .nickname(this.nickname)
                .phone(this.phone)
                .role(this.role)
                .createdAt(this.createdAt)
                .updatedAt(LocalDateTime.now())
                .build();
    }
}
