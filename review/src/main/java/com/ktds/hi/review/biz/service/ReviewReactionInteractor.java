package com.ktds.hi.review.biz.service;

import com.ktds.hi.review.biz.usecase.in.ManageReviewReactionUseCase;
import com.ktds.hi.review.biz.usecase.out.ReviewReactionRepository;
import com.ktds.hi.review.biz.usecase.out.ReviewRepository;
import com.ktds.hi.review.biz.domain.ReviewReaction;
import com.ktds.hi.review.biz.domain.ReactionType;
import com.ktds.hi.review.biz.domain.Review;
import com.ktds.hi.review.infra.dto.response.ReviewReactionResponse;
import com.ktds.hi.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * 리뷰 반응 인터랙터 클래스
 * 리뷰 좋아요/싫어요 기능을 구현
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ReviewReactionInteractor implements ManageReviewReactionUseCase {
    
    private final ReviewReactionRepository reactionRepository;
    private final ReviewRepository reviewRepository;
    
    @Override
    public ReviewReactionResponse addReaction(Long reviewId, Long memberId, ReactionType reactionType) {
        // 리뷰 존재 확인
        Review review = reviewRepository.findReviewById(reviewId)
                .orElseThrow(() -> new BusinessException("존재하지 않는 리뷰입니다"));
        
        // 기존 반응 확인
        Optional<ReviewReaction> existingReaction = reactionRepository
                .findReactionByReviewIdAndMemberId(reviewId, memberId);
        
        if (existingReaction.isPresent()) {
            ReviewReaction reaction = existingReaction.get();
            if (reaction.getReactionType() == reactionType) {
                throw new BusinessException("이미 같은 반응을 등록했습니다");
            }
            
            // 반응 유형 변경
            ReviewReaction updatedReaction = reaction.updateReactionType(reactionType);
            reactionRepository.saveReaction(updatedReaction);
        } else {
            // 새로운 반응 생성
            ReviewReaction newReaction = ReviewReaction.builder()
                    .reviewId(reviewId)
                    .memberId(memberId)
                    .reactionType(reactionType)
                    .createdAt(LocalDateTime.now())
                    .build();
            
            reactionRepository.saveReaction(newReaction);
        }
        
        // 반응 개수 업데이트
        updateReactionCounts(reviewId);
        
        log.info("리뷰 반응 추가: reviewId={}, memberId={}, type={}", reviewId, memberId, reactionType);
        
        return ReviewReactionResponse.builder()
                .success(true)
                .message("반응이 등록되었습니다")
                .build();
    }
    
    @Override
    public ReviewReactionResponse removeReaction(Long reviewId, Long memberId) {
        ReviewReaction reaction = reactionRepository.findReactionByReviewIdAndMemberId(reviewId, memberId)
                .orElseThrow(() -> new BusinessException("등록된 반응이 없습니다"));
        
        reactionRepository.deleteReaction(reaction.getId());
        
        // 반응 개수 업데이트
        updateReactionCounts(reviewId);
        
        log.info("리뷰 반응 제거: reviewId={}, memberId={}", reviewId, memberId);
        
        return ReviewReactionResponse.builder()
                .success(true)
                .message("반응이 제거되었습니다")
                .build();
    }
    
    /**
     * 리뷰의 반응 개수 업데이트
     */
    private void updateReactionCounts(Long reviewId) {
        Long likeCount = reactionRepository.countReactionsByReviewIdAndType(reviewId, ReactionType.LIKE);
        Long dislikeCount = reactionRepository.countReactionsByReviewIdAndType(reviewId, ReactionType.DISLIKE);
        
        Review review = reviewRepository.findReviewById(reviewId)
                .orElseThrow(() -> new BusinessException("존재하지 않는 리뷰입니다"));
        
        Review updatedReview = review.updateLikeCount(likeCount.intValue(), dislikeCount.intValue());
        reviewRepository.saveReview(updatedReview);
    }
}
