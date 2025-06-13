package com.ktds.hi.store.biz.usecase.out;

import java.util.List;

/**
 * 캐시 포트 인터페이스
 */
public interface CachePort {

    /**
     * 캐시에서 매장 정보 조회
     */
    <T> T getStoreCache(String key);

    /**
     * 캐시에 매장 정보 저장
     */
    void putStoreCache(String key, Object value, long ttlSeconds);

    /**
     * 캐시 무효화
     */
    void invalidateStoreCache(Object key);
}