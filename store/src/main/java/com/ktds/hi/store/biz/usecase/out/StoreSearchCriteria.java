package com.ktds.hi.store.biz.usecase.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 매장 검색 조건 클래스
 * 매장 검색 시 사용되는 필터 조건들을 정의
 *
 * @author 하이오더 개발팀
 * @version 1.0.0
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StoreSearchCriteria {

    /**
     * 검색 키워드 (매장명, 주소)
     */
    private String keyword;

    /**
     * 카테고리 필터
     */
    private String category;

    /**
     * 태그 필터 목록
     */
    private List<String> tags;

    /**
     * 검색 중심 위도
     */
    private Double latitude;

    /**
     * 검색 중심 경도
     */
    private Double longitude;

    /**
     * 검색 반경 (킬로미터)
     */
    private Double radiusKm;

    /**
     * 최소 평점
     */
    private Double minRating;

    /**
     * 최대 평점
     */
    private Double maxRating;

    /**
     * 매장 상태 필터
     */
    private String status;

    /**
     * 정렬 기준 (rating, distance, reviewCount 등)
     */
    private String sortBy;

    /**
     * 정렬 방향 (ASC, DESC)
     */
    private String sortDirection;

    /**
     * 페이지 번호 (0부터 시작)
     */
    private Integer page;

    /**
     * 페이지 크기
     */
    private Integer size;

    /**
     * 검색 조건 유효성 검증
     */
    public boolean isValid() {
        // 위치 기반 검색인 경우 위도, 경도, 반경이 모두 있어야 함
        if (latitude != null || longitude != null || radiusKm != null) {
            return latitude != null && longitude != null && radiusKm != null &&
                    latitude >= -90 && latitude <= 90 &&
                    longitude >= -180 && longitude <= 180 &&
                    radiusKm > 0;
        }

        // 키워드나 카테고리 중 하나는 있어야 함
        return (keyword != null && !keyword.trim().isEmpty()) ||
                (category != null && !category.trim().isEmpty()) ||
                (tags != null && !tags.isEmpty());
    }

    /**
     * 위치 기반 검색 여부 확인
     */
    public boolean hasLocationFilter() {
        return latitude != null && longitude != null && radiusKm != null;
    }

    /**
     * 키워드 검색 여부 확인
     */
    public boolean hasKeywordFilter() {
        return keyword != null && !keyword.trim().isEmpty();
    }

    /**
     * 카테고리 필터 여부 확인
     */
    public boolean hasCategoryFilter() {
        return category != null && !category.trim().isEmpty();
    }

    /**
     * 태그 필터 여부 확인
     */
    public boolean hasTagFilter() {
        return tags != null && !tags.isEmpty();
    }

    /**
     * 평점 필터 여부 확인
     */
    public boolean hasRatingFilter() {
        return minRating != null || maxRating != null;
    }

    /**
     * 정렬 조건 여부 확인
     */
    public boolean hasSortFilter() {
        return sortBy != null && !sortBy.trim().isEmpty();
    }

    /**
     * 기본 페이징 설정 적용
     */
    public StoreSearchCriteria withDefaultPaging() {
        return StoreSearchCriteria.builder()
                .keyword(this.keyword)
                .category(this.category)
                .tags(this.tags)
                .latitude(this.latitude)
                .longitude(this.longitude)
                .radiusKm(this.radiusKm)
                .minRating(this.minRating)
                .maxRating(this.maxRating)
                .status(this.status)
                .sortBy(this.sortBy != null ? this.sortBy : "rating")
                .sortDirection(this.sortDirection != null ? this.sortDirection : "DESC")
                .page(this.page != null ? this.page : 0)
                .size(this.size != null ? this.size : 20)
                .build();
    }
}