package com.ktds.hi.store.biz.service;

import com.ktds.hi.store.biz.usecase.in.ExternalIntegrationUseCase;
import com.ktds.hi.store.biz.usecase.out.ExternalPlatformPort;
import com.ktds.hi.store.biz.usecase.out.EventPort;
import com.ktds.hi.store.infra.dto.ExternalSyncRequest;
import com.ktds.hi.store.infra.dto.ExternalSyncResponse;
import com.ktds.hi.store.infra.dto.ExternalConnectRequest;
import com.ktds.hi.store.infra.dto.ExternalConnectResponse;
import com.ktds.hi.common.exception.BusinessException;
import com.ktds.hi.common.exception.ExternalServiceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 외부 연동 인터랙터 클래스
 * 외부 플랫폼 연동 비즈니스 로직을 구현
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ExternalIntegrationInteractor implements ExternalIntegrationUseCase {

    private final ExternalPlatformPort externalPlatformPort;
    private final EventPort eventPort;

    @Override
    public ExternalSyncResponse syncReviews(Long storeId, ExternalSyncRequest request) {
        log.info("외부 플랫폼 리뷰 동기화 시작: storeId={}, platform={}", storeId, request.getPlatform());

        try {
            validateSyncRequest(storeId, request);

            int syncedCount = 0;

            // 플랫폼별 리뷰 동기화
            switch (request.getPlatform().toUpperCase()) {
                case "NAVER":
                    syncedCount = externalPlatformPort.syncNaverReviews(storeId, request.getExternalStoreId());
                    break;
                case "KAKAO":
                    syncedCount = externalPlatformPort.syncKakaoReviews(storeId, request.getExternalStoreId());
                    break;
                case "GOOGLE":
                    syncedCount = externalPlatformPort.syncGoogleReviews(storeId, request.getExternalStoreId());
                    break;
                case "HIORDER":
                    syncedCount = externalPlatformPort.syncHiorderReviews(storeId, request.getExternalStoreId());
                    break;
                default:
                    throw new BusinessException("지원하지 않는 플랫폼입니다: " + request.getPlatform());
            }

            // 동기화 이벤트 발행
            publishSyncEvent(storeId, request.getPlatform(), syncedCount);

            log.info("외부 플랫폼 리뷰 동기화 완료: storeId={}, platform={}, syncedCount={}",
                    storeId, request.getPlatform(), syncedCount);

            return ExternalSyncResponse.builder()
                    .success(true)
                    .message(String.format("%s 플랫폼에서 %d개의 리뷰를 성공적으로 동기화했습니다",
                            request.getPlatform(), syncedCount))
                    .syncedCount(syncedCount)
                    .build();

        } catch (Exception e) {
            log.error("외부 플랫폼 리뷰 동기화 실패: storeId={}, platform={}, error={}",
                    storeId, request.getPlatform(), e.getMessage(), e);

            throw new ExternalServiceException(request.getPlatform(),
                    "리뷰 동기화 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    @Override
    public ExternalConnectResponse connectPlatform(Long storeId, ExternalConnectRequest request) {
        log.info("외부 플랫폼 계정 연동 시작: storeId={}, platform={}", storeId, request.getPlatform());

        try {
            validateConnectRequest(storeId, request);

            boolean connected = false;

            // 플랫폼별 계정 연동
            switch (request.getPlatform().toUpperCase()) {
                case "NAVER":
                    connected = externalPlatformPort.connectNaverAccount(storeId,
                            request.getUsername(), request.getPassword());
                    break;
                case "KAKAO":
                    connected = externalPlatformPort.connectKakaoAccount(storeId,
                            request.getUsername(), request.getPassword());
                    break;
                case "GOOGLE":
                    connected = externalPlatformPort.connectGoogleAccount(storeId,
                            request.getUsername(), request.getPassword());
                    break;
                case "HIORDER":
                    connected = externalPlatformPort.connectHiorderAccount(storeId,
                            request.getUsername(), request.getPassword());
                    break;
                default:
                    throw new BusinessException("지원하지 않는 플랫폼입니다: " + request.getPlatform());
            }

            if (connected) {
                // 연동 성공 이벤트 발행
                publishConnectEvent(storeId, request.getPlatform());

                log.info("외부 플랫폼 계정 연동 완료: storeId={}, platform={}", storeId, request.getPlatform());

                return ExternalConnectResponse.builder()
                        .success(true)
                        .message(request.getPlatform() + " 계정이 성공적으로 연동되었습니다")
                        .build();
            } else {
                throw new ExternalServiceException(request.getPlatform(), "계정 인증에 실패했습니다");
            }

        } catch (Exception e) {
            log.error("외부 플랫폼 계정 연동 실패: storeId={}, platform={}, error={}",
                    storeId, request.getPlatform(), e.getMessage(), e);

            return ExternalConnectResponse.builder()
                    .success(false)
                    .message("계정 연동에 실패했습니다: " + e.getMessage())
                    .build();
        }
    }

    @Override
    public ExternalConnectResponse disconnectPlatform(Long storeId, String platform) {
        log.info("외부 플랫폼 연동 해제: storeId={}, platform={}", storeId, platform);

        try {
            // 연동 해제 로직 구현
            boolean disconnected = externalPlatformPort.disconnectPlatform(storeId, platform);

            if (disconnected) {
                // 연동 해제 이벤트 발행
                publishDisconnectEvent(storeId, platform);

                return ExternalConnectResponse.builder()
                        .success(true)
                        .message(platform + " 플랫폼 연동이 해제되었습니다")
                        .build();
            } else {
                throw new ExternalServiceException(platform, "연동 해제에 실패했습니다");
            }

        } catch (Exception e) {
            log.error("외부 플랫폼 연동 해제 실패: storeId={}, platform={}, error={}",
                    storeId, platform, e.getMessage(), e);

            return ExternalConnectResponse.builder()
                    .success(false)
                    .message("연동 해제에 실패했습니다: " + e.getMessage())
                    .build();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ExternalConnectResponse getConnectedPlatforms(Long storeId) {
        log.info("연동된 플랫폼 목록 조회: storeId={}", storeId);

        try {
            // 연동된 플랫폼 목록 조회 로직
            // 실제로는 ExternalPlatformEntity에서 조회

            return ExternalConnectResponse.builder()
                    .success(true)
                    .message("연동된 플랫폼 목록을 조회했습니다")
                    .build();

        } catch (Exception e) {
            log.error("연동된 플랫폼 목록 조회 실패: storeId={}, error={}", storeId, e.getMessage(), e);

            return ExternalConnectResponse.builder()
                    .success(false)
                    .message("플랫폼 목록 조회에 실패했습니다: " + e.getMessage())
                    .build();
        }
    }

    // Private helper methods

    private void validateSyncRequest(Long storeId, ExternalSyncRequest request) {
        if (storeId == null) {
            throw new BusinessException("매장 ID는 필수입니다");
        }

        if (request.getPlatform() == null || request.getPlatform().trim().isEmpty()) {
            throw new BusinessException("플랫폼 정보는 필수입니다");
        }

        if (request.getExternalStoreId() == null || request.getExternalStoreId().trim().isEmpty()) {
            throw new BusinessException("외부 매장 ID는 필수입니다");
        }
    }

    private void validateConnectRequest(Long storeId, ExternalConnectRequest request) {
        if (storeId == null) {
            throw new BusinessException("매장 ID는 필수입니다");
        }

        if (request.getPlatform() == null || request.getPlatform().trim().isEmpty()) {
            throw new BusinessException("플랫폼 정보는 필수입니다");
        }

        if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
            throw new BusinessException("사용자명은 필수입니다");
        }

        if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            throw new BusinessException("비밀번호는 필수입니다");
        }
    }

    private void publishSyncEvent(Long storeId, String platform, int syncedCount) {
        try {
            // 동기화 이벤트 발행 로직
            log.info("동기화 이벤트 발행: storeId={}, platform={}, syncedCount={}",
                    storeId, platform, syncedCount);
        } catch (Exception e) {
            log.warn("동기화 이벤트 발행 실패: {}", e.getMessage());
        }
    }

    private void publishConnectEvent(Long storeId, String platform) {
        try {
            // 연동 이벤트 발행 로직
            log.info("연동 이벤트 발행: storeId={}, platform={}", storeId, platform);
        } catch (Exception e) {
            log.warn("연동 이벤트 발행 실패: {}", e.getMessage());
        }
    }

    private void publishDisconnectEvent(Long storeId, String platform) {
        try {
            // 연동 해제 이벤트 발행 로직
            log.info("연동 해제 이벤트 발행: storeId={}, platform={}", storeId, platform);
        } catch (Exception e) {
            log.warn("연동 해제 이벤트 발행 실패: {}", e.getMessage());
        }
    }
}