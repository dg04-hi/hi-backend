// store/src/main/java/com/ktds/hi/store/infra/controller/StoreController.java
package com.ktds.hi.store.infra.controller;

import com.ktds.hi.store.biz.service.StoreService;
import com.ktds.hi.store.biz.usecase.in.MenuUseCase;
import com.ktds.hi.store.biz.usecase.in.StoreUseCase;
import com.ktds.hi.store.domain.Store;
import com.ktds.hi.store.infra.dto.*;
import com.ktds.hi.common.dto.ApiResponse;
import com.ktds.hi.common.security.JwtTokenProvider;
import com.ktds.hi.store.infra.dto.response.StoreListResponse;
import com.ktds.hi.store.infra.dto.response.StoreMenuListResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.List;

/**
 * 매장 관리 컨트롤러
 * 매장 등록, 수정, 삭제, 조회 기능 제공
 *
 * @author 하이오더 개발팀
 * @version 1.0.0
 */
@Tag(name = "매장 관리", description = "매장 등록, 수정, 삭제, 조회 API")
@Slf4j
@RestController
@RequestMapping("/api/stores")
@RequiredArgsConstructor
@Validated
public class StoreController {

    private final StoreUseCase storeUseCase;
    private final JwtTokenProvider jwtTokenProvider;
    private final MenuUseCase menuUseCase;


    @Operation(summary = "매장 등록", description = "새로운 매장을 등록합니다.")
    @PostMapping
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<ApiResponse<StoreCreateResponse>> createStore(
            @Valid @RequestBody StoreCreateRequest request,
            HttpServletRequest httpRequest) {

        Long ownerId = jwtTokenProvider.extractOwnerInfo(httpRequest);
        StoreCreateResponse response = storeUseCase.createStore(ownerId, request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "매장이 성공적으로 등록되었습니다."));
    }



    @Operation(summary = "내 매장 목록 조회", description = "점주가 등록한 매장 목록을 조회합니다.")
    @GetMapping("/my")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<ApiResponse<List<MyStoreListResponse>>> getMyStores(
            HttpServletRequest httpRequest) {

        Long ownerId = jwtTokenProvider.extractOwnerInfo(httpRequest);
        List<MyStoreListResponse> responses = storeUseCase.getMyStores(ownerId);

        return ResponseEntity.ok(ApiResponse.success(responses, "내 매장 목록 조회 완료"));
    }



    @GetMapping("/stores/all")
    @Operation(summary = "매장 전체 리스트")
    public ResponseEntity<ApiResponse<List<StoreListResponse>>> getAllStores() {

        List<StoreListResponse> responses = storeUseCase.getAllStores();
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @Operation(summary = "매장 조회", description = "가게 검색한 매장 목록을 조회합니다.")
    @GetMapping("/search/storeName/{storeName}")
    public ResponseEntity<ApiResponse<List<StoreListResponse>>> getSearchStores(
            HttpServletRequest httpRequest, @PathVariable String storeName) {

        List<StoreListResponse> responses = storeUseCase.getSearchStoreName(storeName);

        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @GetMapping("/stores/category/{category}")
    @Operation(summary = "카테고리에 해당하는 매장")
    public ResponseEntity<ApiResponse<List<StoreListResponse>>> getStoreCategories(@PathVariable String category) {

        List<StoreListResponse> responses = storeUseCase.getCategoryStores(category);
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @GetMapping("/stores/{storeId}/tags")
    @Operation(summary = "매장 전체 리스트")
    public ResponseEntity<String> getStoreTags(@PathVariable Long storeId) {

        String tagsJson  = storeUseCase.getAllTags(storeId);
        return ResponseEntity.ok(tagsJson);
    }


    @Operation(summary = "매장 상세 조회", description = "매장의 상세 정보를 조회합니다.")
    @GetMapping("/{storeId}")
    public ResponseEntity<ApiResponse<StoreDetailResponse>> getStoreDetail(
            @Parameter(description = "매장 ID") @PathVariable Long storeId) {

        StoreDetailResponse response = storeUseCase.getStoreDetail(storeId);
        return ResponseEntity.ok(ApiResponse.success(response, "매장 상세 정보 조회 완료"));
    }

    @Operation(summary = "매장 정보 수정", description = "매장 정보를 수정합니다.")
    @PutMapping("/{storeId}")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<ApiResponse<StoreUpdateResponse>> updateStore(
            @Parameter(description = "매장 ID") @PathVariable Long storeId,
            @Valid @RequestBody StoreUpdateRequest request,
            HttpServletRequest httpRequest) {

        Long ownerId = jwtTokenProvider.extractOwnerInfo(httpRequest);
        StoreUpdateResponse response = storeUseCase.updateStore(storeId, ownerId, request);

        return ResponseEntity.ok(ApiResponse.success(response, "매장 정보 수정 완료"));
    }

    @Operation(summary = "매장 삭제", description = "매장을 삭제합니다.")
    @DeleteMapping("/{storeId}")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<ApiResponse<StoreDeleteResponse>> deleteStore(
            @Parameter(description = "매장 ID") @PathVariable Long storeId,
            HttpServletRequest httpRequest) {

        Long ownerId = jwtTokenProvider.extractOwnerInfo(httpRequest);
        StoreDeleteResponse response = storeUseCase.deleteStore(storeId, ownerId);

        return ResponseEntity.ok(ApiResponse.success(response, "매장 삭제 완료"));
    }

    @Operation(summary = "매장 검색", description = "조건에 따라 매장을 검색합니다.")
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<StoreSearchResponse>>> searchStores(
            @Parameter(description = "검색 키워드") @RequestParam(required = false) String keyword,
            @Parameter(description = "카테고리") @RequestParam(required = false) String category,
            @Parameter(description = "태그") @RequestParam(required = false) String tags,
            @Parameter(description = "위도") @RequestParam(required = false) Double latitude,
            @Parameter(description = "경도") @RequestParam(required = false) Double longitude,
            @Parameter(description = "검색 반경(km)") @RequestParam(defaultValue = "5") Integer radius,
            @Parameter(description = "페이지 번호") @RequestParam(defaultValue = "0") Integer page,
            @Parameter(description = "페이지 크기") @RequestParam(defaultValue = "20") Integer size) {

        List<StoreSearchResponse> responses = storeUseCase.searchStores(
                keyword, category, tags, latitude, longitude, radius, page, size);

        return ResponseEntity.ok(ApiResponse.success(responses, "매장 검색 완료"));
    }

    @GetMapping("/{storeId}/menus")
    @Operation(summary = "매장 메뉴 목록 조회 (매장 상세에서)", description = "매장 상세 정보 조회 시 메뉴 목록을 함께 제공")
    public ResponseEntity<ApiResponse<List<StoreMenuListResponse>>> getStoreMenusFromStore(
            @Parameter(description = "매장 ID") @PathVariable Long storeId) {

        List<StoreMenuListResponse> response = menuUseCase.getStoreMenus(storeId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }





    @GetMapping("/{storeId}/menus/popular")
    @Operation(summary = "매장 인기 메뉴 조회", description = "매장의 인기 메뉴(주문 횟수 기준)를 조회합니다.")
    public ResponseEntity<ApiResponse<List<StoreMenuListResponse>>> getPopularMenus(
            @Parameter(description = "매장 ID") @PathVariable Long storeId,
            @Parameter(description = "조회할 메뉴 개수") @RequestParam(defaultValue = "5") int limit) {

        List<StoreMenuListResponse> response = menuUseCase.getStoreMenus(storeId);
        // 인기 메뉴 조회 로직 추가 필요
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}