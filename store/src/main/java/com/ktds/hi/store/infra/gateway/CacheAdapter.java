package com.ktds.hi.store.infra.gateway;

import com.ktds.hi.store.biz.usecase.out.CachePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Optional;

/**
 * 캐시 어댑터 클래스
 * Cache Port를 구현하여 Redis 캐시 기능을 제공
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class CacheAdapter implements CachePort {
    
    private final RedisTemplate<String, Object> redisTemplate;
    
    @Override
    public Optional<Object> getStoreCache(String key) {
        try {
            Object value = redisTemplate.opsForValue().get(key);
            return Optional.ofNullable(value);
        } catch (Exception e) {
            log.error("매장 캐시 조회 실패: key={}, error={}", key, e.getMessage());
            return Optional.empty();
        }
    }
    
    @Override
    public void putStoreCache(String key, Object value, Duration ttl) {
        try {
            redisTemplate.opsForValue().set(key, value, ttl);
            log.debug("매장 캐시 저장 완료: key={}, ttl={}분", key, ttl.toMinutes());
        } catch (Exception e) {
            log.error("매장 캐시 저장 실패: key={}, error={}", key, e.getMessage());
        }
    }
    
    @Override
    public void invalidateStoreCache(Long storeId) {
        try {
            // 매장 관련 모든 캐시 키 패턴 삭제
            String storeDetailKey = "store_detail:" + storeId;
            String myStoresKey = "my_stores:*";
            
            redisTemplate.delete(storeDetailKey);
            
            log.debug("매장 캐시 무효화 완료: storeId={}", storeId);
        } catch (Exception e) {
            log.error("매장 캐시 무효화 실패: storeId={}, error={}", storeId, e.getMessage());
        }
    }
}
