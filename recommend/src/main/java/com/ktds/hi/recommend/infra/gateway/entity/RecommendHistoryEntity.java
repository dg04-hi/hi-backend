package com.ktds.hi.recommend.infra.gateway.entity;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ktds.hi.recommend.biz.domain.RecommendType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 추천 히스토리 엔티티 클래스
 * 데이터베이스 recommend_history 테이블과 매핑되는 JPA 엔티티
 */
@Entity
@Table(name = "recommend_history")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class RecommendHistoryEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "member_id", nullable = false)
    private Long memberId;
    
    @Column(name = "recommended_store_ids_json", columnDefinition = "TEXT")
    private String recommendedStoreIdsJson;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "recommend_type", nullable = false)
    private RecommendType recommendType;
    
    @Column(length = 500)
    private String criteria;
    
    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    /**
     * JSON 문자열을 List로 변환
     */
    public List<Long> getRecommendedStoreIdsList() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(recommendedStoreIdsJson, new TypeReference<List<Long>>() {});
        } catch (Exception e) {
            return List.of();
        }
    }
}
