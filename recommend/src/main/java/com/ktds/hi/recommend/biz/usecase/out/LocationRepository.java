package com.ktds.hi.recommend.biz.usecase.out;

import com.ktds.hi.recommend.biz.domain.Location;
import com.ktds.hi.recommend.biz.domain.RecommendStore;

import java.util.List;

public interface LocationRepository {

    Location saveLocation(Location location);
    Double calculateDistance(Double lat1, Double lon1, Double lat2, Double lon2);
    Location geocodeAddress(String address);

    // 수정된 메서드명 (Controller에서 호출하는 것과 일치)
    List<RecommendStore> findStoresWithinDistance(Double latitude, Double longitude, Integer radius);
}