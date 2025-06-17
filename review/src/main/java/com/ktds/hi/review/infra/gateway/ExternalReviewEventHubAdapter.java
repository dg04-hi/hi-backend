package com.ktds.hi.review.infra.gateway;

import com.azure.messaging.eventhubs.EventData;
import com.azure.messaging.eventhubs.EventHubConsumerClient;
import com.azure.messaging.eventhubs.models.EventPosition;
import com.azure.messaging.eventhubs.models.PartitionEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ktds.hi.review.biz.domain.Review;
import com.ktds.hi.review.biz.domain.ReviewStatus;
import com.ktds.hi.review.biz.usecase.out.ReviewRepository;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Azure Event Hub 어댑터 클래스 (Analytics EventHubAdapter 참고)
 * 외부 리뷰 이벤트 수신 및 Review 테이블 저장
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ExternalReviewEventHubAdapter {

    @Qualifier("externalReviewEventConsumer")
    private final EventHubConsumerClient externalReviewEventConsumer;

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
     * 외부 리뷰 이벤트 수신 처리 (Analytics의 listenToReviewEvents 참고)
     */
    private void listenToExternalReviewEvents() {
        log.info("외부 리뷰 이벤트 수신 시작");

        try {
            while (isRunning) {
                Iterable<PartitionEvent> events = externalReviewEventConsumer.receiveFromPartition(
                        "0",                          // 파티션 ID
                        100,                          // 최대 이벤트 수
                        EventPosition.earliest(),     // 시작 위치
                        Duration.ofSeconds(30)        // 타임아웃
                );

                for (PartitionEvent partitionEvent : events) {
                    handleExternalReviewEvent(partitionEvent);
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
     * 외부 리뷰 이벤트 처리 (Analytics의 handleReviewEvent 참고)
     */
    private void handleExternalReviewEvent(PartitionEvent partitionEvent) {
        try {
            EventData eventData = partitionEvent.getData();
            String eventBody = eventData.getBodyAsString();

            Map<String, Object> event = objectMapper.readValue(eventBody, Map.class);
            String eventType = (String) event.get("eventType");
            Long storeId = Long.valueOf(event.get("storeId").toString());

            log.info("외부 리뷰 이벤트 수신: type={}, storeId={}", eventType, storeId);

            if ("EXTERNAL_REVIEW_CREATED".equals(eventType)) {
                handleExternalReviewCreatedEvent(storeId, event);
            } else {
                log.warn("알 수 없는 외부 리뷰 이벤트 타입: {}", eventType);
            }

        } catch (Exception e) {
            log.error("외부 리뷰 이벤트 처리 중 오류 발생", e);
        }
    }

    /**
     * 외부 리뷰 생성 이벤트 처리 - Review 테이블에 저장
     */
    private void handleExternalReviewCreatedEvent(Long storeId, Map<String, Object> event) {
        try {
            String platform = (String) event.get("platform");

            @SuppressWarnings("unchecked")
            Map<String, Object> reviewData = (Map<String, Object>) event.get("reviewData");

            if (reviewData == null) {
                log.warn("리뷰 데이터가 없습니다: platform={}, storeId={}", platform, storeId);
                return;
            }

            // Review 도메인 객체 생성
            Review review = Review.builder()
                    .storeId(storeId)
                    .memberId(null) // 외부 리뷰는 회원 ID 없음
                    .memberNickname(createMemberNickname(platform, reviewData))
                    .rating(extractRating(reviewData))
                    .content(extractContent(reviewData))
                    .imageUrls(new ArrayList<>()) // 외부 리뷰는 이미지 없음
                    .status(ReviewStatus.ACTIVE)
                    .likeCount(0)
                    .dislikeCount(0)
                    .build();

            // Review 테이블에 저장
            Review savedReview = reviewRepository.saveReview(review);

            log.info("외부 리뷰 저장 완료: reviewId={}, platform={}, storeId={}",
                    savedReview.getId(), platform, storeId);

        } catch (Exception e) {
            log.error("외부 리뷰 생성 이벤트 처리 실패: storeId={}, error={}", storeId, e.getMessage(), e);
        }
    }

    /**
     * 플랫폼별 회원 닉네임 생성
     */
    private String createMemberNickname(String platform, Map<String, Object> reviewData) {
        String authorName = (String) reviewData.get("authorName");

        if (authorName == null || authorName.trim().isEmpty()) {
            return platform + " 사용자";
        }

        return authorName + "(" + platform + ")";
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
        return content != null ? content : "";
    }
}