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
 * 외부 플랫폼 어댑터 클래스
 * External Platform Port를 구현하여 외부 API 연동 기능을 제공
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ExternalPlatformAdapter implements ExternalPlatformPort {

    private final RestTemplate restTemplate;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    @Value("${external-api.naver.client-id:}")
    private String naverClientId;

    @Value("${external-api.naver.client-secret:}")
    private String naverClientSecret;

    @Value("${external-api.kakao.api-key:}")
    private String kakaoApiKey;

    @Value("${external-api.google.api-key:}")
    private String googleApiKey;

    @Value("${external-api.hiorder.api-key:}")
    private String hiorderApiKey;

    @Override
    public int syncNaverReviews(Long storeId, String externalStoreId) {
        log.info("네이버 리뷰 동기화 시작: storeId={}, externalStoreId={}", storeId, externalStoreId);

        try {
            // 네이버 API 호출 (Mock)
            HttpHeaders headers = new HttpHeaders();
            headers.set("X-Naver-Client-Id", naverClientId);
            headers.set("X-Naver-Client-Secret", naverClientSecret);

            // 실제 API 호출 로직
            // ResponseEntity<Map> response = restTemplate.exchange(...);

            // Mock 응답
            int syncedCount = 15; // Mock 데이터

            log.info("네이버 리뷰 동기화 완료: storeId={}, syncedCount={}", storeId, syncedCount);
            return syncedCount;

        } catch (Exception e) {
            log.error("네이버 리뷰 동기화 실패: storeId={}, error={}", storeId, e.getMessage(), e);
            return 0;
        }
    }

    @Override
    public int syncKakaoReviews(Long storeId, String externalStoreId) {
        log.info("카카오 리뷰 동기화 시작: storeId={}, externalStoreId={}", storeId, externalStoreId);

        try {
            // 기존 API 호출 로직 그대로 유지 ⭐
            String url = "http://kakao-review-api.20.249.191.180.nip.io/analyze";

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("store_id", storeId);
            requestBody.put("days_limit", 360);
            requestBody.put("max_time", 300);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

            int syncedCount = parseAndStoreToRedis(storeId, "KAKAO", response.getBody());

            return syncedCount;

        } catch (Exception e) {
            log.error("카카오 리뷰 동기화 실패: storeId={}, error={}", storeId, e.getMessage(), e);
            return 0;
        }
    }



    @Override
    public int syncGoogleReviews(Long storeId, String externalStoreId) {
        log.info("구글 리뷰 동기화 시작: storeId={}, externalStoreId={}", storeId, externalStoreId);

        try {
            // 구글 Places API 호출 (Mock)
            String url = "https://maps.googleapis.com/maps/api/place/details/json?place_id=" +
                    externalStoreId + "&fields=reviews&key=" + googleApiKey;

            // Mock 응답
            int syncedCount = 20;

            log.info("구글 리뷰 동기화 완료: storeId={}, syncedCount={}", storeId, syncedCount);
            return syncedCount;

        } catch (Exception e) {
            log.error("구글 리뷰 동기화 실패: storeId={}, error={}", storeId, e.getMessage(), e);
            return 0;
        }
    }

    @Override
    public int syncHiorderReviews(Long storeId, String externalStoreId) {
        log.info("하이오더 리뷰 동기화 시작: storeId={}, externalStoreId={}", storeId, externalStoreId);

        try {
            // 하이오더 API 호출 (Mock)
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + hiorderApiKey);

            // Mock 응답
            int syncedCount = 8;

            log.info("하이오더 리뷰 동기화 완료: storeId={}, syncedCount={}", storeId, syncedCount);
            return syncedCount;

        } catch (Exception e) {
            log.error("하이오더 리뷰 동기화 실패: storeId={}, error={}", storeId, e.getMessage(), e);
            return 0;
        }
    }

    @Override
    public boolean connectNaverAccount(Long storeId, String username, String password) {
        log.info("네이버 계정 연동 시작: storeId={}, username={}", storeId, username);

        try {
            // 네이버 계정 인증 로직 (Mock)
            // 실제로는 OAuth2 플로우나 ID/PW 인증

            // Mock 성공
            boolean connected = true;

            if (connected) {
                // 연동 정보 저장
                saveExternalConnection(storeId, "NAVER", username);
            }

            log.info("네이버 계정 연동 완료: storeId={}, connected={}", storeId, connected);
            return connected;

        } catch (Exception e) {
            log.error("네이버 계정 연동 실패: storeId={}, error={}", storeId, e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean connectKakaoAccount(Long storeId, String username, String password) {
        log.info("카카오 계정 연동 시작: storeId={}, username={}", storeId, username);

        try {
            // 카카오 계정 인증 로직 (Mock)
            boolean connected = true;

            if (connected) {
                saveExternalConnection(storeId, "KAKAO", username);
            }

            log.info("카카오 계정 연동 완료: storeId={}, connected={}", storeId, connected);
            return connected;

        } catch (Exception e) {
            log.error("카카오 계정 연동 실패: storeId={}, error={}", storeId, e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean connectGoogleAccount(Long storeId, String username, String password) {
        log.info("구글 계정 연동 시작: storeId={}, username={}", storeId, username);

        try {
            // 구글 계정 인증 로직 (Mock)
            boolean connected = true;

            if (connected) {
                saveExternalConnection(storeId, "GOOGLE", username);
            }

            log.info("구글 계정 연동 완료: storeId={}, connected={}", storeId, connected);
            return connected;

        } catch (Exception e) {
            log.error("구글 계정 연동 실패: storeId={}, error={}", storeId, e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean connectHiorderAccount(Long storeId, String username, String password) {
        log.info("하이오더 계정 연동 시작: storeId={}, username={}", storeId, username);

        try {
            // 하이오더 계정 인증 로직 (Mock)
            boolean connected = true;

            if (connected) {
                saveExternalConnection(storeId, "HIORDER", username);
            }

            log.info("하이오더 계정 연동 완료: storeId={}, connected={}", storeId, connected);
            return connected;

        } catch (Exception e) {
            log.error("하이오더 계정 연동 실패: storeId={}, error={}", storeId, e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean disconnectPlatform(Long storeId, String platform) {
        log.info("외부 플랫폼 연동 해제 시작: storeId={}, platform={}", storeId, platform);

        try {
            // 플랫폼별 연동 해제 로직
            boolean disconnected = false;

            switch (platform.toUpperCase()) {
                case "NAVER":
                    disconnected = disconnectNaverAccount(storeId);
                    break;
                case "KAKAO":
                    disconnected = disconnectKakaoAccount(storeId);
                    break;
                case "GOOGLE":
                    disconnected = disconnectGoogleAccount(storeId);
                    break;
                case "HIORDER":
                    disconnected = disconnectHiorderAccount(storeId);
                    break;
                default:
                    log.warn("지원하지 않는 플랫폼: {}", platform);
                    return false;
            }

            if (disconnected) {
                removeExternalConnection(storeId, platform);
            }

            log.info("외부 플랫폼 연동 해제 완료: storeId={}, platform={}, disconnected={}",
                    storeId, platform, disconnected);
            return disconnected;

        } catch (Exception e) {
            log.error("외부 플랫폼 연동 해제 실패: storeId={}, platform={}, error={}",
                    storeId, platform, e.getMessage(), e);
            return false;
        }
    }

    /**
     * 네이버 계정 연동 해제
     */
    private boolean disconnectNaverAccount(Long storeId) {
        // 네이버 연동 해제 로직 (Mock)
        return true;
    }

    /**
     * 카카오 계정 연동 해제
     */
    private boolean disconnectKakaoAccount(Long storeId) {
        // 카카오 연동 해제 로직 (Mock)
        return true;
    }

    /**
     * 구글 계정 연동 해제
     */
    private boolean disconnectGoogleAccount(Long storeId) {
        // 구글 연동 해제 로직 (Mock)
        return true;
    }

    /**
     * 하이오더 계정 연동 해제
     */
    private boolean disconnectHiorderAccount(Long storeId) {
        // 하이오더 연동 해제 로직 (Mock)
        return true;
    }

    /**
     * 외부 연동 정보 저장 Redis 활용
     */
    private void saveExternalConnection(Long storeId, String platform, String username) {
        try {
            String connectionKey = String.format("external:connection:%d:%s", storeId, platform);

            Map<String, Object> connectionData = new HashMap<>();
            connectionData.put("storeId", storeId);
            connectionData.put("platform", platform);
            connectionData.put("username", username);
            connectionData.put("connectedAt", System.currentTimeMillis());
            connectionData.put("isActive", true);

            redisTemplate.opsForValue().set(connectionKey, connectionData, Duration.ofDays(30));

            log.info("외부 연동 정보 Redis 저장 완료: storeId={}, platform={}, username={}",
                    storeId, platform, username);

        } catch (Exception e) {
            log.error("외부 연동 정보 저장 실패: storeId={}, platform={}, error={}",
                    storeId, platform, e.getMessage());
        }
    }

    /**
     * 외부 연동 정보 제거
     */
    private void removeExternalConnection(Long storeId, String platform) {
        // 실제로는 ExternalPlatformEntity에서 연동 정보 제거
        log.info("외부 연동 정보 제거: storeId={}, platform={}", storeId, platform);
    }

    /**
     * 카카오 응답 파싱 및 Redis 저장 (새로 추가)
     */
    private int parseAndStoreToRedis(Long storeId, String platform, String responseBody) {
        try {
            log.info("카카오 API 응답: {}", responseBody);

            // JSON 파싱
            JsonNode rootNode = objectMapper.readTree(responseBody);
            JsonNode reviewsNode = rootNode.get("reviews");

            if (reviewsNode == null || !reviewsNode.isArray()) {
                return 0;
            }

            // 리뷰 데이터 변환
            List<Map<String, Object>> parsedReviews = new ArrayList<>();
            for (JsonNode reviewNode : reviewsNode) {
                Map<String, Object> review = new HashMap<>();
                review.put("reviewId", reviewNode.get("review_id").asText());
                review.put("content", reviewNode.get("content").asText());
                review.put("rating", reviewNode.get("rating").asInt());
                review.put("authorName", reviewNode.get("author_name").asText());
                review.put("reviewDate", reviewNode.get("review_date").asText());
                review.put("platform", platform);
                parsedReviews.add(review);
            }

            if (!parsedReviews.isEmpty()) {
                // Redis에 저장 (TTL: 24시간)
                String redisKey = String.format("external:reviews:pending:%d:%s:%d",
                        storeId, platform, System.currentTimeMillis());

                Map<String, Object> cacheData = new HashMap<>();
                cacheData.put("storeId", storeId);
                cacheData.put("platform", platform);
                cacheData.put("reviews", parsedReviews);
                cacheData.put("status", "PENDING");
                cacheData.put("createdAt", System.currentTimeMillis());
                cacheData.put("retryCount", 0);

                redisTemplate.opsForValue().set(redisKey, cacheData, Duration.ofHours(24));

                log.info("Redis에 리뷰 데이터 저장 완료: key={}, count={}", redisKey, parsedReviews.size());

                // 동기화 상태 업데이트
                updateSyncStatus(storeId, platform, "SUCCESS", parsedReviews.size());
            }

            return parsedReviews.size();

        } catch (Exception e) {
            log.error("카카오 응답 파싱 및 Redis 저장 실패: {}", e.getMessage());
            updateSyncStatus(storeId, platform, "FAILED", 0);
            return 0;
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

    @Override
    public List<Map<String, Object>> getTempReviews(Long storeId, String platform) {
        try {
            // Redis에서 최신 pending 데이터 조회
            String pattern = String.format("external:reviews:pending:%d:%s:*", storeId, platform);
            Set<String> keys = redisTemplate.keys(pattern);

            if (keys != null && !keys.isEmpty()) {
                // 가장 최신 키 선택 (타임스탬프 기준)
                String latestKey = keys.stream()
                        .max(Comparator.comparing(key -> Long.parseLong(key.substring(key.lastIndexOf(':') + 1))))
                        .orElse(null);

                if (latestKey != null) {
                    Map<String, Object> cacheData = (Map<String, Object>) redisTemplate.opsForValue().get(latestKey);
                    if (cacheData != null) {
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



}