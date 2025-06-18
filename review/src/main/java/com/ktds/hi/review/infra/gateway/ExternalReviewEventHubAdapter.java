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
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.HashSet;
import java.util.Set;
/**
 * Azure Event Hub 어댑터 클래스 (단순화)
 * 외부 리뷰 이벤트 수신 및 Review 테이블 저장
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ExternalReviewEventHubAdapter {

    @Qualifier("externalReviewEventConsumer")
    private final EventHubConsumerClient externalReviewEventConsumer;

    private final Set<String> processedEventIds = new HashSet<>();

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
     * 외부 리뷰 이벤트 수신 처리
     */
    /**
    private void listenToExternalReviewEvents() {
        log.info("외부 리뷰 이벤트 수신 시작");

        try {
            while (isRunning) {
                Iterable<PartitionEvent> events = externalReviewEventConsumer.receiveFromPartition(
                        "4",                          // 파티션 ID (0으로 수정)
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
    */

    //수정 코드
    /**
     * 외부 리뷰 이벤트 수신 처리
     */
    private void listenToExternalReviewEvents() {
        log.info("외부 리뷰 이벤트 수신 시작");

        try {
            while (isRunning) {
                Iterable<PartitionEvent> events = externalReviewEventConsumer.receiveFromPartition(
                        "4",                          // 파티션 ID
                        100,                          // 최대 이벤트 수
                        EventPosition.latest(),       // 🔄 latest()로 변경하여 새 데이터만 읽기
                        Duration.ofSeconds(30)        // 타임아웃
                );

                for (PartitionEvent partitionEvent : events) {
                    handleExternalReviewEventSafely(partitionEvent);
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
            for (Map<String, Object> reviewData : reviews) {
                try {
                    Review savedReview = saveExternalReview(storeId, platform, reviewData);
                    if (savedReview != null) {
                        savedCount++;
                    }
                } catch (Exception e) {
                    log.error("개별 리뷰 저장 실패: platform={}, storeId={}, error={}",
                            platform, storeId, e.getMessage());
                }
            }

            log.info("외부 리뷰 동기화 완료: platform={}, storeId={}, expected={}, saved={}",
                    platform, storeId, reviews.size(), savedCount);

        } catch (Exception e) {
            log.error("외부 리뷰 동기화 이벤트 처리 실패: storeId={}, error={}", storeId, e.getMessage(), e);
        }
    }

    /**
     * 개별 외부 리뷰 저장 (단순화)
     */
    private Review saveExternalReview(Long storeId, String platform, Map<String, Object> reviewData) {
        try {
            // ✅ 단순화된 매핑
            Review review = Review.builder()
                    .storeId(storeId)
                    .memberId(null)  // 외부 리뷰는 회원 ID 없음
                    .memberNickname(createMemberNickname(platform, reviewData))
                    .rating(extractRating(reviewData))
                    .content(extractContent(reviewData))
                    .imageUrls(new ArrayList<>()) // 외부 리뷰는 이미지 없음
                    .status(ReviewStatus.ACTIVE)
                    .likeCount(0)     // ✅ 고정값 0
                    .dislikeCount(0)
                    .build();

            // Review 테이블에 저장
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
     * 플랫폼별 회원 닉네임 생성 (카카오 API 필드명 수정)
     */
    private String createMemberNickname(String platform, Map<String, Object> reviewData) {
        String authorName = null;

        // ✅ 카카오 API 구조에 맞춰 수정
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

        // 내용이 너무 길면 자르기 (reviews 테이블 length 제한 대비)
        if (content.length() > 1900) {
            content = content.substring(0, 1900) + "...";
        }

        return content;
    }

    //추가 코드
    /**
     * 외부 리뷰 이벤트 처리 (중복 방지)
     */
    private void handleExternalReviewEventSafely(PartitionEvent partitionEvent) {
        try {
            EventData eventData = partitionEvent.getData();
            String eventBody = eventData.getBodyAsString();

            // 🔥 이벤트 고유 ID 생성 (오프셋 + 시퀀스 넘버 기반)
            String eventId = String.format("%s_%s",
                    eventData.getOffset(),
                    eventData.getSequenceNumber());

            // 이미 처리된 이벤트인지 확인
            if (processedEventIds.contains(eventId)) {
                log.debug("이미 처리된 이벤트 스킵: eventId={}", eventId);
                return;
            }

            Map<String, Object> event = objectMapper.readValue(eventBody, Map.class);
            String eventType = (String) event.get("eventType");
            Long storeId = Long.valueOf(event.get("storeId").toString());

            log.info("외부 리뷰 이벤트 수신: type={}, storeId={}, eventId={}", eventType, storeId, eventId);

            if ("EXTERNAL_REVIEW_SYNC".equals(eventType)) {
                // 기존 메서드 호출하여 리뷰 저장
                handleExternalReviewSyncEvent(storeId, event);

                // 🔥 처리 완료된 이벤트 ID 저장
                markEventAsProcessed(eventId);
                log.info("이벤트 처리 완료: eventId={}, storeId={}", eventId, storeId);

            } else {
                log.warn("알 수 없는 외부 리뷰 이벤트 타입: {}", eventType);
            }

        } catch (Exception e) {
            log.error("외부 리뷰 이벤트 처리 중 오류 발생", e);
        }
    }

    /**
     * 이벤트 처리 완료 표시 (메모리 관리 포함)
     */
    private void markEventAsProcessed(String eventId) {
        processedEventIds.add(eventId);

        // 🔥 메모리 관리: 1000개 이상 쌓이면 오래된 것들 삭제
        if (processedEventIds.size() > 1000) {
            // 앞의 500개만 삭제하고 최근 500개는 유지
            Set<String> recentIds = new HashSet<>();
            processedEventIds.stream()
                    .skip(500)
                    .forEach(recentIds::add);

            processedEventIds.clear();
            processedEventIds.addAll(recentIds);

            log.info("처리된 이벤트 ID 캐시 정리 완료: 현재 크기={}", processedEventIds.size());
        }
    }
}