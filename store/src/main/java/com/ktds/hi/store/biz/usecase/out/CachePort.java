package com.ktds.hi.store.biz.usecase.out;

import java.time.Duration;
import java.util.Optional;

/**
 * 캐시 포트 인터페이스
 * 캐시 기능을 정의
 */
public interface CachePort {
    
    /**
     * 캐시에서 매장 데이터 조회
     */
    Optional<Object> getStoreCache(String key);
    
    /**
     * 캐시에 매장 데이터 저장
     */
    void putStoreCache(String key, Object value, Duration ttl);
    
    /**
     * 캐시 무효화
     */
    void invalidateStoreCache(Long storeId);
}
