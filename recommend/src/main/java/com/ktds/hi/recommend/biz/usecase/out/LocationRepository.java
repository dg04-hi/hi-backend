package com.ktds.hi.recommend.biz.usecase.out;

import com.ktds.hi.recommend.biz.domain.Location;
import com.ktds.hi.recommend.biz.domain.RecommendStore;

import java.util.List;

/**
 * 위치 기반 서비스 리포지토리 인터페이스
 * 위치 정보 처리 기능을 정의
 */
public interface LocationRepository {
    
    /**
     * 위치 정보 저장
     */
    Location saveLocation(Location location);
    
    /**
     * 반경 내 매장 조회
     */
    List<RecommendStore> findStoresWithinRadius(Double latitude, Double longitude, Integer radius);
    
    /**
     * 거리 계산
     */
    Double calculateDistance(Double lat1, Double lon1, Double lat2, Double lon2);
    
    /**
     * 주소를 좌표로 변환
     */
    Location geocodeAddress(String address);
}
