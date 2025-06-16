package com.ktds.hi.analytics.infra.gateway;

import static com.azure.ai.textanalytics.models.TextSentiment.*;

import com.azure.ai.textanalytics.TextAnalyticsClient;
import com.azure.ai.textanalytics.TextAnalyticsClientBuilder;
import com.azure.ai.textanalytics.models.AnalyzeSentimentResult;
import com.azure.ai.textanalytics.models.DocumentSentiment;
import com.azure.ai.textanalytics.models.TextSentiment;
import com.azure.core.credential.AzureKeyCredential;
import com.ktds.hi.analytics.biz.domain.AiFeedback;
import com.ktds.hi.analytics.biz.domain.SentimentType;
import com.ktds.hi.analytics.biz.usecase.out.AIServicePort;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
            DocumentSentiment documentSentiment = textAnalyticsClient.analyzeSentiment(content);
            TextSentiment sentiment = documentSentiment.getSentiment();

            if (sentiment == TextSentiment.POSITIVE) {
                return SentimentType.POSITIVE;
            } else if (sentiment == TextSentiment.NEGATIVE) {
                return SentimentType.NEGATIVE;
            } else if (sentiment == TextSentiment.NEUTRAL) {
                return SentimentType.NEUTRAL;
            } else if (sentiment == TextSentiment.MIXED) {
                return SentimentType.NEUTRAL; // MIXED는 NEUTRAL로 처리
            } else {
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
        List<String> positivePoints = new ArrayList<>();

        long positiveCount = sentiments.stream().mapToLong(s -> s == SentimentType.POSITIVE ? 1 : 0).sum();
        double positiveRate = (double) positiveCount / reviewData.size() * 100;

        if (positiveRate > 70) {
            positivePoints.add("고객 만족도가 매우 높습니다");
            positivePoints.add("전반적으로 긍정적인 평가를 받고 있습니다");
            positivePoints.add("재방문 의향이 높은 고객들이 많습니다");
        } else if (positiveRate > 50) {
            positivePoints.add("평균 이상의 고객 만족도를 보입니다");
            positivePoints.add("많은 고객들이 만족하고 있습니다");
        } else {
            positivePoints.add("일부 고객들이 긍정적으로 평가하고 있습니다");
            positivePoints.add("개선의 여지가 있습니다");
        }

        return positivePoints;
    }
    
    /**
     * 개선점 생성
     */
    private List<String> generateImprovementPoints(List<String> reviewData, List<SentimentType> sentiments) {
        List<String> improvementPoints = new ArrayList<>();

        long negativeCount = sentiments.stream().mapToLong(s -> s == SentimentType.NEGATIVE ? 1 : 0).sum();
        double negativeRate = (double) negativeCount / reviewData.size() * 100;

        if (negativeRate > 30) {
            improvementPoints.add("고객 서비스 품질 개선이 시급합니다");
            improvementPoints.add("부정적 피드백에 대한 체계적 대응이 필요합니다");
            improvementPoints.add("근본적인 서비스 개선 방안을 마련해야 합니다");
        } else if (negativeRate > 15) {
            improvementPoints.add("일부 서비스 영역에서 개선이 필요합니다");
            improvementPoints.add("고객 만족도 향상을 위한 노력이 필요합니다");
        } else {
            improvementPoints.add("현재 서비스 수준을 유지하며 세부 개선점을 찾아보세요");
            improvementPoints.add("더 높은 고객 만족을 위한 차별화 요소를 개발하세요");
        }

        return improvementPoints;
    }
    
    /**
     * 추천사항 생성
     */
    private List<String> generateRecommendations(double positiveRate, double negativeRate) {
        List<String> recommendations = new ArrayList<>();

        if (positiveRate > 70) {
            recommendations.add("현재의 우수한 서비스를 유지하면서 브랜드 가치를 높이세요");
            recommendations.add("긍정적 리뷰를 마케팅 자료로 활용하세요");
            recommendations.add("고객 충성도 프로그램을 도입하세요");
        } else if (negativeRate > 30) {
            recommendations.add("고객 불만사항에 대한 즉각적인 대응 체계를 구축하세요");
            recommendations.add("직원 교육을 통한 서비스 품질 향상에 집중하세요");
            recommendations.add("고객 피드백 수집 및 분석 프로세스를 강화하세요");
        } else {
            recommendations.add("지속적인 품질 관리와 고객 만족도 모니터링을 실시하세요");
            recommendations.add("차별화된 서비스 제공을 통해 경쟁력을 강화하세요");
            recommendations.add("고객과의 소통을 늘려 관계를 강화하세요");
        }

        return recommendations;
    }
    
    /**
     * 신뢰도 점수 계산
     */
    private double calculateConfidenceScore(int reviewCount) {
        if (reviewCount >= 50) {
            return 0.9;
        } else if (reviewCount >= 20) {
            return 0.75;
        } else if (reviewCount >= 10) {
            return 0.6;
        } else if (reviewCount >= 5) {
            return 0.4;
        } else {
            return 0.2;
        }
    }
    
    /**
     * 개선점을 실행 계획으로 변환
     */
    private String convertToActionPlan(String improvementPoint) {
        // 개선점을 구체적인 실행계획으로 변환
        if (improvementPoint.contains("서비스 품질")) {
            return "직원 서비스 교육 프로그램 실시 (월 1회, 2시간)";
        } else if (improvementPoint.contains("대기시간")) {
            return "주문 처리 시스템 개선 및 대기열 관리 체계 도입";
        } else if (improvementPoint.contains("가격")) {
            return "경쟁사 가격 분석 및 합리적 가격 정책 수립";
        } else if (improvementPoint.contains("메뉴")) {
            return "고객 선호도 조사를 통한 메뉴 다양화 방안 검토";
        } else {
            return "고객 피드백 기반 서비스 개선 계획 수립";
        }
    }
}
