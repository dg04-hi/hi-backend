package com.ktds.hi.analytics.infra.gateway;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.ktds.hi.analytics.biz.usecase.out.ExternalReviewPort;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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
            // ReviewListResponse 배열로 직접 받기 (Review 서비스가 List<ReviewListResponse> 반환)
            ReviewListResponse[] reviewArray = restTemplate.getForObject(url, ReviewListResponse[].class);

            if (reviewArray == null || reviewArray.length == 0) {
                log.info("매장에 리뷰가 없습니다: storeId={}", storeId);
                return List.of();
            }

            // ReviewListResponse에서 content만 추출
            List<String> reviews = Arrays.stream(reviewArray)
                .map(ReviewListResponse::getContent)
                .filter(content -> content != null && !content.trim().isEmpty())
                .collect(Collectors.toList());
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

            //최근 데이터를 가져오도록 변경
            // String url = reviewServiceUrl + "/api/reviews/stores/recent/" + storeId + "?size=100&days=" + days;
            //
            // // ReviewListResponse 배열로 직접 받기
            // ReviewListResponse[] reviewArray = restTemplate.getForObject(url, ReviewListResponse[].class);

            int totalSize = 200;
            int threadCount = 4;
            int pageSize = totalSize / threadCount; // 50개씩

            // ExecutorService 생성
            ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
            List<CompletableFuture<ReviewListResponse[]>> futures = new ArrayList<>();

            // 4개의 비동기 요청 생성 (limit 50, offset 0/50/100/150)
            for (int i = 0; i < threadCount; i++) {
                final int offset = i * pageSize;
                final int limit = pageSize;

                CompletableFuture<ReviewListResponse[]> future = CompletableFuture.supplyAsync(() -> {
                    String url = reviewServiceUrl + "/api/reviews/stores/recent/" + storeId
                        + "?size=" + limit + "&offset=" + offset + "&days=" + days;

                    log.debug("스레드 {}에서 URL 호출: {}", Thread.currentThread().getName(), url);

                    // 기존과 동일한 방식으로 API 호출
                    ReviewListResponse[] reviewArray = restTemplate.getForObject(url, ReviewListResponse[].class);

                    if (reviewArray == null) {
                        log.debug("스레드 {}에서 빈 응답 수신", Thread.currentThread().getName());
                        return new ReviewListResponse[0];
                    }

                    log.debug("스레드 {}에서 {} 개 리뷰 수신", Thread.currentThread().getName(), reviewArray.length);
                    return reviewArray;

                }, executorService);

                futures.add(future);
            }

            // 모든 요청 완료 대기 및 결과 합치기
            List<ReviewListResponse> allReviewResponses = new ArrayList<>();
            for (CompletableFuture<ReviewListResponse[]> future : futures) {
                ReviewListResponse[] reviewArray = future.get(30, TimeUnit.SECONDS); // 30초 타임아웃
                allReviewResponses.addAll(Arrays.asList(reviewArray));
            }

            executorService.shutdown();


            // 최근 N일 이내의 리뷰만 필터링
            LocalDateTime cutoffDate = LocalDateTime.now().minusDays(days);

            List<String> recentReviews = allReviewResponses.stream()
                .filter(review -> review.getCreatedAt() != null && review.getCreatedAt().isAfter(cutoffDate))
                .map(ReviewListResponse::getContent)
                .filter(content -> content != null && !content.trim().isEmpty())
                .map(content -> content.replace("`", "")
                    .replace("\n", "")
                    .replace("\\", "")
                    .replace("\"", ""))
                .collect(Collectors.toList());



            log.info("최근 리뷰 데이터 조회 완료: storeId={}, count={}", storeId, recentReviews.size());
            
            return recentReviews;
            
        } catch (Exception e) {
            log.error("최근 리뷰 데이터 조회 실패: storeId={}", storeId, e);
            return getDummyRecentReviews(storeId);
        }
    }

    @Override
    public List<String> getPositiveReviews(Long storeId, Integer days) {
        return List.of();
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


    @Data
    public static class ReviewListResponse {

        @JsonProperty("reviewId")
        private Long reviewId;

        @JsonProperty("memberNickname")
        private String memberNickname;

        @JsonProperty("rating")
        private Integer rating;

        @JsonProperty("content")
        private String content;

        @JsonProperty("imageUrls")
        private List<String> imageUrls;

        @JsonProperty("likeCount")
        private Integer likeCount;

        @JsonProperty("dislikeCount")
        private Integer dislikeCount;

        @JsonProperty("createdAt")
        @JsonDeserialize(using = FlexibleLocalDateTimeDeserializer.class)
        private LocalDateTime createdAt;
    }


    /**
     * 다양한 LocalDateTime 형식을 처리하는 커스텀 Deserializer
     */
    public static class FlexibleLocalDateTimeDeserializer extends JsonDeserializer<LocalDateTime> {

        private static final DateTimeFormatter[] FORMATTERS = {
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS"),     // 마이크로초 6자리
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSS"),      // 마이크로초 5자리
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSS"),       // 마이크로초 4자리
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS"),        // 밀리초 3자리
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SS"),         // 2자리
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.S"),          // 1자리
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"),            // 초까지만
            DateTimeFormatter.ISO_LOCAL_DATE_TIME                            // ISO 표준
        };

        @Override
        public LocalDateTime deserialize(JsonParser parser, DeserializationContext context) throws IOException {
            String dateString = parser.getText();

            if (dateString == null || dateString.trim().isEmpty()) {
                return null;
            }

            // 여러 형식으로 시도
            for (DateTimeFormatter formatter : FORMATTERS) {
                try {
                    return LocalDateTime.parse(dateString, formatter);
                } catch (DateTimeParseException e) {
                    // 다음 형식으로 시도
                }
            }

            // 모든 형식이 실패하면 현재 시간 반환 (에러 로그)
            System.err.println("Failed to parse LocalDateTime: " + dateString + ", using current time");
            return LocalDateTime.now();
        }
    }
}
