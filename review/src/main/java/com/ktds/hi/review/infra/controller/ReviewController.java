package com.ktds.hi.review.infra.controller;

import com.ktds.hi.review.biz.usecase.in.*;
import com.ktds.hi.review.biz.domain.ReactionType;
import com.ktds.hi.review.infra.dto.request.ReviewCreateRequest;
import com.ktds.hi.review.infra.dto.response.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 리뷰 관리 컨트롤러 클래스
 * 리뷰 생성, 조회, 삭제 및 반응 관리 API를 제공
 */
@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
@Tag(name = "리뷰 관리 API", description = "리뷰 작성, 조회, 삭제 및 반응 관리 관련 API")
public class ReviewController {
    
    private final CreateReviewUseCase createReviewUseCase;
    private final DeleteReviewUseCase deleteReviewUseCase;
    private final GetReviewUseCase getReviewUseCase;
    private final ManageReviewReactionUseCase manageReviewReactionUseCase;
    
    /**
     * 리뷰 작성 API
     */
    @PostMapping
    @Operation(summary = "리뷰 작성", description = "새로운 리뷰를 작성합니다.")
    public ResponseEntity<ReviewCreateResponse> createReview(Authentication authentication,
                                                            @Valid @RequestBody ReviewCreateRequest request) {
        Long memberId = Long.valueOf(authentication.getName());
        ReviewCreateResponse response = createReviewUseCase.createReview(memberId, request);
        return ResponseEntity.ok(response);
    }
    
    /**
     * 매장 리뷰 목록 조회 API
     */
    @GetMapping("/stores/{storeId}")
    @Operation(summary = "매장 리뷰 목록 조회", description = "특정 매장의 리뷰 목록을 조회합니다.")
    public ResponseEntity<List<ReviewListResponse>> getStoreReviews(@PathVariable Long storeId,
                                                                   @RequestParam(defaultValue = "0") Integer page,
                                                                   @RequestParam(defaultValue = "20") Integer size) {
        List<ReviewListResponse> reviews = getReviewUseCase.getStoreReviews(storeId, page, size);
        return ResponseEntity.ok(reviews);
    }

    /**
     * 최근 매장 리뷰 목록 조회 API
     */
    @GetMapping("/stores/recent/{storeId}")
    @Operation(summary = "매장 최근 리뷰 목록 조회", description = "특정 매장의 최근 리뷰 목록을 조회합니다.")
    public ResponseEntity<List<ReviewListResponse>> getStoreRecentReviews(
        @PathVariable Long storeId,
        @RequestParam(defaultValue = "0") Integer page,
        @RequestParam(defaultValue = "0") Integer size,
        @RequestParam Integer days
    ) {

        List<ReviewListResponse> reviews = getReviewUseCase.getStoreRecentReviews(storeId, page, size, days);
        return ResponseEntity.ok(reviews);
    }


    
    /**
     * 리뷰 상세 조회 API
     */
    @GetMapping("/{reviewId}")
    @Operation(summary = "리뷰 상세 조회", description = "특정 리뷰의 상세 정보를 조회합니다.")
    public ResponseEntity<ReviewDetailResponse> getReviewDetail(@PathVariable Long reviewId) {
        ReviewDetailResponse review = getReviewUseCase.getReviewDetail(reviewId);
        return ResponseEntity.ok(review);
    }
    
    /**
     * 내가 작성한 리뷰 목록 조회 API
     */
    @GetMapping("/my")
    @Operation(summary = "내 리뷰 목록 조회", description = "현재 로그인한 회원이 작성한 리뷰 목록을 조회합니다.")
    public ResponseEntity<List<ReviewListResponse>> getMyReviews(Authentication authentication,
                                                                @RequestParam(defaultValue = "0") Integer page,
                                                                @RequestParam(defaultValue = "20") Integer size) {
        Long memberId = Long.valueOf(authentication.getName());
        List<ReviewListResponse> reviews = getReviewUseCase.getMyReviews(memberId, page, size);
        return ResponseEntity.ok(reviews);
    }
    
    /**
     * 리뷰 삭제 API
     */
    @DeleteMapping("/{reviewId}")
    @Operation(summary = "리뷰 삭제", description = "작성한 리뷰를 삭제합니다.")
    public ResponseEntity<ReviewDeleteResponse> deleteReview(Authentication authentication,
                                                            @PathVariable Long reviewId) {
        Long memberId = Long.valueOf(authentication.getName());
        ReviewDeleteResponse response = deleteReviewUseCase.deleteReview(reviewId, memberId);
        return ResponseEntity.ok(response);
    }
    
    /**
     * 리뷰 좋아요 API
     */
    @PostMapping("/{reviewId}/like")
    @Operation(summary = "리뷰 좋아요", description = "리뷰에 좋아요를 등록합니다.")
    public ResponseEntity<ReviewReactionResponse> likeReview(Authentication authentication,
                                                            @PathVariable Long reviewId) {
        Long memberId = Long.valueOf(authentication.getName());
        ReviewReactionResponse response = manageReviewReactionUseCase.addReaction(reviewId, memberId, ReactionType.LIKE);
        return ResponseEntity.ok(response);
    }
    
    /**
     * 리뷰 싫어요 API
     */
    @PostMapping("/{reviewId}/dislike")
    @Operation(summary = "리뷰 싫어요", description = "리뷰에 싫어요를 등록합니다.")
    public ResponseEntity<ReviewReactionResponse> dislikeReview(Authentication authentication,
                                                               @PathVariable Long reviewId) {
        Long memberId = Long.valueOf(authentication.getName());
        ReviewReactionResponse response = manageReviewReactionUseCase.addReaction(reviewId, memberId, ReactionType.DISLIKE);
        return ResponseEntity.ok(response);
    }
    
    /**
     * 리뷰 반응 제거 API
     */
    @DeleteMapping("/{reviewId}/reaction")
    @Operation(summary = "리뷰 반응 제거", description = "리뷰에 등록한 반응을 제거합니다.")
    public ResponseEntity<ReviewReactionResponse> removeReaction(Authentication authentication,
                                                                @PathVariable Long reviewId) {
        Long memberId = Long.valueOf(authentication.getName());
        ReviewReactionResponse response = manageReviewReactionUseCase.removeReaction(reviewId, memberId);
        return ResponseEntity.ok(response);
    }
}
