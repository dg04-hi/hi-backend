package com.ktds.hi.member.repository.entity;

import com.ktds.hi.member.domain.TagType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 취향 정보 엔티티 클래스
 * 데이터베이스 preferences 테이블과 매핑되는 JPA 엔티티
 */
@Entity
@Table(name = "preferences")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class PreferenceEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "member_id", nullable = false)
    private Long memberId;
    
    @ElementCollection
    @CollectionTable(name = "preference_tags", 
                    joinColumns = @JoinColumn(name = "preference_id"))
    @Column(name = "tag")
    private List<String> tags;
    
    @Column(name = "health_info", length = 500)
    private String healthInfo;
    
    @Column(name = "spicy_level", length = 20)
    private String spicyLevel;
    
    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    private LocalDateTime updatedAt;
    
    /**
     * 취향 정보 업데이트
     */
    public void updatePreference(List<String> newTags, String newHealthInfo, String newSpicyLevel) {
        this.tags = newTags;
        this.healthInfo = newHealthInfo;
        this.spicyLevel = newSpicyLevel;
    }
}
