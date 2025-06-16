package com.ktds.hi.store.infra.controller;

import com.ktds.hi.common.dto.ApiResponse;
import com.ktds.hi.common.security.JwtTokenProvider;
import com.ktds.hi.store.biz.usecase.in.MenuUseCase;
import com.ktds.hi.store.infra.dto.request.MenuCreateRequest;
import com.ktds.hi.store.infra.dto.request.MenuUpdateRequest;
import com.ktds.hi.store.infra.dto.request.MenuAvailabilityRequest;
import com.ktds.hi.store.infra.dto.response.MenuCreateResponse;
import com.ktds.hi.store.infra.dto.response.MenuDetailResponse;
import com.ktds.hi.store.infra.dto.response.MenuUpdateResponse;
import com.ktds.hi.store.infra.dto.response.StoreMenuListResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;

/**
 * 메뉴 관리 컨트롤러
 * 메뉴 관련 REST API 엔드포인트를 제공
 *
 * @author 하이오더 개발팀
 * @version 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/menus")
@RequiredArgsConstructor
@Tag(name = "메뉴 관리", description = "메뉴 등록, 조회, 수정, 삭제 API")
public class MenuController {

    private final MenuUseCase menuUseCase;
    private final JwtTokenProvider jwtTokenProvider;

    @GetMapping("/stores/{storeId}")
    @Operation(summary = "매장 메뉴 목록 조회", description = "특정 매장의 모든 메뉴를 조회합니다.")
    public ResponseEntity<ApiResponse<List<StoreMenuListResponse>>> getStoreMenus(
            @Parameter(description = "매장 ID") @PathVariable Long storeId) {

        log.info("매장 메뉴 목록 조회 요청 - storeId: {}", storeId);

        List<StoreMenuListResponse> response = menuUseCase.getStoreMenus(storeId);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{menuId}")
    @Operation(summary = "메뉴 상세 조회", description = "특정 메뉴의 상세 정보를 조회합니다.")
    public ResponseEntity<ApiResponse<MenuDetailResponse>> getMenuDetail(
            @Parameter(description = "메뉴 ID") @PathVariable Long menuId) {

        log.info("메뉴 상세 조회 요청 - menuId: {}", menuId);

        MenuDetailResponse response = menuUseCase.getMenuDetail(menuId);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/stores/{storeId}")
    @Operation(summary = "메뉴 등록", description = "새로운 메뉴를 등록합니다.")
    public ResponseEntity<ApiResponse<MenuCreateResponse>> createMenu(
            @Parameter(description = "매장 ID") @PathVariable Long storeId,
            @Parameter(description = "메뉴 등록 정보") @Valid @RequestBody MenuCreateRequest request,
            HttpServletRequest httpRequest) {

        Long ownerId = jwtTokenProvider.extractOwnerIdFromRequest(httpRequest);
        log.info("메뉴 등록 요청 - ownerId: {}, storeId: {}, menuName: {}", ownerId, storeId, request.getMenuName());

        MenuCreateResponse response = menuUseCase.createMenu(ownerId, storeId, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response));
    }

    @PutMapping("/{menuId}")
    @Operation(summary = "메뉴 수정", description = "기존 메뉴 정보를 수정합니다.")
    public ResponseEntity<ApiResponse<MenuUpdateResponse>> updateMenu(
            @Parameter(description = "메뉴 ID") @PathVariable Long menuId,
            @Parameter(description = "메뉴 수정 정보") @Valid @RequestBody MenuUpdateRequest request,
            HttpServletRequest httpRequest) {

        Long ownerId = jwtTokenProvider.extractOwnerIdFromRequest(httpRequest);
        log.info("메뉴 수정 요청 - ownerId: {}, menuId: {}", ownerId, menuId);

        MenuUpdateResponse response = menuUseCase.updateMenu(ownerId, menuId, request);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @DeleteMapping("/{menuId}")
    @Operation(summary = "메뉴 삭제", description = "메뉴를 삭제합니다.")
    public ResponseEntity<ApiResponse<Void>> deleteMenu(
            @Parameter(description = "메뉴 ID") @PathVariable Long menuId,
            HttpServletRequest httpRequest) {

        Long ownerId = jwtTokenProvider.extractOwnerIdFromRequest(httpRequest);
        log.info("메뉴 삭제 요청 - ownerId: {}, menuId: {}", ownerId, menuId);

        menuUseCase.deleteMenu(ownerId, menuId);

        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @PatchMapping("/{menuId}/availability")
    @Operation(summary = "메뉴 가용성 변경", description = "메뉴의 판매 가능 여부를 변경합니다.")
    public ResponseEntity<ApiResponse<Void>> updateMenuAvailability(
            @Parameter(description = "메뉴 ID") @PathVariable Long menuId,
            @Parameter(description = "가용성 변경 정보") @Valid @RequestBody MenuAvailabilityRequest request,
            HttpServletRequest httpRequest) {

        Long ownerId = jwtTokenProvider.extractOwnerIdFromRequest(httpRequest);
        log.info("메뉴 가용성 변경 요청 - ownerId: {}, menuId: {}, isAvailable: {}", ownerId, menuId, request.getIsAvailable());

        menuUseCase.updateMenuAvailability(ownerId, menuId, request.getIsAvailable());

        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @GetMapping("/stores/{storeId}/categories/{category}")
    @Operation(summary = "카테고리별 메뉴 조회", description = "특정 매장의 카테고리별 메뉴를 조회합니다.")
    public ResponseEntity<ApiResponse<List<StoreMenuListResponse>>> getMenusByCategory(
            @Parameter(description = "매장 ID") @PathVariable Long storeId,
            @Parameter(description = "메뉴 카테고리") @PathVariable String category) {

        log.info("카테고리별 메뉴 조회 요청 - storeId: {}, category: {}", storeId, category);

        List<StoreMenuListResponse> response = menuUseCase.getMenusByCategory(storeId, category);

        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
