package com.ktds.hi.store.infra.gateway;

import com.ktds.hi.store.biz.usecase.out.ExternalPlatformPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * 외부 플랫폼 어댑터 클래스
 * External Platform Port를 구현하여 외부 API 연동 기능을 제공
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ExternalPlatformAdapter implements ExternalPlatformPort {

    private final RestTemplate restTemplate;

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
            // 카카오 API 호출 (Mock)
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "KakaoAK " + kakaoApiKey);

            // Mock 응답
            int syncedCount = 12;

            log.info("카카오 리뷰 동기화 완료: storeId={}, syncedCount={}", storeId, syncedCount);
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
     * 외부 연동 정보 저장
     */
    private void saveExternalConnection(Long storeId, String platform, String username) {
        // 실제로는 ExternalPlatformEntity에 연동 정보 저장
        log.info("외부 연동 정보 저장: storeId={}, platform={}, username={}", storeId, platform, username);
    }

    /**
     * 외부 연동 정보 제거
     */
    private void removeExternalConnection(Long storeId, String platform) {
        // 실제로는 ExternalPlatformEntity에서 연동 정보 제거
        log.info("외부 연동 정보 제거: storeId={}, platform={}", storeId, platform);
    }
}