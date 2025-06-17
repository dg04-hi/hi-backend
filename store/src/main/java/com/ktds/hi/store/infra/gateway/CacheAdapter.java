package com.ktds.hi.store.infra.gateway;

import com.ktds.hi.store.biz.usecase.out.CachePort;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 캐시 어댑터 클래스
 * Cache Port를 구현하여 Redis 캐시 기능을 제공
 */
@Component
@RequiredArgsConstructor
// @NoArgsConstructor(force = true)
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
    public void putStoreCache(String key, Object value, long ttlSeconds) {
        try {
            redisTemplate.opsForValue().set(key, value, ttlSeconds, TimeUnit.SECONDS);
            log.debug("캐시 저장: key={}, ttl={}초", key, ttlSeconds);
        } catch (Exception e) {
            log.warn("캐시 저장 실패: key={}, error={}", key, e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void invalidateStoreCache(Object key) {
        try {
            if (key instanceof Long) {
                // 매장 ID로 특정 매장 캐시 삭제
                Long storeId = (Long) key;
                String storeDetailKey = "store_detail:" + storeId;
                redisTemplate.delete(storeDetailKey);
                log.debug("매장 캐시 무효화 완료: storeId={}", storeId);

            } else if (key instanceof String) {
                // 패턴으로 캐시 삭제
                String pattern = key.toString();
                Set<String> keys = redisTemplate.keys(pattern);
                if (keys != null && !keys.isEmpty()) {
                    redisTemplate.delete(keys);
                }
                log.debug("패턴 캐시 무효화 완료: pattern={}", pattern);

            } else {
                // 기본적으로 toString()으로 키 생성
                String cacheKey = "stores:" + key.toString();
                redisTemplate.delete(cacheKey);
                log.debug("캐시 무효화 완료: key={}", key);
            }

        } catch (Exception e) {
            log.error("캐시 무효화 실패: key={}, error={}", key, e.getMessage());
        }
    }
}
