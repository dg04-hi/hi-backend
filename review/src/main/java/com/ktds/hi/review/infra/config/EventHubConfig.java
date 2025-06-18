package com.ktds.hi.review.infra.config;

import com.azure.messaging.eventhubs.EventHubClientBuilder;
import com.azure.messaging.eventhubs.EventHubConsumerClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Azure Event Hub 설정 클래스 (단일 EntityPath 포함된 connection string 사용)
 */
@Slf4j
@Configuration
public class EventHubConfig {

    @Value("${azure.eventhub.connection-string}")
    private String connectionString;

    @Value("${azure.eventhub.consumer-group:$Default}")
    private String consumerGroup;

    /**
     * 외부 리뷰 이벤트 수신용 Consumer
     */
    @Bean("externalReviewEventConsumer")
    public EventHubConsumerClient externalReviewEventConsumer() {
        return new EventHubClientBuilder()
                .connectionString(connectionString)
                .consumerGroup(consumerGroup)
                .buildConsumerClient();
    }
}
