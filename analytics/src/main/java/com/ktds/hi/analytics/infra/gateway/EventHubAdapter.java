package com.ktds.hi.analytics.infra.gateway;

import com.azure.messaging.eventhubs.EventData;
import com.azure.messaging.eventhubs.EventDataBatch;
import com.azure.messaging.eventhubs.EventHubConsumerClient;
import com.azure.messaging.eventhubs.EventHubProducerClient;
import com.azure.messaging.eventhubs.models.EventPosition;
import com.azure.messaging.eventhubs.models.PartitionEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ktds.hi.analytics.biz.domain.ActionPlan;
import com.ktds.hi.analytics.biz.domain.AnalysisType;
import com.ktds.hi.analytics.biz.usecase.out.EventPort;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Azure Event Hub 어댑터 클래스
 * 이벤트 발행 및 수신 기능을 제공
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class EventHubAdapter implements EventPort {
    
    @Qualifier("reviewEventConsumer")
    private final EventHubConsumerClient reviewEventConsumer;
    
    @Qualifier("aiAnalysisEventProducer")
    private final EventHubProducerClient aiAnalysisEventProducer;
    
    private final ObjectMapper objectMapper;
    private final ExecutorService executorService = Executors.newFixedThreadPool(3);
    private volatile boolean isRunning = false;
    
    @PostConstruct
    public void startEventListening() {
        log.info("Event Hub 리스너 시작");
        isRunning = true;
        
        // 리뷰 이벤트 수신 시작
        executorService.submit(this::listenToReviewEvents);
    }
    
    @PreDestroy
    public void stopEventListening() {
        log.info("Event Hub 리스너 종료");
        isRunning = false;
        executorService.shutdown();
        reviewEventConsumer.close();
        aiAnalysisEventProducer.close();
    }
    
    @Override
    public void publishAnalysisCompletedEvent(Long storeId, AnalysisType analysisType) {
        try {
            Map<String, Object> eventData = new HashMap<>();
            eventData.put("eventType", "ANALYSIS_COMPLETED");
            eventData.put("storeId", storeId);
            eventData.put("analysisType", analysisType.name());
            eventData.put("timestamp", System.currentTimeMillis());
            
            String jsonData = objectMapper.writeValueAsString(eventData);
            EventData event = new EventData(jsonData);

            EventDataBatch batch = aiAnalysisEventProducer.createBatch();
            if (batch.tryAdd(event)) {
                aiAnalysisEventProducer.send(batch);  // EventDataBatch로 전송
            }
            log.info("분석 완료 이벤트 발행: storeId={}, type={}", storeId, analysisType);
            
        } catch (Exception e) {
            log.error("분석 완료 이벤트 발행 실패: storeId={}", storeId, e);
        }
    }
    
    @Override
    public void publishActionPlanCreatedEvent(ActionPlan actionPlan) {
        try {
            Map<String, Object> eventData = new HashMap<>();
            eventData.put("eventType", "ACTION_PLAN_CREATED");
            eventData.put("planId", actionPlan.getId());
            eventData.put("storeId", actionPlan.getStoreId());
            eventData.put("title", actionPlan.getTitle());
            eventData.put("timestamp", System.currentTimeMillis());
            
            String jsonData = objectMapper.writeValueAsString(eventData);
            EventData event = new EventData(jsonData);

            // ✅ 올바른 사용법
            EventDataBatch batch = aiAnalysisEventProducer.createBatch();
            if (batch.tryAdd(event)) {
                aiAnalysisEventProducer.send(batch);  // EventDataBatch로 전송
            }

            log.info("실행계획 생성 이벤트 발행: planId={}, storeId={}", 
                    actionPlan.getId(), actionPlan.getStoreId());
            
        } catch (Exception e) {
            log.error("실행계획 생성 이벤트 발행 실패: planId={}", actionPlan.getId(), e);
        }
    }
    
    /**
     * 리뷰 이벤트 수신 처리
     */
    private void listenToReviewEvents() {
        log.info("리뷰 이벤트 수신 시작");
        
        try {
            Iterable<PartitionEvent> events = reviewEventConsumer.receiveFromPartition(
                "0",                          // 파티션 ID
                100,                          // 최대 이벤트 수
                EventPosition.earliest(),     // 시작 위치
                Duration.ofSeconds(30)        // 타임아웃
            );

            for (PartitionEvent partitionEvent : events) {
                handleReviewEvent(partitionEvent);
            }
            
        } catch (Exception e) {
            log.error("리뷰 이벤트 수신 중 오류 발생", e);
        }
    }
    
    /**
     * 리뷰 이벤트 처리
     */
    private void handleReviewEvent(PartitionEvent partitionEvent) {
        try {
            EventData eventData = partitionEvent.getData();
            String eventBody = eventData.getBodyAsString();
            
            Map<String, Object> event = objectMapper.readValue(eventBody, Map.class);
            String eventType = (String) event.get("eventType");
            Long storeId = Long.valueOf(event.get("storeId").toString());
            
            log.info("리뷰 이벤트 수신: type={}, storeId={}", eventType, storeId);
            
            switch (eventType) {
                case "REVIEW_CREATED":
                    handleReviewCreatedEvent(storeId, event);
                    break;
                case "REVIEW_DELETED":
                    handleReviewDeletedEvent(storeId, event);
                    break;
                case "REVIEW_COMMENT_CREATED":
                    handleReviewCommentCreatedEvent(storeId, event);
                    break;
                default:
                    log.warn("알 수 없는 이벤트 타입: {}", eventType);
            }
            
        } catch (Exception e) {
            log.error("리뷰 이벤트 처리 중 오류 발생", e);
        }
    }
    
    /**
     * 리뷰 생성 이벤트 처리
     */
    private void handleReviewCreatedEvent(Long storeId, Map<String, Object> event) {
        log.info("리뷰 생성 이벤트 처리: storeId={}", storeId);
        
        // TODO: 리뷰 생성 시 AI 분석 트리거
        // 1. 새로운 리뷰 데이터 수집
        // 2. AI 분석 요청
        // 3. 분석 결과 저장
        // 4. 캐시 무효화
    }
    
    /**
     * 리뷰 삭제 이벤트 처리
     */
    private void handleReviewDeletedEvent(Long storeId, Map<String, Object> event) {
        log.info("리뷰 삭제 이벤트 처리: storeId={}", storeId);
        
        // TODO: 리뷰 삭제 시 분석 데이터 재계산
        // 1. 분석 데이터 재계산
        // 2. 캐시 무효화
    }
    
    /**
     * 리뷰 댓글 생성 이벤트 처리
     */
    private void handleReviewCommentCreatedEvent(Long storeId, Map<String, Object> event) {
        log.info("리뷰 댓글 생성 이벤트 처리: storeId={}", storeId);
        
        // TODO: 댓글 생성 시 고객 응답률 분석
        // 1. 고객 응답률 계산
        // 2. 분석 데이터 업데이트
    }
}
