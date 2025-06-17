package com.ktds.hi.store.biz.usecase.out;

import java.util.List;
import java.util.Map;

/**
 * 외부 플랫폼 포트 인터페이스
 * 외부 플랫폼 연동 기능을 정의
 *
 * @author 하이오더 개발팀
 * @version 1.0.0
 */
public interface ExternalPlatformPort {

    /**
     * 네이버 리뷰 동기화
     *
     * @param storeId 매장 ID
     * @param externalStoreId 외부 매장 ID
     * @return 동기화된 리뷰 수
     */
    int syncNaverReviews(Long storeId, String externalStoreId);

    /**
     * 카카오 리뷰 동기화
     *
     * @param storeId 매장 ID
     * @param externalStoreId 외부 매장 ID
     * @return 동기화된 리뷰 수
     */
    int syncKakaoReviews(Long storeId, String externalStoreId);

    /**
     * 구글 리뷰 동기화
     *
     * @param storeId 매장 ID
     * @param externalStoreId 외부 매장 ID
     * @return 동기화된 리뷰 수
     */
    int syncGoogleReviews(Long storeId, String externalStoreId);

    /**
     * 하이오더 리뷰 동기화
     *
     * @param storeId 매장 ID
     * @param externalStoreId 외부 매장 ID
     * @return 동기화된 리뷰 수
     */
    int syncHiorderReviews(Long storeId, String externalStoreId);

    /**
     * 네이버 계정 연동
     *
     * @param storeId 매장 ID
     * @param username 사용자명
     * @param password 비밀번호
     * @return 연동 성공 여부
     */
    boolean connectNaverAccount(Long storeId, String username, String password);

    /**
     * 카카오 계정 연동
     *
     * @param storeId 매장 ID
     * @param username 사용자명
     * @param password 비밀번호
     * @return 연동 성공 여부
     */
    boolean connectKakaoAccount(Long storeId, String username, String password);

    /**
     * 구글 계정 연동
     *
     * @param storeId 매장 ID
     * @param username 사용자명
     * @param password 비밀번호
     * @return 연동 성공 여부
     */
    boolean connectGoogleAccount(Long storeId, String username, String password);

    /**
     * 하이오더 계정 연동
     *
     * @param storeId 매장 ID
     * @param username 사용자명
     * @param password 비밀번호
     * @return 연동 성공 여부
     */
    boolean connectHiorderAccount(Long storeId, String username, String password);

    /**
     * 외부 플랫폼 연동 해제
     *
     * @param storeId 매장 ID
     * @param platform 플랫폼명 (NAVER, KAKAO, GOOGLE, HIORDER)
     * @return 연동 해제 성공 여부
     */
    boolean disconnectPlatform(Long storeId, String platform);

    public List<Map<String, Object>> getTempReviews(Long storeId, String platform);
}