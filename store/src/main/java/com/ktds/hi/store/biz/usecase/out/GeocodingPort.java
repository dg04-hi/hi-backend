package com.ktds.hi.store.biz.usecase.out;

/**
 * 지오코딩 포트 인터페이스
 */
public interface GeocodingPort {

    /**
     * 주소를 좌표로 변환
     */
    Coordinates getCoordinates(String address);

    /**
     * 두 좌표 간 거리 계산 (km)
     */
    Double calculateDistance(Coordinates coord1, Coordinates coord2);
}