package com.ktds.hi.store.infra.gateway.repository;

import com.ktds.hi.store.infra.gateway.entity.MenuEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 메뉴 JPA 리포지토리 인터페이스
 * 메뉴 데이터의 CRUD 작업을 담당
 */
@Repository
public interface MenuJpaRepository extends JpaRepository<MenuEntity, Long> {
    
    /**
     * 매장 ID로 이용 가능한 메뉴 목록 조회
     */
    List<MenuEntity> findByStoreIdAndIsAvailableTrue(Long storeId);
    
    /**
     * 매장 ID로 모든 메뉴 목록 조회
     */
    List<MenuEntity> findByStoreId(Long storeId);
    
    /**
     * 매장 ID로 메뉴 삭제
     */
    void deleteByStoreId(Long storeId);
    
    /**
     * 카테고리별 메뉴 조회
     */
    List<MenuEntity> findByStoreIdAndCategoryAndIsAvailableTrue(Long storeId, String category);
}
