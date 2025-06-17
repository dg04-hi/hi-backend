package com.ktds.hi.review.infra.gateway.entity;

import com.ktds.hi.review.biz.domain.ReviewStatus;
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
 * 리뷰 엔티티 클래스
 * 데이터베이스 reviews 테이블과 매핑되는 JPA 엔티티
 */
@Entity
@Table(name = "reviews")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class ReviewEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "store_id", nullable = false)
    private Long storeId;
    
    @Column(name = "member_id", nullable = true)
    private Long memberId;
    
    @Column(name = "member_nickname", nullable = false, length = 50)
    private String memberNickname;
    
    @Column(nullable = false)
    private Integer rating;
    
    @Column(nullable = false, length = 1000)
    private String content;
    
    @ElementCollection
    @CollectionTable(name = "review_images", 
                    joinColumns = @JoinColumn(name = "review_id"))
    @Column(name = "image_url")
    private List<String> imageUrls;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private ReviewStatus status = ReviewStatus.ACTIVE;
    
    @Column(name = "like_count")
    @Builder.Default
    private Integer likeCount = 0;
    
    @Column(name = "dislike_count")
    @Builder.Default
    private Integer dislikeCount = 0;
    
    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
