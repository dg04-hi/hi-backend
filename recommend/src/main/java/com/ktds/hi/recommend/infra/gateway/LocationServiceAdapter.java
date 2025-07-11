package com.ktds.hi.recommend.infra.gateway;

import com.ktds.hi.recommend.biz.usecase.out.LocationRepository;
import com.ktds.hi.recommend.biz.domain.Location;
import com.ktds.hi.recommend.biz.domain.RecommendStore;
import com.ktds.hi.recommend.biz.domain.RecommendType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class LocationServiceAdapter implements LocationRepository {

    @Override
    public Location saveLocation(Location location) {
        log.info("위치 정보 저장: {}", location.getAddress());

        return Location.builder()
            .id(1L)
            .address(location.getAddress())
            .latitude(location.getLatitude())
            .longitude(location.getLongitude())
            .city("서울시")
            .district("강남구")
            .country("대한민국")
            .build();
    }

    @Override
    public List<RecommendStore> findStoresWithinDistance(Double latitude, Double longitude, Integer radius) {
        log.info("반경 내 매장 조회: lat={}, lon={}, radius={}", latitude, longitude, radius);

        return Arrays.asList(
            RecommendStore.builder()
                .storeId(5L)
                .storeName("근처 매장 1")
                .address("서울시 강남구 역삼동 123-45")
                .category("한식")
                .tags(Arrays.asList("근처", "맛집"))
                .rating(4.1)
                .reviewCount(95)
                .distance(300.0)
                .recommendScore(78.0)
                .recommendType(RecommendType.LOCATION_BASED)
                .recommendReason("현재 위치에서 300m 거리")
                .build(),

            RecommendStore.builder()
                .storeId(6L)
                .storeName("근처 매장 2")
                .address("서울시 강남구 역삼동 678-90")
                .category("카페")
                .tags(Arrays.asList("커피", "디저트"))
                .rating(4.0)
                .reviewCount(67)
                .distance(450.0)
                .recommendScore(75.0)
                .recommendType(RecommendType.LOCATION_BASED)
                .recommendReason("현재 위치에서 450m 거리")
                .build()
        );
    }

    @Override
    public Double calculateDistance(Double lat1, Double lon1, Double lat2, Double lon2) {
        // 하버사인 공식을 이용한 거리 계산
        final int R = 6371; // 지구 반지름 (km)

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
            + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
            * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return R * c * 1000; // 미터 단위로 반환
    }

    @Override
    public Location geocodeAddress(String address) {
        log.info("주소 좌표 변환: {}", address);

        // Mock 구현 - 실제로는 지도 API 호출
        return Location.builder()
            .address(address)
            .latitude(37.5665)
            .longitude(126.9780)
            .city("서울시")
            .district("중구")
            .country("대한민국")
            .build();
    }
}