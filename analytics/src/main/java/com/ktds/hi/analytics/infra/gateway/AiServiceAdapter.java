package com.ktds.hi.analytics.infra.gateway;

import com.ktds.hi.analytics.biz.domain.AiFeedback;
import com.ktds.hi.analytics.biz.domain.SentimentType;
import com.ktds.hi.analytics.biz.usecase.out.AIServicePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * AI 서비스 어댑터 클래스
 * AI Service Port를 구현하여 외부 AI API 연동 기능을 제공
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class AiServiceAdapter implements AIServicePort {
    
    private final RestTemplate restTemplate;
    
    @Value("${external-api.openai.api-key:}")
    private String openaiApiKey;
    
    @Value("${external-api.claude.api-key:}")
    private String claudeApiKey;
    
    @Override
    public AiFeedback generateFeedback(List<String> reviewData) {
        log.info("AI 피드백 생성 시작: reviewCount={}", reviewData.size());
        
        try {
            // OpenAI API를 사용한 감정 분석
            String combinedReviews = String.join(" ", reviewData);
            SentimentType sentiment = analyzeSentiment(combinedReviews);
            
            // Mock AI 피드백 생성 (실제로는 OpenAI/Claude API 호출)
            AiFeedback feedback = AiFeedback.builder()
                    .summary(generateMockSummary(reviewData, sentiment))
                    .sentiment(sentiment)
                    .positivePoints(generateMockPositivePoints())
                    .negativePoints(generateMockNegativePoints())
                    .recommendations(generateMockRecommendations())
                    .confidence(calculateConfidence(reviewData))
                    .analysisDate(LocalDate.now())
                    .build();
            
            log.info("AI 피드백 생성 완료: sentiment={}, confidence={}", sentiment, feedback.getConfidence());
            return feedback;
            
        } catch (Exception e) {
            log.error("AI 피드백 생성 실패: error={}", e.getMessage(), e);
            return createFallbackFeedback();
        }
    }
    
    @Override
    public SentimentType analyzeSentiment(String content) {
        log.debug("감정 분석 시작: contentLength={}", content.length());
        
        try {
            // 실제로는 OpenAI API 호출
            if (openaiApiKey != null && !openaiApiKey.isEmpty()) {
                return callOpenAISentimentAPI(content);
            }
            
            // Fallback: 간단한 키워드 기반 감정 분석
            return performKeywordBasedSentiment(content);
            
        } catch (Exception e) {
            log.error("감정 분석 실패: error={}", e.getMessage(), e);
            return SentimentType.NEUTRAL;
        }
    }
    
    @Override
    public List<String> generateActionPlan(AiFeedback feedback) {
        log.info("실행 계획 생성 시작: sentiment={}", feedback.getSentiment());
        
        try {
            // AI 기반 실행 계획 생성 (Mock)
            List<String> actionPlan = List.of(
                    "고객 서비스 개선을 위한 직원 교육 실시",
                    "주방 청결도 점검 및 개선",
                    "대기시간 단축을 위한 주문 시스템 개선",
                    "메뉴 다양성 확대 검토",
                    "고객 피드백 수집 시스템 구축"
            );
            
            log.info("실행 계획 생성 완료: planCount={}", actionPlan.size());
            return actionPlan;
            
        } catch (Exception e) {
            log.error("실행 계획 생성 실패: error={}", e.getMessage(), e);
            return List.of("AI 분석을 통한 개선사항 검토");
        }
    }
    
    /**
     * OpenAI API를 통한 감정 분석
     */
    private SentimentType callOpenAISentimentAPI(String content) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + openaiApiKey);
            headers.set("Content-Type", "application/json");
            
            Map<String, Object> requestBody = Map.of(
                    "model", "gpt-3.5-turbo",
                    "messages", List.of(
                            Map.of("role", "user", "content", 
                                    "다음 리뷰의 감정을 분석해주세요. POSITIVE, NEGATIVE, NEUTRAL 중 하나로 답해주세요: " + content)
                    ),
                    "max_tokens", 10
            );
            
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            ResponseEntity<Map> response = restTemplate.exchange(
                    "https://api.openai.com/v1/chat/completions",
                    HttpMethod.POST,
                    entity,
                    Map.class
            );
            
            // API 응답 파싱
            Map<String, Object> responseBody = response.getBody();
            if (responseBody != null && responseBody.containsKey("choices")) {
                List<Map<String, Object>> choices = (List<Map<String, Object>>) responseBody.get("choices");
                if (!choices.isEmpty()) {
                    Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
                    String result = (String) message.get("content");
                    return SentimentType.valueOf(result.trim().toUpperCase());
                }
            }
            
            return SentimentType.NEUTRAL;
            
        } catch (Exception e) {
            log.warn("OpenAI API 호출 실패, 키워드 분석으로 대체: error={}", e.getMessage());
            return performKeywordBasedSentiment(content);
        }
    }
    
    /**
     * 키워드 기반 감정 분석
     */
    private SentimentType performKeywordBasedSentiment(String content) {
        String lowerContent = content.toLowerCase();
        
        List<String> positiveKeywords = List.of("맛있", "좋", "최고", "추천", "만족", "친절", "깔끔");
        List<String> negativeKeywords = List.of("맛없", "나쁘", "별로", "실망", "불친절", "더러", "느리");
        
        long positiveCount = positiveKeywords.stream()
                .mapToLong(keyword -> countOccurrences(lowerContent, keyword))
                .sum();
        
        long negativeCount = negativeKeywords.stream()
                .mapToLong(keyword -> countOccurrences(lowerContent, keyword))
                .sum();
        
        if (positiveCount > negativeCount) {
            return SentimentType.POSITIVE;
        } else if (negativeCount > positiveCount) {
            return SentimentType.NEGATIVE;
        } else {
            return SentimentType.NEUTRAL;
        }
    }
    
    /**
     * 문자열에서 특정 키워드 출현 횟수 계산
     */
    private long countOccurrences(String text, String keyword) {
        return (text.length() - text.replace(keyword, "").length()) / keyword.length();
    }
    
    /**
     * Mock 요약 생성
     */
    private String generateMockSummary(List<String> reviewData, SentimentType sentiment) {
        switch (sentiment) {
            case POSITIVE:
                return "고객들이 음식의 맛과 서비스에 대해 전반적으로 만족하고 있습니다. 특히 음식의 품질과 직원의 친절함이 높이 평가받고 있습니다.";
            case NEGATIVE:
                return "일부 고객들이 음식의 맛이나 서비스에 대해 불만을 표현하고 있습니다. 주로 대기시간과 음식의 온도에 대한 개선이 필요해 보입니다.";
            default:
                return "고객 리뷰가 긍정적인 면과 개선이 필요한 면이 혼재되어 있습니다. 지속적인 품질 관리가 필요합니다.";
        }
    }
    
    /**
     * Mock 긍정 포인트 생성
     */
    private List<String> generateMockPositivePoints() {
        return List.of(
                "음식의 맛이 좋다는 평가",
                "직원들이 친절하다는 의견",
                "매장이 깔끔하고 청결함",
                "가격 대비 만족스러운 품질"
        );
    }
    
    /**
     * Mock 부정 포인트 생성
     */
    private List<String> generateMockNegativePoints() {
        return List.of(
                "주문 후 대기시간이 다소 길음",
                "일부 메뉴의 간이 짜다는 의견",
                "주차 공간이 부족함"
        );
    }
    
    /**
     * Mock 추천사항 생성
     */
    private List<String> generateMockRecommendations() {
        return List.of(
                "주문 처리 시간 단축을 위한 시스템 개선",
                "메뉴별 간 조절에 대한 재검토",
                "고객 대기 공간 개선",
                "직원 서비스 교육 지속 실시",
                "주차 환경 개선 방안 검토"
        );
    }
    
    /**
     * 신뢰도 계산
     */
    private Double calculateConfidence(List<String> reviewData) {
        // 리뷰 수와 내용 길이를 기반으로 신뢰도 계산
        int reviewCount = reviewData.size();
        double avgLength = reviewData.stream()
                .mapToInt(String::length)
                .average()
                .orElse(0.0);
        
        // 기본 신뢰도 계산 로직
        double confidence = Math.min(0.95, 0.5 + (reviewCount * 0.05) + (avgLength * 0.001));
        return Math.round(confidence * 100.0) / 100.0;
    }
    
    /**
     * Fallback 피드백 생성
     */
    private AiFeedback createFallbackFeedback() {
        return AiFeedback.builder()
                .summary("AI 분석을 수행할 수 없어 기본 분석 결과를 제공합니다.")
                .sentiment(SentimentType.NEUTRAL)
                .positivePoints(List.of("분석 데이터 부족"))
                .negativePoints(List.of("분석 데이터 부족"))
                .recommendations(List.of("더 많은 리뷰 데이터 수집 필요"))
                .confidence(0.3)
                .analysisDate(LocalDate.now())
                .build();
    }
}
