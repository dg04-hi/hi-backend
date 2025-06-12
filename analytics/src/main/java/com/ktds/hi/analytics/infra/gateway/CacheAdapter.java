package com.ktds.hi.analytics.infra.gateway;

import com.ktds.hi.analytics.biz.usecase.out.CachePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Optional;
import java.util.Set;

/**
 * Redis 캐시 어댑터 클래스
 * CachePort를 구현하여 Redis 캐싱 기능 제공
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CacheAdapter implements CachePort {
    
    private final RedisTemplate<String, Object> redisTemplate;
    
    @Override
    public Optional<Object> getAnalyticsCache(String key) {
        try {
            Object cached = redisTemplate.opsForValue().get(key);
            if (cached != null) {
                log.debug("캐시 히트: key={}", key);
                return Optional.of(cached);
            }
            log.debug("캐시 미스: key={}", key);
            return Optional.empty();
            
        } catch (Exception e) {
            log.warn("캐시 조회 실패: key={}", key, e);
            return Optional.empty();
        }
    }
    
    @Override
    public void putAnalyticsCache(String key, Object value, Duration ttl) {
        try {
            redisTemplate.opsForValue().set(key, value, ttl);
            log.debug("캐시 저장: key={}, ttl={}초", key, ttl.getSeconds());
            
        } catch (Exception e) {
            log.warn("캐시 저장 실패: key={}", key, e);
        }
    }
    
    @Override
    public void invalidateCache(String key) {
        try {
            Boolean deleted = redisTemplate.delete(key);
            log.debug("캐시 삭제: key={}, deleted={}", key, deleted);
            
        } catch (Exception e) {
            log.warn("캐시 삭제 실패: key={}", key, e);
        }
    }
    
    @Override
    public void invalidateStoreCache(Long storeId) {
        try {
            String pattern = "analytics:store:" + storeId + "*";
            Set<String> keys = redisTemplate.keys(pattern);
            
            if (keys != null && !keys.isEmpty()) {
                Long deletedCount = redisTemplate.delete(keys);
                log.info("매장 캐시 무효화: storeId={}, deleted={}", storeId, deletedCount);
            }
            
        } catch (Exception e) {
            log.warn("매장 캐시 무효화 실패: storeId={}", storeId, e);
        }
    }
}
