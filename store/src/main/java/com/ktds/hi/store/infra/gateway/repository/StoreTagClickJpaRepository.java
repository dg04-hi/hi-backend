package com.ktds.hi.store.infra.gateway.repository;


import com.ktds.hi.store.infra.gateway.entity.StoreTagClickEntity;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StoreTagClickJpaRepository extends JpaRepository<StoreTagClickEntity, Long> {

    /**
     * 특정 매장의 가장 많이 클릭된 태그 5개 조회
     */
    @Query("SELECT stc FROM StoreTagClickEntity stc WHERE stc.storeId = :storeId " +
            "ORDER BY stc.clickCount DESC")
    List<StoreTagClickEntity> findTop5TagsByStoreIdOrderByClickCountDesc(
            @Param("storeId") Long storeId, PageRequest pageRequest);

    /**
     * 전체 매장에서 가장 많이 클릭된 태그 5개 조회
     */
    @Query("SELECT stc.tagId, SUM(stc.clickCount) as totalClicks " +
            "FROM StoreTagClickEntity stc " +
            "GROUP BY stc.tagId " +
            "ORDER BY totalClicks DESC")
    List<Object[]> findTop5TagsGloballyOrderByClickCount(PageRequest pageRequest);

    /**
     * 매장별 태그별 클릭 통계 조회
     */
    Optional<StoreTagClickEntity> findByStoreIdAndTagId(Long storeId, Long tagId);

    /**
     * 특정 매장의 모든 태그 클릭 통계 조회
     */
    List<StoreTagClickEntity> findByStoreIdOrderByClickCountDesc(Long storeId);
}