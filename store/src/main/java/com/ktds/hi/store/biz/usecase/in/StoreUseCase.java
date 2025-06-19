package com.ktds.hi.store.biz.usecase.in;

import com.ktds.hi.store.infra.dto.*;
import com.ktds.hi.store.infra.dto.response.StoreListResponse;

import java.util.List;

/**
 * 매장 관리 유스케이스 인터페이스
 * Clean Architecture의 Input Port
 *
 * @author 하이오더 개발팀
 * @version 1.0.0
 */
public interface StoreUseCase {

    /**
     * 매장 등록
     *
     * @param ownerId 점주 ID
     * @param request 매장 등록 요청 정보
     * @return 매장 등록 응답
     */
    StoreCreateResponse createStore(Long ownerId, StoreCreateRequest request);

    /**
     * 내 매장 목록 조회
     *
     * @param ownerId 점주 ID
     * @return 내 매장 목록
     */
    List<MyStoreListResponse> getMyStores(Long ownerId);

    List<StoreListResponse> getAllStores();

    List<StoreListResponse> getCategoryStores(String category);

    List<StoreListResponse> getSearchStoreName(String storeName);

    String getAllTags(Long storeId);

    /**
     * 매장 상세 조회
     *
     * @param storeId 매장 ID
     * @return 매장 상세 정보
     */
    StoreDetailResponse getStoreDetail(Long storeId);

    /**
     * 매장 정보 수정
     *
     * @param storeId 매장 ID
     * @param ownerId 점주 ID
     * @param request 매장 수정 요청 정보
     * @return 매장 수정 응답
     */
    StoreUpdateResponse updateStore(Long storeId, Long ownerId, StoreUpdateRequest request);

    /**
     * 매장 삭제
     *
     * @param storeId 매장 ID
     * @param ownerId 점주 ID
     * @return 매장 삭제 응답
     */
    StoreDeleteResponse deleteStore(Long storeId, Long ownerId);

    /**
     * 매장 검색
     *
     * @param keyword 검색 키워드
     * @param category 카테고리
     * @param tags 태그
     * @param latitude 위도
     * @param longitude 경도
     * @param radius 검색 반경(km)
     * @param page 페이지 번호
     * @param size 페이지 크기
     * @return 검색된 매장 목록
     */
    List<StoreSearchResponse> searchStores(String keyword, String category, String tags,
                                           Double latitude, Double longitude, Integer radius,
                                           Integer page, Integer size);


}
