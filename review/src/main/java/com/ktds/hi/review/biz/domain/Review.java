package com.ktds.hi.review.biz.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 리뷰 도메인 클래스
 * 리뷰의 기본 정보를 담는 도메인 객체
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Review {
    
    private Long id;
    private Long storeId;
    private Long memberId;
    private String memberNickname;
    private Integer rating;
    private String content;
    private List<String> imageUrls;
    private ReviewStatus status;
    private Integer likeCount;
    private Integer dislikeCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    /**
     * 리뷰 내용 수정
     */
    public Review updateContent(String newContent, Integer newRating) {
        return Review.builder()
                .id(this.id)
                .storeId(this.storeId)
                .memberId(this.memberId)
                .memberNickname(this.memberNickname)
                .rating(newRating)
                .content(newContent)
                .imageUrls(this.imageUrls)
                .status(this.status)
                .likeCount(this.likeCount)
                .dislikeCount(this.dislikeCount)
                .createdAt(this.createdAt)
                .updatedAt(LocalDateTime.now())
                .build();
    }
    
    /**
     * 리뷰 상태 변경
     */
    public Review updateStatus(ReviewStatus newStatus) {
        return Review.builder()
                .id(this.id)
                .storeId(this.storeId)
                .memberId(this.memberId)
                .memberNickname(this.memberNickname)
                .rating(this.rating)
                .content(this.content)
                .imageUrls(this.imageUrls)
                .status(newStatus)
                .likeCount(this.likeCount)
                .dislikeCount(this.dislikeCount)
                .createdAt(this.createdAt)
                .updatedAt(LocalDateTime.now())
                .build();
    }
    
    /**
     * 좋아요 수 업데이트
     */
    public Review updateLikeCount(Integer likeCount, Integer dislikeCount) {
        return Review.builder()
                .id(this.id)
                .storeId(this.storeId)
                .memberId(this.memberId)
                .memberNickname(this.memberNickname)
                .rating(this.rating)
                .content(this.content)
                .imageUrls(this.imageUrls)
                .status(this.status)
                .likeCount(likeCount)
                .dislikeCount(dislikeCount)
                .createdAt(this.createdAt)
                .updatedAt(this.updatedAt)
                .build();
    }
}
