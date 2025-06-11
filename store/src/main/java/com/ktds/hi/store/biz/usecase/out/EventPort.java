package com.ktds.hi.store.biz.usecase.out;

import com.ktds.hi.store.biz.domain.Store;

/**
 * 이벤트 포트 인터페이스
 * 이벤트 발행 기능을 정의
 */
public interface EventPort {
    
    /**
     * 매장 생성 이벤트 발행
     */
    void publishStoreCreatedEvent(Store store);
    
    /**
     * 매장 수정 이벤트 발행
     */
    void publishStoreUpdatedEvent(Store store);
    
    /**
     * 매장 삭제 이벤트 발행
     */
    void publishStoreDeletedEvent(Long storeId);
}
