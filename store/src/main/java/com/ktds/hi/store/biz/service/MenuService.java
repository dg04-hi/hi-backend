// store/src/main/java/com/ktds/hi/store/biz/service/MenuService.java
package com.ktds.hi.store.biz.service;

import com.ktds.hi.common.dto.ResponseCode;
import com.ktds.hi.common.exception.BusinessException;
import com.ktds.hi.store.domain.Menu;
import com.ktds.hi.store.domain.Store;
import com.ktds.hi.store.biz.usecase.in.MenuUseCase;
import com.ktds.hi.store.biz.usecase.out.MenuRepositoryPort;
import com.ktds.hi.store.biz.usecase.out.StoreRepositoryPort;
import com.ktds.hi.store.infra.dto.request.MenuCreateRequest;
import com.ktds.hi.store.infra.dto.request.MenuUpdateRequest;
import com.ktds.hi.store.infra.dto.response.MenuCreateResponse;
import com.ktds.hi.store.infra.dto.response.MenuDetailResponse;
import com.ktds.hi.store.infra.dto.response.MenuUpdateResponse;
import com.ktds.hi.store.infra.dto.response.StoreMenuListResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Qualifier;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 메뉴 서비스 구현체
 * 메뉴 관련 비즈니스 로직을 처리
 *
 * @author 하이오더 개발팀
 * @version 1.0.0
 */
@Slf4j
@Service
//@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MenuService implements MenuUseCase {

    private final MenuRepositoryPort menuRepositoryPort;
    private final StoreRepositoryPort storeRepositoryPort;

    public MenuService(@Qualifier("menuJpaAdapter") MenuRepositoryPort menuRepositoryPort,
                       StoreRepositoryPort storeRepositoryPort) {
        this.menuRepositoryPort = menuRepositoryPort;
        this.storeRepositoryPort = storeRepositoryPort;
    }
    @Override
    public List<StoreMenuListResponse> getStoreMenus(Long storeId) {
        log.info("매장 메뉴 목록 조회 시작 - storeId: {}", storeId);

        // 매장 존재 여부 확인
        validateStoreExists(storeId);

        List<Menu> menus = menuRepositoryPort.findMenusByStoreId(storeId);

        return menus.stream()
                .map(this::mapToStoreMenuListResponse)
                .collect(Collectors.toList());
    }


    @Override
    public MenuDetailResponse getMenuDetail(Long menuId) {
        log.info("메뉴 상세 조회 시작 - menuId: {}", menuId);

        Menu menu = menuRepositoryPort.findMenuById(menuId)
                .orElseThrow(() -> new BusinessException(ResponseCode.NOT_FOUND, "메뉴를 찾을 수 없습니다."));

        return mapToMenuDetailResponse(menu);
    }

    @Override
    @Transactional
    public MenuCreateResponse createMenu(Long ownerId, Long storeId, MenuCreateRequest request) {
        log.info("메뉴 등록 시작 - ownerId: {}, storeId: {}, menuName: {}", ownerId, storeId, request.getMenuName());

        // 매장 소유권 확인
        validateStoreOwnership(ownerId, storeId);

        // 메뉴 생성
        Menu menu = Menu.builder()
                .storeId(storeId)
                .menuName(request.getMenuName())
                .description(request.getDescription())
                .price(request.getPrice())
                .category(request.getCategory())
                .imageUrl(request.getImageUrl())
                .available(request.getIsAvailable() != null ? request.getIsAvailable() : true)
                .orderCount(0)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // 메뉴 유효성 검증
        if (!menu.isValid()) {
            throw new BusinessException(ResponseCode.INVALID_INPUT, "메뉴 정보가 올바르지 않습니다.");
        }

        Menu savedMenu = menuRepositoryPort.saveMenu(menu);

        log.info("메뉴 등록 완료 - menuId: {}", savedMenu.getId());

        return MenuCreateResponse.builder()
                .menuId(savedMenu.getId())
                .message("메뉴가 성공적으로 등록되었습니다.")
                .build();
    }

    @Override
    @Transactional
    public MenuUpdateResponse updateMenu(Long ownerId, Long menuId, MenuUpdateRequest request) {
        log.info("메뉴 수정 시작 - ownerId: {}, menuId: {}", ownerId, menuId);

        // 메뉴 조회 및 소유권 확인
        Menu existingMenu = menuRepositoryPort.findMenuById(menuId)
                .orElseThrow(() -> new BusinessException(ResponseCode.NOT_FOUND, "메뉴를 찾을 수 없습니다."));

        validateStoreOwnership(ownerId, existingMenu.getStoreId());

        // 메뉴 정보 업데이트
        Menu updatedMenu = existingMenu.updateInfo(
                request.getMenuName() != null ? request.getMenuName() : existingMenu.getMenuName(),
                request.getDescription() != null ? request.getDescription() : existingMenu.getDescription(),
                request.getPrice() != null ? request.getPrice() : existingMenu.getPrice()
        );

        if (request.getCategory() != null) {
            updatedMenu = updatedMenu.updateCategory(request.getCategory());
        }

        if (request.getImageUrl() != null) {
            updatedMenu = updatedMenu.updateImage(request.getImageUrl());
        }

        if (request.getIsAvailable() != null) {
            updatedMenu = updatedMenu.setAvailable(request.getIsAvailable());
        }

        Menu savedMenu = menuRepositoryPort.saveMenu(updatedMenu);

        log.info("메뉴 수정 완료 - menuId: {}", savedMenu.getId());

        return MenuUpdateResponse.builder()
                .menuId(savedMenu.getId())
                .message("메뉴가 성공적으로 수정되었습니다.")
                .build();
    }

    @Override
    @Transactional
    public void deleteMenu(Long ownerId, Long menuId) {
        log.info("메뉴 삭제 시작 - ownerId: {}, menuId: {}", ownerId, menuId);

        // 메뉴 조회 및 소유권 확인
        Menu existingMenu = menuRepositoryPort.findMenuById(menuId)
                .orElseThrow(() -> new BusinessException(ResponseCode.NOT_FOUND, "메뉴를 찾을 수 없습니다."));

        validateStoreOwnership(ownerId, existingMenu.getStoreId());

        menuRepositoryPort.deleteMenu(menuId);

        log.info("메뉴 삭제 완료 - menuId: {}", menuId);
    }

    @Override
    @Transactional
    public void updateMenuAvailability(Long ownerId, Long menuId, Boolean isAvailable) {
        log.info("메뉴 가용성 변경 시작 - ownerId: {}, menuId: {}, isAvailable: {}", ownerId, menuId, isAvailable);

        // 메뉴 조회 및 소유권 확인
        Menu existingMenu = menuRepositoryPort.findMenuById(menuId)
                .orElseThrow(() -> new BusinessException(ResponseCode.NOT_FOUND, "메뉴를 찾을 수 없습니다."));

        validateStoreOwnership(ownerId, existingMenu.getStoreId());

        Menu updatedMenu = existingMenu.setAvailable(isAvailable);
        menuRepositoryPort.saveMenu(updatedMenu);

        log.info("메뉴 가용성 변경 완료 - menuId: {}, isAvailable: {}", menuId, isAvailable);
    }

    @Override
    public List<StoreMenuListResponse> getMenusByCategory(Long storeId, String category) {
        log.info("카테고리별 메뉴 조회 시작 - storeId: {}, category: {}", storeId, category);

        validateStoreExists(storeId);

        List<Menu> menus = menuRepositoryPort.findMenusByStoreIdAndCategory(storeId, category);

        return menus.stream()
                .map(this::mapToStoreMenuListResponse)
                .collect(Collectors.toList());
    }

    /**
     * 매장 존재 여부 확인
     */
    private void validateStoreExists(Long storeId) {
        if (!storeRepositoryPort.findStoreById(storeId).isPresent()) {
            throw new BusinessException(ResponseCode.STORE_NOT_FOUND, "매장을 찾을 수 없습니다.");
        }
    }

    /**
     * 매장 소유권 확인
     */
    private void validateStoreOwnership(Long ownerId, Long storeId) {
        Store store = storeRepositoryPort.findStoreByIdAndOwnerId(storeId, ownerId)
                .orElseThrow(() -> new BusinessException(ResponseCode.ACCESS_DENIED, "해당 매장에 대한 권한이 없습니다."));
    }

    /**
     * Menu를 StoreMenuListResponse로 변환
     */
    private StoreMenuListResponse mapToStoreMenuListResponse(Menu menu) {
        return StoreMenuListResponse.builder()
                .menuId(menu.getId())
                .menuName(menu.getMenuName())
                .description(menu.getDescription())
                .price(menu.getPrice())
                .category(menu.getCategory())
                .imageUrl(menu.getImageUrl())
                .isAvailable(menu.getAvailable())
                .orderCount(menu.getOrderCount())
                .build();
    }

    /**
     * Menu를 MenuDetailResponse로 변환
     */
    private MenuDetailResponse mapToMenuDetailResponse(Menu menu) {
        return MenuDetailResponse.builder()
                .menuId(menu.getId())
                .storeId(menu.getStoreId())
                .menuName(menu.getMenuName())
                .description(menu.getDescription())
                .price(menu.getPrice())
                .category(menu.getCategory())
                .imageUrl(menu.getImageUrl())
                .isAvailable(menu.getAvailable())
                .orderCount(menu.getOrderCount())
                .createdAt(menu.getCreatedAt())
                .updatedAt(menu.getUpdatedAt())
                .build();
    }
}