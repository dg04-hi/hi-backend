package com.ktds.hi.member.repository.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * 회원 엔티티 클래스
 * 데이터베이스 회원 테이블과 매핑되는 JPA 엔티티
 */
@Entity
@Table(name = "members")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class MemberEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false, length = 100)
    private String username;
    
    @Column(nullable = false)
    private String password;
    
    @Column(unique = true, nullable = false, length = 50)
    private String nickname;
    
    @Column(length = 20)
    private String phone;
    
    @Column(length = 20)
    @Builder.Default
    private String role = "USER";
    
    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    private LocalDateTime updatedAt;
    
    /**
     * 닉네임 변경
     */
    public void updateNickname(String newNickname) {
        this.nickname = newNickname;
    }
    
    /**
     * 아이디 변경
     */
    public void updateUsername(String newUsername) {
        this.username = newUsername;
    }
    
    /**
     * 비밀번호 변경
     */
    public void updatePassword(String newPassword) {
        this.password = newPassword;
    }
}
