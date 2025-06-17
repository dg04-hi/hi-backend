package com.ktds.hi.store.biz.usecase.in;

import com.ktds.hi.store.infra.dto.ExternalSyncRequest;
import com.ktds.hi.store.infra.dto.ExternalSyncResponse;
import com.ktds.hi.store.infra.dto.ExternalConnectRequest;
import com.ktds.hi.store.infra.dto.ExternalConnectResponse;

import java.util.List;
import java.util.Map;

/**
 * 외부 연동 유스케이스 인터페이스
 * 외부 플랫폼 연동 관련 비즈니스 로직을 정의
 */
public interface ExternalIntegrationUseCase {

    /**
     * 외부 플랫폼 리뷰 동기화
     */
    ExternalSyncResponse syncReviews(Long storeId, ExternalSyncRequest request);

    /**
     * 외부 플랫폼 계정 연동
     */
    ExternalConnectResponse connectPlatform(Long storeId, ExternalConnectRequest request);

    /**
     * 외부 플랫폼 연동 해제
     */
    ExternalConnectResponse disconnectPlatform(Long storeId, String platform);

    /**
     * 연동된 플랫폼 목록 조회
     */
    ExternalConnectResponse getConnectedPlatforms(Long storeId);
}