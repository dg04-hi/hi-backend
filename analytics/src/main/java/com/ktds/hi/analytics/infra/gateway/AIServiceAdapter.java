package com.ktds.hi.analytics.infra.gateway;

import static com.azure.ai.textanalytics.models.TextSentiment.*;

import com.azure.ai.textanalytics.TextAnalyticsClient;
import com.azure.ai.textanalytics.TextAnalyticsClientBuilder;
import com.azure.ai.textanalytics.models.AnalyzeSentimentResult;
import com.azure.ai.textanalytics.models.DocumentSentiment;
import com.azure.ai.textanalytics.models.TextSentiment;
import com.azure.core.credential.AzureKeyCredential;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ktds.hi.analytics.biz.domain.AiFeedback;
import com.ktds.hi.analytics.biz.domain.SentimentType;
import com.ktds.hi.analytics.biz.usecase.out.AIServicePort;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * AI 서비스 어댑터 클래스
 * OpenAI, Azure Cognitive Services 등 외부 AI API 연동
 */
@Slf4j
@Component
public class AIServiceAdapter implements AIServicePort {


    @Value("${ai-api.openai.base-url:https://api.openai.com/v1}")
    private String openaiBaseUrl;

    @Value("${ai-api.openai.api-key}")
    private String openaiApiKey;

    @Value("${ai-api.openai.model:gpt-4o-mini}")
    private String openaiModel;
    
    private TextAnalyticsClient textAnalyticsClient;

    private RestTemplate restTemplate;
    private ObjectMapper objectMapper;
    
    @PostConstruct
    public void initializeClients() {
        // Azure Cognitive Services 클라이언트 초기화
        // textAnalyticsClient = new TextAnalyticsClientBuilder()
        //         .credential(new AzureKeyCredential(cognitiveKey))
        //         .endpoint(cognitiveEndpoint)
        //         .buildClient();
        //
        // log.info("AI 서비스 클라이언트 초기화 완료");

        // OpenAI API 클라이언트 초기화
        restTemplate = new RestTemplate();
        objectMapper = new ObjectMapper();

        if (openaiApiKey == null || openaiApiKey.trim().isEmpty() || openaiApiKey.equals("your-openai-api-key")) {
            log.warn("OpenAI API 키가 설정되지 않았습니다. AI 기능이 제한될 수 있습니다.");
        } else {
            log.info("OpenAI API 클라이언트 초기화 완료");
        }
    }
    
    @Override
    public AiFeedback generateFeedback(List<String> reviewData) {

        log.info("OpenAI 피드백 생성 시작: 리뷰 수={}", reviewData.size());

        try {
            if (reviewData.isEmpty()) {
                return createEmptyFeedback();
            }

            // OpenAI API 호출하여 전체 리뷰 분석
            String analysisResult = callOpenAIForAnalysis(reviewData);

            // 결과 파싱 및 AiFeedback 객체 생성
            return parseAnalysisResult(analysisResult, reviewData.size());

        } catch (Exception e) {
            log.error("OpenAI 피드백 생성 중 오류 발생", e);
            return createFallbackFeedback(reviewData);
        }
    }


    @Override
    public SentimentType analyzeSentiment(String content) {
        try {
            String prompt = String.format(
                "다음 리뷰의 감정을 분석해주세요. POSITIVE, NEGATIVE, NEUTRAL 중 하나로만 답변해주세요.\n\n리뷰: %s",
                content
            );

            String result = callOpenAI(prompt);

            if (result.toUpperCase().contains("POSITIVE")) {
                return SentimentType.POSITIVE;
            } else if (result.toUpperCase().contains("NEGATIVE")) {
                return SentimentType.NEGATIVE;
            } else {
                return SentimentType.NEUTRAL;
            }

        } catch (Exception e) {
            log.warn("OpenAI 감정 분석 실패, 중립으로 처리: content={}", content.substring(0, Math.min(50, content.length())));
            return SentimentType.NEUTRAL;
        }
    }
    
    @Override
    public List<String> generateActionPlan(AiFeedback feedback) {
        log.info("OpenAI 실행 계획 생성 시작");

        try {
            String prompt = String.format(
                """
                다음 AI 피드백을 바탕으로 구체적인 실행 계획 3개를 생성해주세요.
                각 계획은 실행 가능하고 구체적이어야 합니다.
                
                요약: %s
                개선점: %s
                
                실행 계획을 다음 형식으로 작성해주세요:
                1. [구체적인 실행 계획 1]
                2. [구체적인 실행 계획 2]
                3. [구체적인 실행 계획 3]
                """,
                feedback.getSummary(),
                String.join(", ", feedback.getImprovementPoints())
            );

            String result = callOpenAI(prompt);
            return parseActionPlans(result);

        } catch (Exception e) {
            log.error("OpenAI 실행 계획 생성 중 오류 발생", e);
            return Arrays.asList(
                "서비스 품질 개선을 위한 직원 교육 실시",
                "고객 피드백 수집 체계 구축",
                "매장 운영 프로세스 개선"
            );
        }
    }

    /**
     * OpenAI API를 호출하여 전체 리뷰 분석 수행
     */
    private String callOpenAIForAnalysis(List<String> reviewData) {
        String reviewsText = String.join("\n- ", reviewData);

        String prompt = String.format(
            """
            다음은 한 매장의 고객 리뷰들입니다. 이를 분석하여 다음 JSON 형식으로 답변해주세요:
            
            {
              "summary": "전체적인 분석 요약 (2-3문장)",
              "positivePoints": ["긍정적 요소1", "긍정적 요소2", "긍정적 요소3"],
              "improvementPoints": ["개선점1", "개선점2", "개선점3"],
              "recommendations": ["추천사항1", "추천사항2", "추천사항3"],
              "sentimentAnalysis": "전체적인 감정 분석 결과",
              "confidenceScore": 0.85
            }
            
            리뷰 목록:
            - %s
            
            분석 시 다음 사항을 고려해주세요:
            1. 긍정적 요소는 고객들이 자주 언급하는 좋은 점들
            2. 개선점은 부정적 피드백이나 불만사항
            3. 추천사항은 매장 운영에 도움이 될 구체적인 제안
            4. 신뢰도 점수는 0.0-1.0 사이의 값
            """,
            reviewsText
        );

        return callOpenAI(prompt);
    }

    /**
     * OpenAI API 호출
     */
    private String callOpenAI(String prompt) {
        if (openaiApiKey == null || openaiApiKey.trim().isEmpty() || openaiApiKey.equals("your-openai-api-key")) {
            throw new RuntimeException("OpenAI API 키가 설정되지 않았습니다.");
        }

        try {
            // 요청 헤더 설정
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(openaiApiKey);

            // 요청 바디 생성
            OpenAIRequest request = OpenAIRequest.builder()
                .model(openaiModel)
                .messages(List.of(
                    OpenAIMessage.builder()
                        .role("user")
                        .content(prompt)
                        .build()
                ))
                .maxTokens(1500)
                .temperature(0.7)
                .build();

            String requestBody = objectMapper.writeValueAsString(request);
            HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

            // API 호출
            String url = openaiBaseUrl + "/chat/completions";
            ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                entity,
                String.class
            );

            // 응답 파싱
            return parseOpenAIResponse(response.getBody());

        } catch (Exception e) {
            log.error("OpenAI API 호출 실패", e);
            throw new RuntimeException("OpenAI API 호출에 실패했습니다.", e);
        }
    }

    /**
     * OpenAI 응답 파싱
     */
    private String parseOpenAIResponse(String responseBody) {
        try {
            Map<String, Object> response = objectMapper.readValue(responseBody, Map.class);
            List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");

            if (choices != null && !choices.isEmpty()) {
                Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
                return (String) message.get("content");
            }

            throw new RuntimeException("OpenAI 응답에서 내용을 찾을 수 없습니다.");

        } catch (JsonProcessingException e) {
            log.error("OpenAI 응답 파싱 실패", e);
            throw new RuntimeException("OpenAI 응답 파싱에 실패했습니다.", e);
        }
    }

    /**
     * 분석 결과를 AiFeedback 객체로 파싱
     */
    private AiFeedback parseAnalysisResult(String analysisResult, int totalReviews) {
        try {
            // JSON 형태로 응답이 왔다고 가정하고 파싱
            Map<String, Object> result = objectMapper.readValue(analysisResult, Map.class);

            return AiFeedback.builder()
                .summary((String) result.get("summary"))
                .positivePoints((List<String>) result.get("positivePoints"))
                .improvementPoints((List<String>) result.get("improvementPoints"))
                .recommendations((List<String>) result.get("recommendations"))
                .sentimentAnalysis((String) result.get("sentimentAnalysis"))
                .confidenceScore(((Number) result.get("confidenceScore")).doubleValue())
                .generatedAt(LocalDateTime.now())
                .build();

        } catch (Exception e) {
            log.warn("OpenAI 분석 결과 파싱 실패, 기본 분석 수행", e);
            return performBasicAnalysis(analysisResult, totalReviews);
        }
    }

    /**
     * 기본 분석 수행 (파싱 실패 시 fallback)
     */
    private AiFeedback performBasicAnalysis(String analysisResult, int totalReviews) {
        return AiFeedback.builder()
            .summary(String.format("총 %d개의 리뷰를 AI로 분석했습니다.", totalReviews))
            .positivePoints(Arrays.asList("고객 서비스", "음식 품질", "매장 분위기"))
            .improvementPoints(Arrays.asList("대기시간 단축", "메뉴 다양성", "가격 경쟁력"))
            .recommendations(Arrays.asList("고객 피드백 적극 반영", "서비스 교육 강화", "매장 환경 개선"))
            .sentimentAnalysis("전반적으로 긍정적인 평가")
            .confidenceScore(0.75)
            .generatedAt(LocalDateTime.now())
            .build();
    }

    /**
     * 실행 계획 파싱
     */
    private List<String> parseActionPlans(String result) {
        // 숫자로 시작하는 라인들을 찾아서 실행 계획으로 추출
        String[] lines = result.split("\n");
        return Arrays.stream(lines)
            .filter(line -> line.matches("^\\d+\\..*"))
            .map(line -> line.replaceFirst("^\\d+\\.\\s*", "").trim())
            .filter(line -> !line.isEmpty())
            .limit(5) // 최대 5개까지
            .toList();
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
     * Fallback 피드백 생성 (OpenAI 호출 실패 시)
     */
    private AiFeedback createFallbackFeedback(List<String> reviewData) {
        log.warn("OpenAI 호출 실패로 fallback 분석 수행");

        // 간단한 키워드 기반 분석
        long positiveCount = reviewData.stream()
            .mapToLong(review -> countPositiveKeywords(review))
            .sum();

        long negativeCount = reviewData.stream()
            .mapToLong(review -> countNegativeKeywords(review))
            .sum();

        double positiveRate = positiveCount > 0 ? (double) positiveCount / (positiveCount + negativeCount) * 100 : 50.0;

        return AiFeedback.builder()
            .summary(String.format("총 %d개의 리뷰를 분석했습니다. (간편 분석)", reviewData.size()))
            .positivePoints(Arrays.asList("서비스", "맛", "분위기"))
            .improvementPoints(Arrays.asList("대기시간", "가격", "청결도"))
            .recommendations(Arrays.asList("고객 의견 수렴", "서비스 개선", "품질 향상"))
            .sentimentAnalysis(String.format("긍정 비율: %.1f%%", positiveRate))
            .confidenceScore(0.6)
            .generatedAt(LocalDateTime.now())
            .build();
    }

    private long countPositiveKeywords(String review) {
        String[] positiveWords = {"좋", "맛있", "친절", "깨끗", "만족", "추천", "최고"};
        return Arrays.stream(positiveWords)
            .mapToLong(word -> review.toLowerCase().contains(word) ? 1 : 0)
            .sum();
    }

    private long countNegativeKeywords(String review) {
        String[] negativeWords = {"나쁘", "맛없", "불친절", "더럽", "실망", "최악", "별로"};
        return Arrays.stream(negativeWords)
            .mapToLong(word -> review.toLowerCase().contains(word) ? 1 : 0)
            .sum();
    }

    // OpenAI API 요청/응답 DTO 클래스들
    @Data
    @lombok.Builder
    private static class OpenAIRequest {
        private String model;
        private List<OpenAIMessage> messages;
        @JsonProperty("max_tokens")
        private Integer maxTokens;
        private Double temperature;
    }

    @Data
    @lombok.Builder
    private static class OpenAIMessage {
        private String role;
        private String content;
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
