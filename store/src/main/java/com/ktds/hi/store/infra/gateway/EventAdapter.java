package com.ktds.hi.store.infra.gateway;

import com.ktds.hi.store.biz.domain.Store;
import com.ktds.hi.store.biz.usecase.out.EventPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

/**
 * 이벤트 어댑터 클래스
 * Event Port를 구현하여 이벤트 발행 기능을 제공
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class EventAdapter implements EventPort {
    
    private final ApplicationEventPublisher eventPublisher;
    
    @Override
    public void publishStoreCreatedEvent(Store store) {
        log.info("매장 생성 이벤트 발행: storeId={}", store.getId());
        
        try {
            StoreCreatedEvent event = new StoreCreatedEvent(store);
            eventPublisher.publishEvent(event);
            
            log.info("매장 생성 이벤트 발행 완료: storeId={}", store.getId());
            
        } catch (Exception e) {
            log.error("매장 생성 이벤트 발행 실패: storeId={}, error={}", store.getId(), e.getMessage(), e);
        }
    }
    
    @Override
    public void publishStoreUpdatedEvent(Store store) {
        log.info("매장 수정 이벤트 발행: storeId={}", store.getId());
        
        try {
            StoreUpdatedEvent event = new StoreUpdatedEvent(store);
            eventPublisher.publishEvent(event);
            
            log.info("매장 수정 이벤트 발행 완료: storeId={}", store.getId());
            
        } catch (Exception e) {
            log.error("매장 수정 이벤트 발행 실패: storeId={}, error={}", store.getId(), e.getMessage(), e);
        }
    }
    
    @Override
    public void publishStoreDeletedEvent(Long storeId) {
        log.info("매장 삭제 이벤트 발행: storeId={}", storeId);
        
        try {
            StoreDeletedEvent event = new StoreDeletedEvent(storeId);
            eventPublisher.publishEvent(event);
            
            log.info("매장 삭제 이벤트 발행 완료: storeId={}", storeId);
            
        } catch (Exception e) {
            log.error("매장 삭제 이벤트 발행 실패: storeId={}, error={}", storeId, e.getMessage(), e);
        }
    }
    
    /**
     * 매장 이벤트 클래스들
     */
    public static class StoreCreatedEvent {
        private final Store store;
        
        public StoreCreatedEvent(Store store) {
            this.store = store;
        }
        
        public Store getStore() {
            return store;
        }
    }
    
    public static class StoreUpdatedEvent {
        private final Store store;
        
        public StoreUpdatedEvent(Store store) {
            this.store = store;
        }
        
        public Store getStore() {
            return store;
        }
    }
    
    public static class StoreDeletedEvent {
        private final Long storeId;
        
        public StoreDeletedEvent(Long storeId) {
            this.storeId = storeId;
        }
        
        public Long getStoreId() {
            return storeId;
        }
    }
}
