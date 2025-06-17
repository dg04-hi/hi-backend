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
            String url = "http://kakao-review-api-service.ai-review-ns.svc.cluster.local/analyze";

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("store_id", externalStoreId);
            requestBody.put("days_limit", 360);
            requestBody.put("max_time", 300);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

            // 🔥 실제 카카오 응답 파싱 및 Redis 저장
            return parseAndStoreToRedis(storeId, "KAKAO", response.getBody());

        } catch (Exception e) {
            log.error("카카오 리뷰 동기화 실패: storeId={}, error={}", storeId, e.getMessage(), e);
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


    /**
     * 카카오 응답 파싱 및 Redis 저장 (실제 응답 구조에 맞게 완전 수정)
     */
    private int parseAndStoreToRedis(Long storeId, String platform, String responseBody) {
        try {
            log.info("카카오 API 응답: {}", responseBody);

            // JSON 파싱
            JsonNode rootNode = objectMapper.readTree(responseBody);

            // 🔥 실제 카카오 크롤링 서비스 응답 구조 체크
            if (!rootNode.has("success") || !rootNode.get("success").asBoolean()) {
                log.warn("카카오 API 응답 실패: {}", responseBody);
                updateSyncStatus(storeId, platform, "FAILED", 0);
                return 0;
            }

            JsonNode reviewsNode = rootNode.get("reviews");
            if (reviewsNode == null || !reviewsNode.isArray()) {
                log.warn("카카오 응답에 reviews 배열이 없음");
                updateSyncStatus(storeId, platform, "SUCCESS", 0);
                return 0;
            }

            // 🔥 실제 카카오 응답 구조에 맞는 리뷰 데이터 변환
            List<Map<String, Object>> parsedReviews = new ArrayList<>();
            for (JsonNode reviewNode : reviewsNode) {
                Map<String, Object> review = new HashMap<>();

                // 🔑 기본 리뷰 정보 (실제 필드명 사용)
                review.put("reviewId", generateReviewId(reviewNode));
                review.put("content", reviewNode.path("content").asText(""));
                review.put("rating", reviewNode.path("rating").asDouble(0.0));
                review.put("reviewerName", reviewNode.path("reviewer_name").asText(""));
                review.put("createdAt", reviewNode.path("date").asText(""));
                review.put("platform", platform);

                // 🏷️ 카카오 특화 정보
                review.put("reviewerLevel", reviewNode.path("reviewer_level").asText(""));
                review.put("likes", reviewNode.path("likes").asInt(0));
                review.put("photoCount", reviewNode.path("photo_count").asInt(0));
                review.put("hasPhotos", reviewNode.path("has_photos").asBoolean(false));

                // 📊 리뷰어 통계 (있는 경우만)
                if (reviewNode.has("reviewer_stats")) {
                    JsonNode stats = reviewNode.get("reviewer_stats");
                    Map<String, Object> reviewerStats = new HashMap<>();
                    reviewerStats.put("reviews", stats.path("reviews").asInt(0));
                    reviewerStats.put("averageRating", stats.path("average_rating").asDouble(0.0));
                    reviewerStats.put("followers", stats.path("followers").asInt(0));
                    review.put("reviewerStats", reviewerStats);
                }

                // 🏆 배지 (있는 경우만)
                if (reviewNode.has("badges") && reviewNode.get("badges").isArray()) {
                    List<String> badges = new ArrayList<>();
                    for (JsonNode badge : reviewNode.get("badges")) {
                        badges.add(badge.asText());
                    }
                    review.put("badges", badges);
                }

                parsedReviews.add(review);
            }

            if (!parsedReviews.isEmpty()) {
                // 🔥 Redis에 저장
                String redisKey = String.format("external:reviews:pending:%d:%s:%d",
                        storeId, platform, System.currentTimeMillis());

                Map<String, Object> cacheData = new HashMap<>();
                cacheData.put("storeId", storeId);
                cacheData.put("platform", platform);
                cacheData.put("reviews", parsedReviews);
                cacheData.put("status", "PENDING");
                cacheData.put("timestamp", System.currentTimeMillis());
                cacheData.put("retryCount", 0);

                redisTemplate.opsForValue().set(redisKey, cacheData, Duration.ofDays(1));

                log.info("Redis에 리뷰 데이터 저장 완료: key={}, count={}", redisKey, parsedReviews.size());

                // 🔥 동기화 상태 업데이트
                updateSyncStatus(storeId, platform, "SUCCESS", parsedReviews.size());
            }

            return parsedReviews.size();

        } catch (Exception e) {
            log.error("카카오 응답 파싱 및 Redis 저장 실패: storeId={}, error={}", storeId, e.getMessage());
            updateSyncStatus(storeId, platform, "FAILED", 0);
            return 0;
        }
    }


    /**
     * 🔥 누락된 generateReviewId 메서드 추가
     */
    private String generateReviewId(JsonNode reviewNode) {
        try {
            String reviewerName = reviewNode.path("reviewer_name").asText("");
            String date = reviewNode.path("date").asText("");
            String content = reviewNode.path("content").asText("");

            // 고유한 ID 생성 (리뷰어명 + 날짜 + 컨텐츠 해시)
            String combined = reviewerName + "_" + date + "_" + content.hashCode();
            return "kakao_" + Math.abs(combined.hashCode());
        } catch (Exception e) {
            // 예외 발생 시 타임스탬프 기반 ID 생성
            return "kakao_" + System.currentTimeMillis() + "_" + (int)(Math.random() * 1000);
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