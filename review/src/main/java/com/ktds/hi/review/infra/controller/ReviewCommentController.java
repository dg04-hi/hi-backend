package com.ktds.hi.review.infra.controller;

import com.ktds.hi.review.biz.usecase.in.ManageReviewCommentUseCase;
import com.ktds.hi.review.infra.dto.request.ReviewCommentRequest;
import com.ktds.hi.review.infra.dto.response.ReviewCommentResponse;
import com.ktds.hi.common.dto.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 리뷰 댓글 관리 컨트롤러 클래스
 * 리뷰 댓글 작성, 조회, 삭제 API를 제공
 */
@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
@Tag(name = "리뷰 댓글 API", description = "리뷰 댓글 작성, 조회, 삭제 관련 API")
public class ReviewCommentController {
    
    private final ManageReviewCommentUseCase manageReviewCommentUseCase;
    
    /**
     * 리뷰 댓글 목록 조회 API
     */
    @GetMapping("/{reviewId}/comments")
    @Operation(summary = "리뷰 댓글 목록 조회", description = "특정 리뷰의 댓글 목록을 조회합니다.")
    public ResponseEntity<List<ReviewCommentResponse>> getReviewComments(@PathVariable Long reviewId) {
        List<ReviewCommentResponse> comments = manageReviewCommentUseCase.getReviewComments(reviewId);
        return ResponseEntity.ok(comments);
    }
    
    /**
     * 리뷰 댓글 작성 API (점주용)
     */
    @PostMapping("/{reviewId}/comments")
    @Operation(summary = "리뷰 댓글 작성", description = "점주가 리뷰에 댓글을 작성합니다.")
    public ResponseEntity<ReviewCommentResponse> createComment(Authentication authentication,
                                                              @PathVariable Long reviewId,
                                                              @Valid @RequestBody ReviewCommentRequest request) {
        Long ownerId = Long.valueOf(authentication.getName());
        ReviewCommentResponse response = manageReviewCommentUseCase.createComment(reviewId, ownerId, request);
        return ResponseEntity.ok(response);
    }
    
    /**
     * 리뷰 댓글 삭제 API
     */
    @DeleteMapping("/{reviewId}/comments/{commentId}")
    @Operation(summary = "리뷰 댓글 삭제", description = "작성한 리뷰 댓글을 삭제합니다.")
    public ResponseEntity<SuccessResponse> deleteComment(Authentication authentication,
                                                        @PathVariable Long reviewId,
                                                        @PathVariable Long commentId) {
        Long ownerId = Long.valueOf(authentication.getName());
        manageReviewCommentUseCase.deleteComment(commentId, ownerId);
        return ResponseEntity.ok(SuccessResponse.of("댓글이 삭제되었습니다"));
    }
}
