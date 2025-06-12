package com.ktds.hi.store.biz.usecase.out;

import com.ktds.hi.store.biz.domain.Menu;

import java.util.List;
import java.util.Optional;

/**
 * 메뉴 리포지토리 포트 인터페이스
 * 메뉴 데이터 영속성 기능을 정의
 *
 * @author 하이오더 개발팀
 * @version 1.0.0
 */
public interface MenuRepositoryPort {

    /**
     * 매장 ID로 메뉴 목록 조회
     *
     * @param storeId 매장 ID
     * @return 메뉴 목록
     */
    List<Menu> findMenusByStoreId(Long storeId);

    /**
     * 메뉴 ID로 메뉴 조회
     *
     * @param menuId 메뉴 ID
     * @return 메뉴 정보 (Optional)
     */
    Optional<Menu> findMenuById(Long menuId);

    /**
     * 메뉴 저장
     *
     * @param menu 저장할 메뉴 정보
     * @return 저장된 메뉴 정보
     */
    Menu saveMenu(Menu menu);

    /**
     * 메뉴 삭제
     *
     * @param menuId 삭제할 메뉴 ID
     */
    void deleteMenu(Long menuId);

    /**
     * 매장의 이용 가능한 메뉴 목록 조회
     *
     * @param storeId 매장 ID
     * @return 이용 가능한 메뉴 목록
     */
    List<Menu> findAvailableMenusByStoreId(Long storeId);

    /**
     * 카테고리별 메뉴 목록 조회
     *
     * @param storeId 매장 ID
     * @param category 카테고리
     * @return 카테고리별 메뉴 목록
     */
    List<Menu> findMenusByStoreIdAndCategory(Long storeId, String category);

    /**
     * 메뉴 여러 개 저장
     *
     * @param menus 저장할 메뉴 목록
     * @return 저장된 메뉴 목록
     */
    List<Menu> saveMenus(List<Menu> menus);

    /**
     * 매장의 모든 메뉴 삭제
     *
     * @param storeId 매장 ID
     */
    void deleteMenusByStoreId(Long storeId);
}