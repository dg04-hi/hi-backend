package com.ktds.hi.store.infra.gateway.repository;

import com.ktds.hi.store.infra.gateway.entity.StoreEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 매장 JPA 리포지토리 인터페이스
 * 매장 데이터의 CRUD 작업을 담당
 *
 * @author 하이오더 개발팀
 * @version 1.0.0
 */
@Repository
public interface StoreJpaRepository extends JpaRepository<StoreEntity, Long> {

    @Query("SELECT s FROM StoreEntity s WHERE s.status = 'ACTIVE' ORDER BY s.rating DESC")
    Page<StoreEntity> findAllByOrderByRatingDesc(Pageable pageable);


    /**
     * 점주 ID로 매장 목록 조회
     */
    List<StoreEntity> findByOwnerId(Long ownerId);


    /**
     * 매장 ID와 점주 ID로 매장 조회
     */
    Optional<StoreEntity> findByIdAndOwnerId(Long id, Long ownerId);

    /**
     * 매장명 또는 주소로 검색
     */
    List<StoreEntity> findByStoreNameContainingOrAddressContaining(String storeName, String address);

    /**
     * 카테고리로 매장 조회
     */
    List<StoreEntity> findByCategory(String category);

    /**
     * 상태로 매장 조회
     */
    List<StoreEntity> findByStatus(String status);

    /**
     * 평점 기준 내림차순으로 매장 조회
     */
    @Query(value = "SELECT DISTINCT s.* FROM stores s " +
            "WHERE EXISTS (SELECT 1 FROM store_tags st " +
            "WHERE st.store_id = s.id AND st.tag_name IN :tagNames) " +
            "AND s.status = 'ACTIVE'", nativeQuery = true)
    List<StoreEntity> findByTagNamesIn(@Param("tagNames") List<String> tagNames);

    @Query(value = "SELECT s.* FROM stores s " +
            "WHERE (SELECT COUNT(DISTINCT st.tag_name) FROM store_tags st " +
            "WHERE st.store_id = s.id AND st.tag_name IN :tagNames) = :tagCount " +
            "AND s.status = 'ACTIVE'", nativeQuery = true)
    List<StoreEntity> findByAllTagNames(@Param("tagNames") List<String> tagNames,
                                        @Param("tagCount") Integer tagCount);
    /**
     * 점주별 매장 수 조회
     */
    Long countByOwnerId(Long ownerId);

    /**
     * 활성 상태 매장 조회
     */
    List<StoreEntity> findByStatusAndRatingGreaterThanEqual(String status, Double minRating);

    /**
     * 카테고리와 상태로 매장 조회 (평점 내림차순)
     */
    List<StoreEntity> findByCategoryAndStatusOrderByRatingDesc(String category, String status);

    /**
     * 위치 기반 매장 검색 (네이티브 쿼리 - PostGIS 사용 시)
     */
    @Query(value = "SELECT * FROM stores s WHERE " +
            "ST_DWithin(ST_Point(s.longitude, s.latitude)::geography, " +
            "ST_Point(:longitude, :latitude)::geography, :radiusMeters) " +
            "AND s.status = 'ACTIVE'", nativeQuery = true)
    List<StoreEntity> findStoresWithinRadius(@Param("latitude") Double latitude,
                                             @Param("longitude") Double longitude,
                                             @Param("radiusMeters") Double radiusMeters);

    /**
     * 키워드로 매장 검색 (매장명, 주소, 설명 포함)
     */
    @Query("SELECT s FROM StoreEntity s WHERE " +
            "s.storeName LIKE %:keyword% OR " +
            "s.address LIKE %:keyword% OR " +
            "s.description LIKE %:keyword%")
    List<StoreEntity> findByKeyword(@Param("keyword") String keyword);

    /**
     * 평점 범위로 매장 조회
     */
    List<StoreEntity> findByRatingBetweenAndStatus(Double minRating, Double maxRating, String status);

    /**
     * 리뷰 수 기준으로 인기 매장 조회
     */
    @Query("SELECT s FROM StoreEntity s WHERE s.status = 'ACTIVE' ORDER BY s.reviewCount DESC")
    Page<StoreEntity> findPopularStores(Pageable pageable);

    /**
     * 최근 생성된 매장 조회
     */
    @Query("SELECT s FROM StoreEntity s WHERE s.status = 'ACTIVE' ORDER BY s.createdAt DESC")
    Page<StoreEntity> findRecentStores(Pageable pageable);

    /**
     * 특정 지역(주소 포함) 매장 조회
     */
    List<StoreEntity> findByAddressContainingAndStatus(String addressKeyword, String status);

    /**
     * 점주별 활성 매장 조회
     */
    List<StoreEntity> findByOwnerIdAndStatus(Long ownerId, String status);

    /**
     * 매장 존재 여부 확인 (점주 ID와 매장명으로)
     */
    boolean existsByOwnerIdAndStoreName(Long ownerId, String storeName);

    /**
     * 복합 검색 쿼리
     */
    @Query("SELECT s FROM StoreEntity s WHERE " +
            "(:category IS NULL OR s.category = :category) AND " +
            "(:status IS NULL OR s.status = :status) AND " +
            "(:minRating IS NULL OR s.rating >= :minRating) AND " +
            "(:keyword IS NULL OR s.storeName LIKE %:keyword% OR s.address LIKE %:keyword%)")
    Page<StoreEntity> findStoresWithFilters(@Param("category") String category,
                                            @Param("status") String status,
                                            @Param("minRating") Double minRating,
                                            @Param("keyword") String keyword,
                                            Pageable pageable);

    StoreEntity findById(Long id);
}