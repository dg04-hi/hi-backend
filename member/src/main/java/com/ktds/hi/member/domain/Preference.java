package com.ktds.hi.member.domain;

import jakarta.persistence.EntityListeners;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * 취향 정보 도메인 클래스
 * 회원의 음식 취향 및 건강 정보를 담는 도메인 객체
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Preference {
    
    private Long id;
    private Long memberId;
    private List<String> tags;
    private String healthInfo;
    private String spicyLevel;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    /**
     * 취향 정보 업데이트
     */
    public Preference updatePreference(List<String> newTags, String newHealthInfo, String newSpicyLevel) {
        return Preference.builder()
                .id(this.id)
                .memberId(this.memberId)
                .tags(newTags)
                .healthInfo(newHealthInfo)
                .spicyLevel(newSpicyLevel)
                .createdAt(this.createdAt)
                .updatedAt(LocalDateTime.now())
                .build();
    }
}
