package com.ktds.hi.analytics.infra.gateway;

import com.azure.ai.textanalytics.TextAnalyticsClient;
import com.azure.ai.textanalytics.TextAnalyticsClientBuilder;
import com.azure.ai.textanalytics.models.AnalyzeSentimentResult;
import com.azure.ai.textanalytics.models.DocumentSentiment;
import com.azure.core.credential.AzureKeyCredential;
import com.ktds.hi.analytics.biz.domain.AiFeedback;
import com.ktds.hi.analytics.biz.domain.SentimentType;
import com.ktds.hi.analytics.biz.usecase.out.AIServicePort;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * AI 서비스 어댑터 클래스
 * OpenAI, Azure Cognitive Services 등 외부 AI API 연동
 */
@Slf4j
@Component
public class AIServiceAdapter implements AIServicePort {
    
    @Value("${ai.azure.cognitive.endpoint}")
    private String cognitiveEndpoint;
    
    @Value("${ai.azure.cognitive.key}")
    private String cognitiveKey;
    
    @Value("${ai.openai.api-key}")
    private String openaiApiKey;
    
    private TextAnalyticsClient textAnalyticsClient;
    
    @PostConstruct
    public void initializeClients() {
        // Azure Cognitive Services 클라이언트 초기화
        textAnalyticsClient = new TextAnalyticsClientBuilder()
                .credential(new AzureKeyCredential(cognitiveKey))
                .endpoint(cognitiveEndpoint)
                .buildClient();
        
        log.info("AI 서비스 클라이언트 초기화 완료");
    }
    
    @Override
    public AiFeedback generateFeedback(List<String> reviewData) {
        log.info("AI 피드백 생성 시작: 리뷰 수={}", reviewData.size());
        
        try {
            if (reviewData.isEmpty()) {
                return createEmptyFeedback();
            }
            
            // 1. 감정 분석 수행
            List<SentimentType> sentiments = reviewData.stream()
                    .map(this::analyzeSentiment)
                    .toList();
            
            // 2. 긍정/부정 비율 계산
            long positiveCount = sentiments.stream()
                    .mapToLong(s -> s == SentimentType.POSITIVE ? 1 : 0)
                    .sum();
            
            long negativeCount = sentiments.stream()
                    .mapToLong(s -> s == SentimentType.NEGATIVE ? 1 : 0)
                    .sum();
            
            double positiveRate = (double) positiveCount / reviewData.size() * 100;
            double negativeRate = (double) negativeCount / reviewData.size() * 100;
            
            // 3. 피드백 생성
            AiFeedback feedback = AiFeedback.builder()
                    .summary(generateSummary(positiveRate, negativeRate, reviewData.size()))
                    .positivePoints(generatePositivePoints(reviewData, sentiments))
                    .improvementPoints(generateImprovementPoints(reviewData, sentiments))
                    .recommendations(generateRecommendations(positiveRate, negativeRate))
                    .sentimentAnalysis(String.format("긍정: %.1f%%, 부정: %.1f%%", positiveRate, negativeRate))
                    .confidenceScore(calculateConfidenceScore(reviewData.size()))
                    .generatedAt(LocalDateTime.now())
                    .build();
            
            log.info("AI 피드백 생성 완료: 긍정률={}%, 부정률={}%", positiveRate, negativeRate);
            return feedback;
            
        } catch (Exception e) {
            log.error("AI 피드백 생성 중 오류 발생", e);
            throw new RuntimeException("AI 피드백 생성에 실패했습니다.", e);
        }
    }
    
    @Override
    public SentimentType analyzeSentiment(String content) {
        try {
            AnalyzeSentimentResult result = textAnalyticsClient.analyzeSentiment(content);
            DocumentSentiment sentiment = result.getDocumentSentiment();
            
            switch (sentiment) {
                case POSITIVE:
                    return SentimentType.POSITIVE;
                case NEGATIVE:
                    return SentimentType.NEGATIVE;
                default:
                    return SentimentType.NEUTRAL;
            }
            
        } catch (Exception e) {
            log.warn("감정 분석 실패, 중립으로 처리: content={}", content.substring(0, Math.min(50, content.length())));
            return SentimentType.NEUTRAL;
        }
    }
    
    @Override
    public List<String> generateActionPlan(AiFeedback feedback) {
        log.info("실행 계획 생성 시작");
        
        try {
            // 개선점을 기반으로 실행 계획 생성
            List<String> actionPlans = feedback.getImprovementPoints().stream()
                    .map(this::convertToActionPlan)
                    .toList();
            
            log.info("실행 계획 생성 완료: 계획 수={}", actionPlans.size());
            return actionPlans;
            
        } catch (Exception e) {
            log.error("실행 계획 생성 중 오류 발생", e);
            return Arrays.asList("서비스 품질 개선을 위한 직원 교육 실시", "고객 피드백 수집 체계 구축");
        }
    }
    
    /**
     * 빈 피드백 생성
     */
    private AiFeedback createEmptyFeedback() {
        return AiFeedback.builder()
                .summary("분석할 리뷰 데이터가 없습니다.")
                .positivePoints(Arrays.asList("리뷰 데이터 부족으로 분석 불가"))
                .improvementPoints(Arrays.asList("더 많은 고객 리뷰 수집 필요"))
                .recommendations(Arrays.asList("고객들에게 리뷰 작성을 유도하는 이벤트 진행"))
                .sentimentAnalysis("데이터 부족")
                .confidenceScore(0.0)
                .generatedAt(LocalDateTime.now())
                .build();
    }
    
    /**
     * 요약 생성
     */
    private String generateSummary(double positiveRate, double negativeRate, int totalReviews) {
        if (positiveRate > 70) {
            return String.format("총 %d개의 리뷰 중 %.1f%%가 긍정적입니다. 고객 만족도가 높은 수준입니다.", 
                    totalReviews, positiveRate);
        } else if (negativeRate > 30) {
            return String.format("총 %d개의 리뷰 중 %.1f%%가 부정적입니다. 서비스 개선이 필요합니다.", 
                    totalReviews, negativeRate);
        } else {
            return String.format("총 %d개의 리뷰로 분석한 결과, 전반적으로 평균적인 고객 만족도를 보입니다.", 
                    totalReviews);
        }
    }
    
    /**
     * 긍정적 요소 생성
     */
    private List<String> generatePositivePoints(List<String> reviewData, List<SentimentType> sentiments) {
        // 실제로는 자연어 처리를 통해 긍정적 키워드 추출
        return Arrays.asList(
                "음식 맛에 대한 긍정적 평가가 많습니다",
                "직원 서비스에 대한 만족도가 높습니다",
                "가격 대비 만족도가 좋습니다"
        );
    }
    
    /**
     * 개선점 생성
     */
    private List<String> generateImprovementPoints(List<String> reviewData, List<SentimentType> sentiments) {
        // 실제로는 자연어 처리를 통해 부정적 키워드 추출
        return Arrays.asList(
                "배달 시간 단축이 필요합니다",
                "음식 포장 상태 개선이 필요합니다",
                "메뉴 다양성 확대를 고려해보세요"
        );
    }
    
    /**
     * 추천사항 생성
     */
    private List<String> generateRecommendations(double positiveRate, double negativeRate) {
        List<String> recommendations = Arrays.asList(
                "고객 피드백을 정기적으로 모니터링하고 대응하세요",
                "리뷰에 적극적으로 댓글을 달아 고객과 소통하세요",
                "메뉴 품질 관리 체계를 강화하세요"
        );
        
        if (negativeRate > 30) {
            recommendations.add("긴급히 서비스 품질 개선 계획을 수립하세요");
        }
        
        return recommendations;
    }
    
    /**
     * 신뢰도 점수 계산
     */
    private double calculateConfidenceScore(int reviewCount) {
        if (reviewCount >= 100) return 0.95;
        if (reviewCount >= 50) return 0.85;
        if (reviewCount >= 20) return 0.75;
        if (reviewCount >= 10) return 0.65;
        return 0.5;
    }
    
    /**
     * 개선점을 실행 계획으로 변환
     */
    private String convertToActionPlan(String improvementPoint) {
        // 개선점을 구체적인 실행 계획으로 변환하는 로직
        if (improvementPoint.contains("배달 시간")) {
            return "배달 시간 단축을 위한 배달 경로 최적화 및 인력 증원 검토";
        } else if (improvementPoint.contains("포장")) {
            return "음식 포장 재료 교체 및 포장 방법 개선";
        } else if (improvementPoint.contains("메뉴")) {
            return "고객 선호도 조사를 통한 신메뉴 개발 계획 수립";
        }
        return improvementPoint + "을 위한 구체적인 실행 방안 수립";
    }
}
