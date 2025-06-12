package com.ktds.hi.store.biz.usecase.out;

import com.ktds.hi.store.domain.Store;

import java.util.List;
import java.util.Optional;

/**
 * 매장 리포지토리 포트 인터페이스
 * 매장 데이터 영속성 기능을 정의
 *
 * @author 하이오더 개발팀
 * @version 1.0.0
 */
public interface StoreRepositoryPort {

    /**
     * 점주 ID로 매장 목록 조회
     *
     * @param ownerId 점주 ID
     * @return 매장 목록
     */
    List<Store> findStoresByOwnerId(Long ownerId);

    /**
     * 매장 ID로 매장 조회
     *
     * @param storeId 매장 ID
     * @return 매장 정보 (Optional)
     */
    Optional<Store> findStoreById(Long storeId);

    /**
     * 매장 ID와 점주 ID로 매장 조회
     *
     * @param storeId 매장 ID
     * @param ownerId 점주 ID
     * @return 매장 정보 (Optional)
     */
    Optional<Store> findStoreByIdAndOwnerId(Long storeId, Long ownerId);

    /**
     * 매장 저장
     *
     * @param store 저장할 매장 정보
     * @return 저장된 매장 정보
     */
    Store saveStore(Store store);

    /**
     * 매장 삭제
     *
     * @param storeId 삭제할 매장 ID
     */
    void deleteStore(Long storeId);

    /**
     * 매장 검색
     *
     * @param searchCriteria 검색 조건
     * @return 검색된 매장 목록
     */
    List<Store> searchStores(StoreSearchCriteria searchCriteria);

    /**
     * 카테고리별 매장 목록 조회
     *
     * @param category 카테고리
     * @return 카테고리별 매장 목록
     */
    List<Store> findStoresByCategory(String category);

    /**
     * 위치 기반 매장 검색 (반경 내)
     *
     * @param latitude 위도
     * @param longitude 경도
     * @param radiusKm 반경 (킬로미터)
     * @return 반경 내 매장 목록
     */
    List<Store> findStoresWithinRadius(Double latitude, Double longitude, Double radiusKm);

    /**
     * 매장명 또는 주소로 검색
     *
     * @param keyword 검색 키워드
     * @return 검색된 매장 목록
     */
    List<Store> findStoresByKeyword(String keyword);

    /**
     * 활성 상태의 매장 목록 조회
     *
     * @return 활성 매장 목록
     */
    List<Store> findActiveStores();

    /**
     * 평점 기준 상위 매장 조회
     *
     * @param limit 조회할 매장 수
     * @return 상위 평점 매장 목록
     */
    List<Store> findTopRatedStores(Integer limit);

    /**
     * 매장 존재 여부 확인
     *
     * @param storeId 매장 ID
     * @return 존재 여부
     */
    boolean existsById(Long storeId);

    /**
     * 점주의 매장 수 조회
     *
     * @param ownerId 점주 ID
     * @return 매장 수
     */
    Long countStoresByOwnerId(Long ownerId);

    /**
     * 매장 상태 업데이트
     *
     * @param storeId 매장 ID
     * @param status 새로운 상태
     * @return 업데이트된 매장 정보
     */
    Store updateStoreStatus(Long storeId, String status);

    /**
     * 여러 매장 일괄 저장
     *
     * @param stores 저장할 매장 목록
     * @return 저장된 매장 목록
     */
    List<Store> saveStores(List<Store> stores);
}