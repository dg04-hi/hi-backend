package com.ktds.hi.store.biz.usecase.in;

import com.ktds.hi.store.infra.dto.response.StoreMenuListResponse;
import java.util.List;
import com.ktds.hi.store.infra.dto.request.MenuCreateRequest;
import com.ktds.hi.store.infra.dto.request.MenuUpdateRequest;
import com.ktds.hi.store.infra.dto.response.MenuCreateResponse;
import com.ktds.hi.store.infra.dto.response.MenuDetailResponse;
import com.ktds.hi.store.infra.dto.response.MenuUpdateResponse;

public interface MenuUseCase {

    /**
     * 매장 메뉴 목록 조회
     *
     * @param storeId 매장 ID
     * @return 메뉴 목록 응답
     */
    List<StoreMenuListResponse> getStoreMenus(Long storeId);


    /**
     * 메뉴 상세 조회
     *
     * @param menuId 메뉴 ID
     * @return 메뉴 상세 정보
     */
    MenuDetailResponse getMenuDetail(Long menuId);

    /**
     * 메뉴 등록
     *
     * @param ownerId 점주 ID
     * @param storeId 매장 ID
     * @param request 메뉴 등록 요청
     * @return 메뉴 등록 응답
     */
    MenuCreateResponse createMenu(Long ownerId, Long storeId, MenuCreateRequest request);

    /**
     * 메뉴 수정
     *
     * @param ownerId 점주 ID
     * @param menuId 메뉴 ID
     * @param request 메뉴 수정 요청
     * @return 메뉴 수정 응답
     */
    MenuUpdateResponse updateMenu(Long ownerId, Long menuId, MenuUpdateRequest request);

    /**
     * 메뉴 삭제
     *
     * @param ownerId 점주 ID
     * @param menuId 메뉴 ID
     */
    void deleteMenu(Long ownerId, Long menuId);

    /**
     * 메뉴 가용성 변경
     *
     * @param ownerId 점주 ID
     * @param menuId 메뉴 ID
     * @param isAvailable 가용성 여부
     */
    void updateMenuAvailability(Long ownerId, Long menuId, Boolean isAvailable);

    /**
     * 메뉴 카테고리별 조회
     *
     * @param storeId 매장 ID
     * @param category 카테고리
     * @return 카테고리별 메뉴 목록
     */
    List<StoreMenuListResponse> getMenusByCategory(Long storeId, String category);
}