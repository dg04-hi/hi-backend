package com.ktds.hi.analytics.infra.gateway;

import com.ktds.hi.analytics.biz.usecase.out.ExternalReviewPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 외부 리뷰 어댑터 클래스
 * External Review Port를 구현하여 외부 리뷰 데이터 연동 기능을 제공
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ExternalReviewAdapter implements ExternalReviewPort {
    
    @Override
    public List<String> getReviewData(Long storeId) {
        log.info("외부 리뷰 데이터 조회 시작: storeId={}", storeId);
        
        try {
            // 실제로는 Review Service와 연동하여 리뷰 데이터를 가져옴
            // Mock 데이터 반환
            List<String> reviews = new ArrayList<>();
            reviews.add("음식이 정말 맛있고 서비스도 친절해요. 다음에 또 올게요!");
            reviews.add("가격 대비 양이 많고 맛도 괜찮습니다. 추천해요.");
            reviews.add("분위기가 좋고 음식도 맛있어요. 특히 김치찌개가 일품이네요.");
            reviews.add("조금 대기시간이 길었지만 음식은 만족스러웠습니다.");
            reviews.add("깔끔하고 맛있어요. 직원분들도 친절하시고 좋았습니다.");
            reviews.add("기대보다는 평범했지만 나쁘지 않았어요.");
            reviews.add("음식이 너무 짜서 별로였습니다. 개선이 필요할 것 같아요.");
            reviews.add("가성비 좋고 맛도 괜찮아요. 재방문 의사 있습니다.");
            reviews.add("위생 상태가 좋고 음식도 깔끔해요. 만족합니다.");
            reviews.add("주차하기 어려워서 불편했지만 음식은 맛있었어요.");
            
            log.info("외부 리뷰 데이터 조회 완료: storeId={}, count={}", storeId, reviews.size());
            return reviews;
            
        } catch (Exception e) {
            log.error("외부 리뷰 데이터 조회 실패: storeId={}, error={}", storeId, e.getMessage(), e);
            return List.of();
        }
    }
    
    @Override
    public List<String> getRecentReviews(Long storeId, Integer days) {
        log.info("최근 리뷰 데이터 조회 시작: storeId={}, days={}", storeId, days);
        
        try {
            // 실제로는 최근 N일간의 리뷰만 필터링
            List<String> allReviews = getReviewData(storeId);
            
            // Mock: 최근 리뷰는 전체 리뷰의 70% 정도로 가정
            int recentCount = (int) (allReviews.size() * 0.7);
            List<String> recentReviews = allReviews.subList(0, Math.min(recentCount, allReviews.size()));
            
            log.info("최근 리뷰 데이터 조회 완료: storeId={}, days={}, count={}", storeId, days, recentReviews.size());
            return recentReviews;
            
        } catch (Exception e) {
            log.error("최근 리뷰 데이터 조회 실패: storeId={}, days={}, error={}", storeId, days, e.getMessage(), e);
            return List.of();
        }
    }
    
    @Override
    public Integer getReviewCount(Long storeId) {
        log.info("리뷰 수 조회 시작: storeId={}", storeId);
        
        try {
            List<String> reviews = getReviewData(storeId);
            int count = reviews.size();
            
            log.info("리뷰 수 조회 완료: storeId={}, count={}", storeId, count);
            return count;
            
        } catch (Exception e) {
            log.error("리뷰 수 조회 실패: storeId={}, error={}", storeId, e.getMessage(), e);
            return 0;
        }
    }
}
