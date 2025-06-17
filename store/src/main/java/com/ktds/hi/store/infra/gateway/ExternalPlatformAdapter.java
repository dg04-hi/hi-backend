package com.ktds.hi.store.infra.gateway;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ktds.hi.store.biz.usecase.out.ExternalPlatformPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.*;

/**
 * 외부 플랫폼 연동 어댑터
 * 각 플랫폼별 API 호출 및 Redis 저장을 담당
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ExternalPlatformAdapter implements ExternalPlatformPort {

    private final RestTemplate restTemplate;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    @Value("${external.kakao.crawler.url:http://localhost:9001}")
    private String kakaoCrawlerUrl;

    @Value("${external.naver.crawler.url:http://localhost:9002}")
    private String naverCrawlerUrl;

    @Value("${external.google.crawler.url:http://localhost:9003}")
    private String googleCrawlerUrl;

    @Value("${external.hiorder.api.url:http://localhost:8080}")
    private String hiorderApiUrl;

    // ===== 리뷰 동기화 메서드들 =====

    @Override
    public int syncNaverReviews(Long storeId, String externalStoreId) {
        log.info("네이버 리뷰 동기화 시작: storeId={}, externalStoreId={}", storeId, externalStoreId);

        try {
            // 네이버 크롤링 서비스 호출
            String url = String.format("%s/api/naver/reviews?storeId=%s", naverCrawlerUrl, externalStoreId);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(url, org.springframework.http.HttpMethod.GET, entity, String.class);

            return processNaverResponse(storeId, "NAVER", response.getBody());

        } catch (Exception e) {
            log.error("네이버 리뷰 동기화 실패: storeId={}, externalStoreId={}, error={}",
                    storeId, externalStoreId, e.getMessage());
            updateSyncStatus(storeId, "NAVER", "FAILED", 0);
            return 0;
        }
    }

    @Override
    public int syncKakaoReviews(Long storeId, String externalStoreId) {
        log.info("카카오 리뷰 동기화 시작: storeId={}, externalStoreId={}", storeId, externalStoreId);

        try {
            // 카카오 크롤링 서비스 호출
            String url = String.format("%s/api/kakao/reviews?storeId=%s", kakaoCrawlerUrl, externalStoreId);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(url, org.springframework.http.HttpMethod.GET, entity, String.class);

            return processKakaoResponse(storeId, "KAKAO", response.getBody());

        } catch (Exception e) {
            log.error("카카오 리뷰 동기화 실패: storeId={}, externalStoreId={}, error={}",
                    storeId, externalStoreId, e.getMessage());
            updateSyncStatus(storeId, "KAKAO", "FAILED", 0);
            return 0;
        }
    }

    @Override
    public int syncGoogleReviews(Long storeId, String externalStoreId) {
        log.info("구글 리뷰 동기화 시작: storeId={}, externalStoreId={}", storeId, externalStoreId);

        try {
            // 구글 크롤링 서비스 호출
            String url = String.format("%s/api/google/reviews?storeId=%s", googleCrawlerUrl, externalStoreId);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(url, org.springframework.http.HttpMethod.GET, entity, String.class);

            return processGoogleResponse(storeId, "GOOGLE", response.getBody());

        } catch (Exception e) {
            log.error("구글 리뷰 동기화 실패: storeId={}, externalStoreId={}, error={}",
                    storeId, externalStoreId, e.getMessage());
            updateSyncStatus(storeId, "GOOGLE", "FAILED", 0);
            return 0;
        }
    }

    @Override
    public int syncHiorderReviews(Long storeId, String externalStoreId) {
        log.info("하이오더 리뷰 동기화 시작: storeId={}, externalStoreId={}", storeId, externalStoreId);

        try {
            // 하이오더 API 호출
            String url = String.format("%s/api/reviews?storeId=%s", hiorderApiUrl, externalStoreId);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(url, org.springframework.http.HttpMethod.GET, entity, String.class);

            return processHiorderResponse(storeId, "HIORDER", response.getBody());

        } catch (Exception e) {
            log.error("하이오더 리뷰 동기화 실패: storeId={}, externalStoreId={}, error={}",
                    storeId, externalStoreId, e.getMessage());
            updateSyncStatus(storeId, "HIORDER", "FAILED", 0);
            return 0;
        }
    }

    // ===== 계정 연동 메서드들 =====

    @Override
    public boolean connectNaverAccount(Long storeId, String username, String password) {
        log.info("네이버 계정 연동: storeId={}", storeId);
        try {
            if (validateCredentials(username, password)) {
                saveConnectionInfo(storeId, "NAVER", username);
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error("네이버 계정 연동 실패: storeId={}, error={}", storeId, e.getMessage());
            return false;
        }
    }

    @Override
    public boolean connectKakaoAccount(Long storeId, String username, String password) {
        log.info("카카오 계정 연동: storeId={}", storeId);
        try {
            if (validateCredentials(username, password)) {
                saveConnectionInfo(storeId, "KAKAO", username);
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error("카카오 계정 연동 실패: storeId={}, error={}", storeId, e.getMessage());
            return false;
        }
    }

    @Override
    public boolean connectGoogleAccount(Long storeId, String username, String password) {
        log.info("구글 계정 연동: storeId={}", storeId);
        try {
            if (validateCredentials(username, password)) {
                saveConnectionInfo(storeId, "GOOGLE", username);
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error("구글 계정 연동 실패: storeId={}, error={}", storeId, e.getMessage());
            return false;
        }
    }

    @Override
    public boolean connectHiorderAccount(Long storeId, String username, String password) {
        log.info("하이오더 계정 연동: storeId={}", storeId);
        try {
            if (validateCredentials(username, password)) {
                saveConnectionInfo(storeId, "HIORDER", username);
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error("하이오더 계정 연동 실패: storeId={}, error={}", storeId, e.getMessage());
            return false;
        }
    }

    @Override
    public boolean disconnectPlatform(Long storeId, String platform) {
        log.info("플랫폼 연동 해제: storeId={}, platform={}", storeId, platform);

        try {
            String connectionKey = String.format("external:connection:%d:%s", storeId, platform);
            redisTemplate.delete(connectionKey);

            log.info("플랫폼 연동 해제 완료: storeId={}, platform={}", storeId, platform);
            return true;

        } catch (Exception e) {
            log.error("플랫폼 연동 해제 실패: storeId={}, platform={}, error={}",
                    storeId, platform, e.getMessage());
            return false;
        }
    }

    @Override
    public List<String> getConnectedPlatforms(Long storeId) {
        log.info("연동된 플랫폼 조회: storeId={}", storeId);

        try {
            List<String> connectedPlatforms = new ArrayList<>();

            String pattern = String.format("external:connection:%d:*", storeId);
            Set<String> keys = redisTemplate.keys(pattern);

            if (keys != null) {
                for (String key : keys) {
                    String platform = key.substring(key.lastIndexOf(':') + 1);
                    connectedPlatforms.add(platform.toUpperCase());
                }
            }

            return connectedPlatforms;

        } catch (Exception e) {
            log.error("연동된 플랫폼 조회 실패: storeId={}, error={}", storeId, e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public List<Map<String, Object>> getTempReviews(Long storeId, String platform) {
        try {
            String pattern = String.format("external:reviews:pending:%d:%s:*", storeId, platform);
            Set<String> keys = redisTemplate.keys(pattern);

            if (keys != null && !keys.isEmpty()) {
                String latestKey = keys.stream()
                        .max(Comparator.comparing(key -> {
                            try {
                                return Long.parseLong(key.substring(key.lastIndexOf(':') + 1));
                            } catch (NumberFormatException e) {
                                return 0L;
                            }
                        }))
                        .orElse(null);

                if (latestKey != null) {
                    Map<String, Object> cacheData = (Map<String, Object>) redisTemplate.opsForValue().get(latestKey);
                    if (cacheData != null && cacheData.containsKey("reviews")) {
                        return (List<Map<String, Object>>) cacheData.get("reviews");
                    }
                }
            }

            return new ArrayList<>();

        } catch (Exception e) {
            log.error("Redis에서 임시 리뷰 데이터 조회 실패: storeId={}, platform={}", storeId, platform);
            return new ArrayList<>();
        }
    }

    // ===== Private Helper Methods =====

    private int processNaverResponse(Long storeId, String platform, String responseBody) {
        try {
            if (responseBody == null || responseBody.trim().isEmpty()) {
                log.warn("네이버 응답이 비어있음: storeId={}", storeId);
                return 0;
            }

            JsonNode root = objectMapper.readTree(responseBody);

            List<Map<String, Object>> parsedReviews = new ArrayList<>();

            // 네이버 크롤링 응답 처리
            if (root.has("success") && root.get("success").asBoolean() && root.has("data")) {
                JsonNode data = root.get("data");
                if (data.has("reviews")) {
                    JsonNode reviews = data.get("reviews");
                    for (JsonNode reviewNode : reviews) {
                        Map<String, Object> review = new HashMap<>();
                        review.put("reviewId", reviewNode.path("reviewId").asText());
                        review.put("rating", reviewNode.path("rating").asDouble(0.0));
                        review.put("content", reviewNode.path("content").asText());
                        review.put("reviewerName", reviewNode.path("reviewerName").asText());
                        review.put("createdAt", reviewNode.path("createdAt").asText());
                        review.put("platform", platform);
                        parsedReviews.add(review);
                    }
                }
            }

            if (!parsedReviews.isEmpty()) {
                saveToRedis(storeId, platform, parsedReviews);
                updateSyncStatus(storeId, platform, "SUCCESS", parsedReviews.size());

                log.info("Redis에 리뷰 데이터 저장 완료: key={}, count={}",
                        String.format("external:reviews:pending:%d:%s:%d", storeId, platform, System.currentTimeMillis()),
                        parsedReviews.size());
            }

            return parsedReviews.size();

        } catch (Exception e) {
            log.error("네이버 응답 파싱 및 Redis 저장 실패: {}", e.getMessage());
            updateSyncStatus(storeId, platform, "FAILED", 0);
            return 0;
        }
    }

    private int processKakaoResponse(Long storeId, String platform, String responseBody) {
        try {
            if (responseBody == null || responseBody.trim().isEmpty()) {
                log.warn("카카오 응답이 비어있음: storeId={}", storeId);
                return 0;
            }

            JsonNode root = objectMapper.readTree(responseBody);

            List<Map<String, Object>> parsedReviews = new ArrayList<>();

            // 카카오 크롤링 응답 처리
            if (root.has("success") && root.get("success").asBoolean() && root.has("data")) {
                JsonNode data = root.get("data");
                if (data.has("reviews")) {
                    JsonNode reviews = data.get("reviews");
                    for (JsonNode reviewNode : reviews) {
                        Map<String, Object> review = new HashMap<>();
                        review.put("reviewId", reviewNode.path("reviewId").asText());
                        review.put("rating", reviewNode.path("rating").asDouble(0.0));
                        review.put("content", reviewNode.path("content").asText());
                        review.put("reviewerName", reviewNode.path("reviewerName").asText());
                        review.put("createdAt", reviewNode.path("createdAt").asText());
                        review.put("platform", platform);
                        parsedReviews.add(review);
                    }
                }
            }

            if (!parsedReviews.isEmpty()) {
                saveToRedis(storeId, platform, parsedReviews);
                updateSyncStatus(storeId, platform, "SUCCESS", parsedReviews.size());

                log.info("Redis에 리뷰 데이터 저장 완료: key={}, count={}",
                        String.format("external:reviews:pending:%d:%s:%d", storeId, platform, System.currentTimeMillis()),
                        parsedReviews.size());
            }

            return parsedReviews.size();

        } catch (Exception e) {
            log.error("카카오 응답 파싱 및 Redis 저장 실패: {}", e.getMessage());
            updateSyncStatus(storeId, platform, "FAILED", 0);
            return 0;
        }
    }

    private int processGoogleResponse(Long storeId, String platform, String responseBody) {
        try {
            if (responseBody == null || responseBody.trim().isEmpty()) {
                log.warn("구글 응답이 비어있음: storeId={}", storeId);
                return 0;
            }

            JsonNode root = objectMapper.readTree(responseBody);

            List<Map<String, Object>> parsedReviews = new ArrayList<>();

            // 구글 크롤링 응답 처리
            if (root.has("success") && root.get("success").asBoolean() && root.has("data")) {
                JsonNode data = root.get("data");
                if (data.has("reviews")) {
                    JsonNode reviews = data.get("reviews");
                    for (JsonNode reviewNode : reviews) {
                        Map<String, Object> review = new HashMap<>();
                        review.put("reviewId", reviewNode.path("reviewId").asText());
                        review.put("rating", reviewNode.path("rating").asDouble(0.0));
                        review.put("content", reviewNode.path("content").asText());
                        review.put("reviewerName", reviewNode.path("reviewerName").asText());
                        review.put("createdAt", reviewNode.path("createdAt").asText());
                        review.put("platform", platform);
                        parsedReviews.add(review);
                    }
                }
            }

            if (!parsedReviews.isEmpty()) {
                saveToRedis(storeId, platform, parsedReviews);
                updateSyncStatus(storeId, platform, "SUCCESS", parsedReviews.size());
            }

            return parsedReviews.size();

        } catch (Exception e) {
            log.error("구글 응답 파싱 실패: storeId={}, error={}", storeId, e.getMessage());
            updateSyncStatus(storeId, platform, "FAILED", 0);
            return 0;
        }
    }

    private int processHiorderResponse(Long storeId, String platform, String responseBody) {
        try {
            if (responseBody == null || responseBody.trim().isEmpty()) {
                log.warn("하이오더 응답이 비어있음: storeId={}", storeId);
                return 0;
            }

            JsonNode root = objectMapper.readTree(responseBody);

            List<Map<String, Object>> parsedReviews = new ArrayList<>();

            // 하이오더 API 응답 처리
            if (root.has("success") && root.get("success").asBoolean() && root.has("data")) {
                JsonNode data = root.get("data");
                for (JsonNode reviewNode : data) {
                    Map<String, Object> review = new HashMap<>();
                    review.put("reviewId", reviewNode.path("reviewId").asText());
                    review.put("rating", reviewNode.path("rating").asDouble(0.0));
                    review.put("content", reviewNode.path("comment").asText());
                    review.put("reviewerName", reviewNode.path("customerName").asText());
                    review.put("createdAt", reviewNode.path("reviewDate").asText());
                    review.put("platform", platform);
                    parsedReviews.add(review);
                }
            }

            if (!parsedReviews.isEmpty()) {
                saveToRedis(storeId, platform, parsedReviews);
                updateSyncStatus(storeId, platform, "SUCCESS", parsedReviews.size());
            }

            return parsedReviews.size();

        } catch (Exception e) {
            log.error("하이오더 응답 파싱 실패: storeId={}, error={}", storeId, e.getMessage());
            updateSyncStatus(storeId, platform, "FAILED", 0);
            return 0;
        }
    }

    private boolean validateCredentials(String username, String password) {
        return username != null && !username.trim().isEmpty() &&
                password != null && !password.trim().isEmpty();
    }

    private void saveToRedis(Long storeId, String platform, List<Map<String, Object>> reviews) {
        try {
            long timestamp = System.currentTimeMillis();
            String redisKey = String.format("external:reviews:pending:%d:%s:%d", storeId, platform, timestamp);

            Map<String, Object> cacheData = new HashMap<>();
            cacheData.put("storeId", storeId);
            cacheData.put("platform", platform);
            cacheData.put("reviews", reviews);
            cacheData.put("timestamp", timestamp);
            cacheData.put("status", "PENDING");
            cacheData.put("retryCount", 0);

            redisTemplate.opsForValue().set(redisKey, cacheData, Duration.ofDays(1));

            log.info("Redis에 리뷰 데이터 저장 완료: key={}, count={}", redisKey, reviews.size());

        } catch (Exception e) {
            log.error("Redis 저장 실패: storeId={}, platform={}, error={}",
                    storeId, platform, e.getMessage());
        }
    }

    /**
     * 동기화 상태 Redis에 저장
     */
    private void updateSyncStatus(Long storeId, String platform, String status, int count) {
        try {
            String statusKey = String.format("external:sync:status:%d:%s", storeId, platform);

            Map<String, Object> statusData = new HashMap<>();
            statusData.put("storeId", storeId);
            statusData.put("platform", platform);
            statusData.put("status", status);
            statusData.put("syncedCount", count);
            statusData.put("timestamp", System.currentTimeMillis());

            redisTemplate.opsForValue().set(statusKey, statusData, Duration.ofDays(1));

        } catch (Exception e) {
            log.warn("동기화 상태 저장 실패: storeId={}, platform={}", storeId, platform);
        }
    }

    private void saveConnectionInfo(Long storeId, String platform, String username) {
        try {
            String connectionKey = String.format("external:connection:%d:%s", storeId, platform);

            Map<String, Object> connectionData = new HashMap<>();
            connectionData.put("storeId", storeId);
            connectionData.put("platform", platform);
            connectionData.put("username", username);
            connectionData.put("connectedAt", System.currentTimeMillis());
            connectionData.put("status", "CONNECTED");

            redisTemplate.opsForValue().set(connectionKey, connectionData, Duration.ofDays(30));

            log.info("연동 정보 저장 완료: storeId={}, platform={}", storeId, platform);

        } catch (Exception e) {
            log.error("연동 정보 저장 실패: storeId={}, platform={}, error={}",
                    storeId, platform, e.getMessage());
        }
    }
}