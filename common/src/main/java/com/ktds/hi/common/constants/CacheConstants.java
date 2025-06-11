package com.ktds.hi.common.constants;

import java.time.Duration;

/**
 * 캐시 관련 상수
 */
public class CacheConstants {

    public static final Duration DEFAULT_TTL = Duration.ofHours(1);
    public static final Duration SHORT_TTL = Duration.ofMinutes(15);
    public static final Duration LONG_TTL = Duration.ofHours(24);

    public static final String USER_CACHE_PREFIX = "user:";
    public static final String STORE_CACHE_PREFIX = "store:";
    public static final String REVIEW_CACHE_PREFIX = "review:";
    public static final String RECOMMENDATION_CACHE_PREFIX = "recommend:";

    private CacheConstants() {
        // 유틸리티 클래스
    }
}