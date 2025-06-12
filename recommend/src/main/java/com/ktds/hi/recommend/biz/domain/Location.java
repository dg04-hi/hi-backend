package com.ktds.hi.recommend.biz.domain;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Location {
    private Long id;           // 추가
    private Double latitude;
    private Double longitude;
    private String address;
    private String city;       // 추가
    private String district;   // 추가
    private String country;    // 추가
}