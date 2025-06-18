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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Set;

/**
 * Azure Event Hub 어댑터 클래스 (기존 로직 유지 + 중복 제거만 추가)
 * 외부 리뷰 이벤트 수신 및 Review 테이블 저장
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ExternalReviewEventHubAdapter {

    @Qualifier("externalReviewEventConsumer")
    private final EventHubConsumerClient externalReviewEventConsumer;
    private final ReviewJpaRepository reviewJpaRepository;
    private final ObjectMapper objectMapper;
    private final ReviewRepository reviewRepository;

    private final ExecutorService executorService = Executors.newFixedThreadPool(3);
    private volatile boolean isRunning = false;

    // 🔥 중복 방지를 위한 이벤트 ID 캐시 (Thread-safe)
    private final Set<String> processedEventIds = ConcurrentHashMap.newKeySet();

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
     * 외부 리뷰 이벤트 수신 처리 (기존 로직 유지)
     */
    private void listenToExternalReviewEvents() {
        log.info("외부 리뷰 이벤트 수신 시작");

        try {
            while (isRunning) {
                Iterable<PartitionEvent> events = externalReviewEventConsumer.receiveFromPartition(
                        "4",                          // 파티션 ID (기존과 동일)
                        100,                          // 최대 이벤트 수 (기존과 동일)
                        EventPosition.earliest(),     // 시작 위치 (기존과 동일)
                        Duration.ofSeconds(10)        // 타임아웃 (기존과 동일)
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
     * 외부 리뷰 이벤트 처리 (중복 체크 추가)
     */
    private void handleExternalReviewEvent(PartitionEvent partitionEvent) {
        try {
            EventData eventData = partitionEvent.getData();
            String eventBody = eventData.getBodyAsString();

            // 🔥 이벤트 고유 ID 생성 (중복 방지)
            String eventId = String.format("%s_%s",
                    eventData.getOffset(),
                    eventData.getSequenceNumber());

            // 🔥 이미 처리된 이벤트인지 확인
            if (isEventAlreadyProcessed(eventId)) {
                log.debug("이미 처리된 이벤트 스킵: eventId={}", eventId);
                return;
            }

            Map<String, Object> event = objectMapper.readValue(eventBody, Map.class);
            String eventType = (String) event.get("eventType");
            Long storeId = Long.valueOf(event.get("storeId").toString());

            log.info("외부 리뷰 이벤트 수신: type={}, storeId={}, eventId={}", eventType, storeId, eventId);

            if ("EXTERNAL_REVIEW_SYNC".equals(eventType)) {
                handleExternalReviewSyncEvent(storeId, event);
                // 🔥 처리 완료된 이벤트 ID 저장
                markEventAsProcessed(eventId);
            } else {
                log.warn("알 수 없는 외부 리뷰 이벤트 타입: {}", eventType);
            }

        } catch (Exception e) {
            log.error("외부 리뷰 이벤트 처리 중 오류 발생", e);
        }
    }

    /**
     * 외부 리뷰 동기화 이벤트 처리 - 여러 리뷰를 배치로 처리 (중복 체크 강화)
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
            int errorCount = 0;

            for (Map<String, Object> reviewData : reviews) {
                try {
                    // 🔥 중복 체크 포함된 저장 로직
                    Review savedReview = saveExternalReviewWithDuplicateCheck(storeId, platform, reviewData);
                    if (savedReview != null) {
                        savedCount++;
                    } else {
                        duplicateCount++;
                    }
                } catch (Exception e) {
                    errorCount++;
                    log.error("개별 리뷰 저장 실패: platform={}, storeId={}, error={}",
                            platform, storeId, e.getMessage());
                }
            }

            log.info("외부 리뷰 동기화 완료: platform={}, storeId={}, expected={}, saved={}, duplicated={}, errors={}",
                    platform, storeId, reviews.size(), savedCount, duplicateCount, errorCount);

        } catch (Exception e) {
            log.error("외부 리뷰 동기화 이벤트 처리 실패: storeId={}, error={}", storeId, e.getMessage(), e);
        }
    }

    /**
     * 🔥 중복 체크 포함된 외부 리뷰 저장
     */
    private Review saveExternalReviewWithDuplicateCheck(Long storeId, String platform, Map<String, Object> reviewData) {
        try {
            String content = extractContent(reviewData);
            String memberNickname = extractMemberNickname(reviewData);


            // 1차 중복 체크: content 기반 (전체 시스템)
            if (reviewJpaRepository.existsByContent(content)) {
                log.debug("중복 리뷰 스킵 (content 기준): content={}",
                        content.substring(0, Math.min(50, content.length())));
                return null;
            }

            // 중복이 아닌 경우 새 리뷰 저장
            Review newReview = createReviewFromExternalData(storeId, platform, reviewData);
            Review savedReview = reviewRepository.saveReview(newReview);

            log.debug("새 외부 리뷰 저장 완료: storeId={}, reviewId={}, platform={}",
                    storeId, savedReview.getId(), platform);

            return savedReview;

        } catch (Exception e) {
            log.error("외부 리뷰 저장 중 오류: storeId={}, platform={}, error={}",
                    storeId, platform, e.getMessage());
            throw e;
        }
    }

    /**
     * 외부 리뷰 데이터에서 Review 도메인 객체 생성
     */
    private Review createReviewFromExternalData(Long storeId, String platform, Map<String, Object> reviewData) {
        return Review.builder()
                .storeId(storeId)
                .memberId(null) // 외부 리뷰는 내부 회원 ID 없음
                .memberNickname(extractMemberNickname(reviewData))
                .rating(extractRating(reviewData))
                .content(extractContent(reviewData))
                .imageUrls(extractImageUrls(reviewData))
                .status(ReviewStatus.ACTIVE)
                .likeCount(0)
                .dislikeCount(0)
                .build();
    }

    /**
     * 회원 닉네임 추출
     */
    private String extractMemberNickname(Map<String, Object> reviewData) {
        String nickname = (String) reviewData.get("memberNickname");
        if (nickname == null || nickname.trim().isEmpty()) {
            nickname = (String) reviewData.get("nickname");
        }
        if (nickname == null || nickname.trim().isEmpty()) {
            nickname = "외부사용자";
        }
        return nickname.length() > 50 ? nickname.substring(0, 50) : nickname;
    }

    /**
     * 평점 추출
     */
    private Integer extractRating(Map<String, Object> reviewData) {
        Object ratingObj = reviewData.get("rating");
        if (ratingObj instanceof Number) {
            int ratingValue = ((Number) ratingObj).intValue();
            return Math.max(1, Math.min(5, ratingValue)); // 1-5 범위로 제한
        }
        return 5; // 기본값
    }

    /**
     * 리뷰 내용 추출
     */
    private String extractContent(Map<String, Object> reviewData) {
        String content = (String) reviewData.get("content");
        if (content == null || content.trim().isEmpty()) {
            return "외부 플랫폼 리뷰";
        }

        // 내용이 너무 길면 자르기 (1000자 제한)
        if (content.length() > 1000) {
            content = content.substring(0, 1000) + "...";
        }

        return content;
    }

    /**
     * 이미지 URL 목록 추출
     */
    @SuppressWarnings("unchecked")
    private List<String> extractImageUrls(Map<String, Object> reviewData) {
        try {
            Object imageUrlsObj = reviewData.get("imageUrls");
            if (imageUrlsObj instanceof List) {
                return (List<String>) imageUrlsObj;
            }
            return new ArrayList<>();
        } catch (Exception e) {
            log.warn("이미지 URL 추출 실패: {}", e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * 🔥 이벤트 중복 처리 확인
     */
    private boolean isEventAlreadyProcessed(String eventId) {
        return processedEventIds.contains(eventId);
    }

    /**
     * 🔥 이벤트 처리 완료 표시 (메모리 관리 포함)
     */
    private void markEventAsProcessed(String eventId) {
        processedEventIds.add(eventId);

        // 메모리 관리: 1000개 이상 쌓이면 오래된 것들 삭제
        if (processedEventIds.size() > 1000) {
            Set<String> oldIds = ConcurrentHashMap.newKeySet();
            processedEventIds.stream()
                    .limit(500)
                    .forEach(oldIds::add);

            processedEventIds.removeAll(oldIds);

            log.info("처리된 이벤트 ID 캐시 정리 완료: 현재 크기={}", processedEventIds.size());
        }
    }


}