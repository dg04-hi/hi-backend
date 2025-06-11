package com.ktds.hi.recommend.infra.gateway.entity;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ktds.hi.recommend.biz.domain.TasteCategory;
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
import java.util.Map;

/**
 * 취향 프로필 엔티티 클래스
 * 데이터베이스 taste_profiles 테이블과 매핑되는 JPA 엔티티
 */
@Entity
@Table(name = "taste_profiles")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class TasteProfileEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "member_id", nullable = false, unique = true)
    private Long memberId;
    
    @Column(name = "preferred_categories_json", columnDefinition = "TEXT")
    private String preferredCategoriesJson;
    
    @Column(name = "category_scores_json", columnDefinition = "TEXT")
    private String categoryScoresJson;
    
    @Column(name = "preferred_tags_json", columnDefinition = "TEXT")
    private String preferredTagsJson;
    
    @Column(name = "behavior_patterns_json", columnDefinition = "TEXT")
    private String behaviorPatternsJson;
    
    @Column(name = "price_preference")
    private Double pricePreference;
    
    @Column(name = "distance_preference")
    private Double distancePreference;
    
    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    /**
     * JSON 문자열을 객체로 변환하는 메서드들
     */
    public List<TasteCategory> getPreferredCategoriesList() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(preferredCategoriesJson, new TypeReference<List<TasteCategory>>() {});
        } catch (Exception e) {
            return List.of();
        }
    }
    
    public Map<String, Double> getCategoryScoresMap() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(categoryScoresJson, new TypeReference<Map<String, Double>>() {});
        } catch (Exception e) {
            return Map.of();
        }
    }
    
    public List<String> getPreferredTagsList() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(preferredTagsJson, new TypeReference<List<String>>() {});
        } catch (Exception e) {
            return List.of();
        }
    }
    
    public Map<String, Object> getBehaviorPatternsMap() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(behaviorPatternsJson, new TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            return Map.of();
        }
    }
}
