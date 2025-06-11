package com.ktds.hi.review.biz.service;

import com.ktds.hi.review.biz.usecase.in.ManageReviewCommentUseCase;
import com.ktds.hi.review.biz.usecase.out.ReviewCommentRepository;
import com.ktds.hi.review.biz.usecase.out.ReviewRepository;
import com.ktds.hi.review.biz.domain.ReviewComment;
import com.ktds.hi.review.biz.domain.Review;
import com.ktds.hi.review.infra.dto.request.ReviewCommentRequest;
import com.ktds.hi.review.infra.dto.response.ReviewCommentResponse;
import com.ktds.hi.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 리뷰 댓글 인터랙터 클래스
 * 리뷰 댓글 작성, 조회, 삭제 기능을 구현
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ReviewCommentInteractor implements ManageReviewCommentUseCase {
    
    private final ReviewCommentRepository commentRepository;
    private final ReviewRepository reviewRepository;
    
    @Override
    @Transactional(readOnly = true)
    public List<ReviewCommentResponse> getReviewComments(Long reviewId) {
        List<ReviewComment> comments = commentRepository.findCommentsByReviewId(reviewId);
        
        return comments.stream()
                .map(comment -> ReviewCommentResponse.builder()
                        .commentId(comment.getId())
                        .ownerNickname(comment.getOwnerNickname())
                        .content(comment.getContent())
                        .createdAt(comment.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }
    
    @Override
    public ReviewCommentResponse createComment(Long reviewId, Long ownerId, ReviewCommentRequest request) {
        // 리뷰 존재 확인
        Review review = reviewRepository.findReviewById(reviewId)
                .orElseThrow(() -> new BusinessException("존재하지 않는 리뷰입니다"));
        
        // 댓글 생성
        ReviewComment comment = ReviewComment.builder()
                .reviewId(reviewId)
                .ownerId(ownerId)
                .ownerNickname("점주" + ownerId) // TODO: 점주 서비스에서 닉네임 조회
                .content(request.getContent())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
        ReviewComment savedComment = commentRepository.saveComment(comment);
        
        log.info("리뷰 댓글 생성 완료: commentId={}, reviewId={}, ownerId={}", 
                savedComment.getId(), reviewId, ownerId);
        
        return ReviewCommentResponse.builder()
                .commentId(savedComment.getId())
                .ownerNickname(savedComment.getOwnerNickname())
                .content(savedComment.getContent())
                .createdAt(savedComment.getCreatedAt())
                .build();
    }
    
    @Override
    public void deleteComment(Long commentId, Long ownerId) {
        ReviewComment comment = commentRepository.findCommentByIdAndOwnerId(commentId, ownerId)
                .orElseThrow(() -> new BusinessException("댓글을 찾을 수 없거나 권한이 없습니다"));
        
        commentRepository.deleteComment(commentId);
        
        log.info("리뷰 댓글 삭제 완료: commentId={}, ownerId={}", commentId, ownerId);
    }
}
