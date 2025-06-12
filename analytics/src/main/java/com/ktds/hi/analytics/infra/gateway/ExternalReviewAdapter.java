package com.ktds.hi.analytics.infra.gateway;

import com.ktds.hi.analytics.biz.usecase.out.ExternalReviewPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

/**
 * 외부 리뷰 서비스 어댑터 클래스
 * 리뷰 서비스와의 API 통신을 담당
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ExternalReviewAdapter implements ExternalReviewPort {
    
    private final RestTemplate restTemplate;
    
    @Value("${external.services.review}")
    private String reviewServiceUrl;
    
    @Override
    public List<String> getReviewData(Long storeId) {
        log.info("리뷰 데이터 조회: storeId={}", storeId);
        
        try {
            String url = reviewServiceUrl + "/api/reviews/stores/" + storeId + "/content";
            String[] reviewArray = restTemplate.getForObject(url, String[].class);
            
            List<String> reviews = reviewArray != null ? Arrays.asList(reviewArray) : List.of();
            log.info("리뷰 데이터 조회 완료: storeId={}, count={}", storeId, reviews.size());
            
            return reviews;
            
        } catch (Exception e) {
            log.error("리뷰 데이터 조회 실패: storeId={}", storeId, e);
            // 실패 시 더미 데이터 반환
            return getDummyReviewData(storeId);
        }
    }
    
    @Override
    public List<String> getRecentReviews(Long storeId, Integer days) {
        log.info("최근 리뷰 데이터 조회: storeId={}, days={}", storeId, days);
        
        try {
            String url = reviewServiceUrl + "/api/reviews/stores/" + storeId + "/recent?days=" + days;
            String[] reviewArray = restTemplate.getForObject(url, String[].class);
            
            List<String> reviews = reviewArray != null ? Arrays.asList(reviewArray) : List.of();
            log.info("최근 리뷰 데이터 조회 완료: storeId={}, count={}", storeId, reviews.size());
            
            return reviews;
            
        } catch (Exception e) {
            log.error("최근 리뷰 데이터 조회 실패: storeId={}", storeId, e);
            return getDummyRecentReviews(storeId);
        }
    }
    
    @Override
    public Integer getReviewCount(Long storeId) {
        log.info("리뷰 개수 조회: storeId={}", storeId);
        
        try {
            String url = reviewServiceUrl + "/api/reviews/stores/" + storeId + "/count";
            Integer count = restTemplate.getForObject(url, Integer.class);
            
            log.info("리뷰 개수 조회 완료: storeId={}, count={}", storeId, count);
            return count != null ? count : 0;
            
        } catch (Exception e) {
            log.error("리뷰 개수 조회 실패: storeId={}", storeId, e);
            return 25; // 더미 값
        }
    }
    
    @Override
    public Double getAverageRating(Long storeId) {
        log.info("평균 평점 조회: storeId={}", storeId);
        
        try {
            String url = reviewServiceUrl + "/api/reviews/stores/" + storeId + "/average-rating";
            Double rating = restTemplate.getForObject(url, Double.class);
            
            log.info("평균 평점 조회 완료: storeId={}, rating={}", storeId, rating);
            return rating != null ? rating : 0.0;
            
        } catch (Exception e) {
            log.error("평균 평점 조회 실패: storeId={}", storeId, e);
            return 4.2; // 더미 값
        }
    }
    
    /**
     * 더미 리뷰 데이터 생성
     */
    private List<String> getDummyReviewData(Long storeId) {
        return Arrays.asList(
                "음식이 정말 맛있어요! 배달도 빨랐습니다.",
                "가격 대비 양이 많고 맛도 좋네요. 추천합니다.",
                "배달 시간이 너무 오래 걸렸어요. 음식은 괜찮았습니다.",
                "포장 상태가 별로였어요. 국물이 새어나왔습니다.",
                "직원분들이 친절하고 음식도 맛있어요. 재주문 할게요!",
                "메뉴가 다양하고 맛있습니다. 자주 이용할 것 같아요.",
                "가격이 조금 비싸긴 하지만 맛은 좋아요.",
                "배달 기사님이 친절하셨어요. 음식도 따뜻했습니다."
        );
    }
    
    /**
     * 더미 최근 리뷰 데이터 생성
     */
    private List<String> getDummyRecentReviews(Long storeId) {
        return Arrays.asList(
                "어제 주문했는데 정말 맛있었어요!",
                "배달이 빨라서 좋았습니다.",
                "음식 온도가 적절했어요.",
                "포장이 깔끔하게 되어있었습니다.",
                "다음에도 주문할게요!"
        );
    }
}
