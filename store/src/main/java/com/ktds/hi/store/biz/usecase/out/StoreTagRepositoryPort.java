package com.ktds.hi.store.biz.usecase.out;

import java.util.List;

/**
 * 매장 태그 리포지토리 포트 인터페이스
 */
public interface StoreTagRepositoryPort {

    /**
     * 매장 ID로 태그 목록 조회
     */
    List<String> findTagsByStoreId(Long storeId);

    /**
     * 매장 태그 저장
     */
    void saveStoreTags(Long storeId, List<String> tags);

    /**
     * 매장 태그 삭제
     */
    void deleteTagsByStoreId(Long storeId);
}