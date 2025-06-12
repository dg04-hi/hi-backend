package com.ktds.hi.analytics.biz.usecase.out;

import java.time.Duration;
import java.util.Optional;

/**
 * 캐시 포트 인터페이스
 * Redis 캐시 연동을 위한 출력 포트
 */
public interface CachePort {
    
    /**
     * 캐시에서 데이터 조회
     */
    Optional<Object> getAnalyticsCache(String key);
    
    /**
     * 캐시에 데이터 저장
     */
    void putAnalyticsCache(String key, Object value, Duration ttl);
    
    /**
     * 캐시 무효화
     */
    void invalidateCache(String key);
    
    /**
     * 매장별 캐시 무효화
     */
    void invalidateStoreCache(Long storeId);
}
