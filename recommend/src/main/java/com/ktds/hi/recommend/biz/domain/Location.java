package com.ktds.hi.recommend.biz.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 위치 도메인 클래스
 * 위치 정보를 담는 도메인 객체
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Location {
    
    private Long id;
    private String address;
    private Double latitude;
    private Double longitude;
    private String city;
    private String district;
    private String country;
    
    /**
     * 좌표 업데이트
     */
    public Location updateCoordinates(Double newLatitude, Double newLongitude) {
        return Location.builder()
                .id(this.id)
                .address(this.address)
                .latitude(newLatitude)
                .longitude(newLongitude)
                .city(this.city)
                .district(this.district)
                .country(this.country)
                .build();
    }
}
