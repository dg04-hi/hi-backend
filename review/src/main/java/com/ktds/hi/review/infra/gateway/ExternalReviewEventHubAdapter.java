package com.ktds.hi.review.infra.gateway;

import com.azure.messaging.eventhubs.EventData;
import com.azure.messaging.eventhubs.EventHubConsumerClient;
import com.azure.messaging.eventhubs.models.EventPosition;
import com.azure.messaging.eventhubs.models.PartitionEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ktds.hi.review.biz.domain.Review;
import com.ktds.hi.review.biz.domain.ReviewStatus;
import com.ktds.hi.review.biz.usecase.out.ReviewRepository;
import com.ktds.hi.review.infra.gateway.repository.ReviewJpaRepository;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Azure Event Hub 어댑터 클래스 - 수정된 버전
 * 외부 리뷰 이벤트 수신 및 Review 테이블 저장 (중복 방지)
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ExternalReviewEventHubAdapter {

    @Qualifier("externalReviewEventConsumer")
    private final EventHubConsumerClient externalReviewEventConsumer;
    private final ReviewJpaRepository reviewJpaRepository;  // ✅ 중복 체크용
    private final ObjectMapper objectMapper;
    private final ReviewRepository reviewRepository;

    private final ExecutorService executorService = Executors.newFixedThreadPool(3);
    private volatile boolean isRunning = false;

    @PostConstruct
    public void startEventListening() {
        log.info("외부 리뷰 Event Hub 리스너 시작");
        isRunning = true;

        // 외부 리뷰 이벤트 수신 시작
        executorService.submit(this::listenToExternalReviewEvents);
    }

    @PreDestroy
    public void stopEventListening() {
        log.info("외부 리뷰 Event Hub 리스너 종료");
        isRunning = false;
        executorService.shutdown();
        externalReviewEventConsumer.close();
    }

    /**
     * 외부 리뷰 이벤트 수신 처리 - 모든 파티션 처리
     */
    private void listenToExternalReviewEvents() {
        log.info("외부 리뷰 이벤트 수신 시작");

        try {
            // ✅ 1. 파티션 정보 조회 (하드코딩 제거)
            String[] partitionIds = {"0", "1", "2", "3"};
            log.info("처리할 파티션: {}", String.join(", ", partitionIds));

            while (isRunning) {
                // ✅ 2. 모든 파티션 순회 처리
                for (String partitionId : partitionIds) {
                    try {
                        Iterable<PartitionEvent> events = externalReviewEventConsumer.receiveFromPartition(
                                partitionId,                  // ✅ 동적 파티션 ID
                                50,                           // 배치 크기 줄임 (성능 최적화)
                                EventPosition.latest(),       // ✅ 최신 메시지만 (중복 방지)
                                Duration.ofSeconds(10)        // 타임아웃 줄임
                        );

                        for (PartitionEvent partitionEvent : events) {
                            handleExternalReviewEvent(partitionEvent);
                        }

                    } catch (Exception e) {
                        log.error("파티션 {} 처리 중 오류: {}", partitionId, e.getMessage());
                    }
                }

                Thread.sleep(1000);
            }
        } catch (InterruptedException e) {
            log.info("외부 리뷰 이벤트 수신 중단됨");
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            log.error("외부 리뷰 이벤트 수신 중 오류 발생", e);
        }
    }

    /**
     * 외부 리뷰 이벤트 처리
     */
    private void handleExternalReviewEvent(PartitionEvent partitionEvent) {
        try {
            EventData eventData = partitionEvent.getData();
            String eventBody = eventData.getBodyAsString();

            Map<String, Object> event = objectMapper.readValue(eventBody, Map.class);
            String eventType = (String) event.get("eventType");
            Long storeId = Long.valueOf(event.get("storeId").toString());

            log.info("외부 리뷰 이벤트 수신: type={}, storeId={}", eventType, storeId);

            if ("EXTERNAL_REVIEW_SYNC".equals(eventType)) {
                handleExternalReviewSyncEvent(storeId, event);
            } else {
                log.warn("알 수 없는 외부 리뷰 이벤트 타입: {}", eventType);
            }

        } catch (Exception e) {
            log.error("외부 리뷰 이벤트 처리 중 오류 발생", e);
        }
    }

    /**
     * 외부 리뷰 동기화 이벤트 처리 - 여러 리뷰를 배치로 처리
     */
    private void handleExternalReviewSyncEvent(Long storeId, Map<String, Object> event) {
        try {
            String platform = (String) event.get("platform");
            Integer syncedCount = (Integer) event.get("syncedCount");

            // Store에서 발행하는 reviews 배열 처리
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> reviews = (List<Map<String, Object>>) event.get("reviews");

            if (reviews == null || reviews.isEmpty()) {
                log.warn("리뷰 데이터가 없습니다: platform={}, storeId={}", platform, storeId);
                return;
            }

            log.info("외부 리뷰 동기화 처리 시작: platform={}, storeId={}, count={}",
                    platform, storeId, reviews.size());

            int savedCount = 0;
            int duplicateCount = 0;

            for (Map<String, Object> reviewData : reviews) {
                try {
                    Review savedReview = saveExternalReview(storeId, platform, reviewData);
                    if (savedReview != null) {
                        savedCount++;
                    } else {
                        duplicateCount++;
                    }
                } catch (Exception e) {
                    log.error("개별 리뷰 저장 실패: platform={}, storeId={}, error={}",
                            platform, storeId, e.getMessage());
                }
            }

            log.info("외부 리뷰 동기화 완료: platform={}, storeId={}, total={}, saved={}, duplicate={}",
                    platform, storeId, reviews.size(), savedCount, duplicateCount);

        } catch (Exception e) {
            log.error("외부 리뷰 동기화 이벤트 처리 실패: storeId={}, error={}", storeId, e.getMessage(), e);
        }
    }

    /**
     * 개별 외부 리뷰 저장 - 중복 체크 포함
     */
    private Review saveExternalReview(Long storeId, String platform, Map<String, Object> reviewData) {
        try {
            // ✅ 1. 중복 체크용 고유 식별자 생성
            String externalNickname = createMemberNickname(platform, reviewData);
            String content = extractContent(reviewData);

            // ✅ 2. 중복 체크 (storeId + 닉네임 + 내용으로 중복 판단)
            boolean isDuplicate = reviewJpaRepository.existsByStoreIdAndMemberNicknameAndContent(
                    storeId, externalNickname, content);

            if (isDuplicate) {
                log.debug("중복 리뷰 스킵: storeId={}, nickname={}", storeId, externalNickname);
                return null;
            }

            // ✅ 3. 새로운 리뷰 저장
            Review review = Review.builder()
                    .storeId(storeId)
                    .memberId(null)  // 외부 리뷰는 회원 ID 없음
                    .memberNickname(externalNickname)
                    .rating(extractRating(reviewData))
                    .content(content)
                    .imageUrls(new ArrayList<>()) // 외부 리뷰는 이미지 없음
                    .status(ReviewStatus.ACTIVE)
                    .likeCount(0)
                    .dislikeCount(0)
                    .build();

            Review savedReview = reviewRepository.saveReview(review);

            log.debug("외부 리뷰 저장 완료: reviewId={}, platform={}, storeId={}, author={}",
                    savedReview.getId(), platform, storeId, savedReview.getMemberNickname());

            return savedReview;

        } catch (Exception e) {
            log.error("외부 리뷰 저장 실패: platform={}, storeId={}, error={}",
                    platform, storeId, e.getMessage(), e);
            return null;
        }
    }

    /**
     * 플랫폼별 회원 닉네임 생성
     */
    private String createMemberNickname(String platform, Map<String, Object> reviewData) {
        String authorName = null;

        // 카카오 API 구조에 맞춰 수정
        if ("KAKAO".equalsIgnoreCase(platform)) {
            authorName = (String) reviewData.get("reviewer_name");
        } else {
            // 다른 플랫폼 대비
            authorName = (String) reviewData.get("author_name");
            if (authorName == null) {
                authorName = (String) reviewData.get("authorName");
            }
        }

        if (authorName == null || authorName.trim().isEmpty()) {
            return platform.toUpperCase() + " 사용자";
        }

        return authorName + "(" + platform.toUpperCase() + ")";
    }

    /**
     * 평점 추출 (기본값: 5)
     */
    private Integer extractRating(Map<String, Object> reviewData) {
        Object rating = reviewData.get("rating");
        if (rating instanceof Number) {
            int ratingValue = ((Number) rating).intValue();
            return (ratingValue >= 1 && ratingValue <= 5) ? ratingValue : 5;
        }
        return 5;
    }

    /**
     * 리뷰 내용 추출
     */
    private String extractContent(Map<String, Object> reviewData) {
        String content = (String) reviewData.get("content");
        if (content == null || content.trim().isEmpty()) {
            return "외부 플랫폼 리뷰";
        }

        // 내용이 너무 길면 자르기
        if (content.length() > 1900) {
            content = content.substring(0, 1900) + "...";
        }

        return content;
    }
}