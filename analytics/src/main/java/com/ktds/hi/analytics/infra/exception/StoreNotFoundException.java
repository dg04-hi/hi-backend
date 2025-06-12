package com.ktds.hi.analytics.infra.exception;

/**
 * 매장 정보를 찾을 수 없을 때 발생하는 예외
 */
public class StoreNotFoundException extends AnalyticsException {
    
    public StoreNotFoundException(Long storeId) {
        super("STORE_NOT_FOUND", "매장을 찾을 수 없습니다: " + storeId);
    }
}
