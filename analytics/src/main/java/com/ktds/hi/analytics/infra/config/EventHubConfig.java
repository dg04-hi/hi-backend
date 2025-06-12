package com.ktds.hi.analytics.infra.config;

import com.azure.messaging.eventhubs.EventHubClientBuilder;
import com.azure.messaging.eventhubs.EventHubConsumerClient;
import com.azure.messaging.eventhubs.EventHubProducerClient;
import com.azure.messaging.eventhubs.checkpointstore.blob.BlobCheckpointStore;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Azure Event Hub 설정 클래스
 * Event Hub 클라이언트와 체크포인트 스토어를 구성
 */
@Slf4j
@Configuration
public class EventHubConfig {
    
    @Value("${azure.eventhub.connection-string}")
    private String connectionString;
    
    @Value("${azure.eventhub.consumer-group}")
    private String consumerGroup;
    
    @Value("${azure.storage.connection-string}")
    private String storageConnectionString;
    
    @Value("${azure.storage.container-name}")
    private String containerName;
    
    @Value("${azure.eventhub.event-hubs.review-events}")
    private String reviewEventsHub;
    
    @Value("${azure.eventhub.event-hubs.ai-analysis-events}")
    private String aiAnalysisEventsHub;
    
    /**
     * 리뷰 이벤트 수신용 Consumer 클라이언트
     */
    @Bean("reviewEventConsumer")
    public EventHubConsumerClient reviewEventConsumer() {
        BlobContainerClient blobContainerClient = createBlobContainerClient();
        BlobCheckpointStore checkpointStore = new BlobCheckpointStore(blobContainerClient);
        
        return new EventHubClientBuilder()
                .connectionString(connectionString, reviewEventsHub)
                .consumerGroup(consumerGroup)
                .checkpointStore(checkpointStore)
                .buildConsumerClient();
    }
    
    /**
     * AI 분석 결과 발행용 Producer 클라이언트
     */
    @Bean("aiAnalysisEventProducer")
    public EventHubProducerClient aiAnalysisEventProducer() {
        return new EventHubClientBuilder()
                .connectionString(connectionString, aiAnalysisEventsHub)
                .buildProducerClient();
    }
    
    /**
     * Blob 컨테이너 클라이언트 생성
     */
    private BlobContainerClient createBlobContainerClient() {
        return new BlobServiceClientBuilder()
                .connectionString(storageConnectionString)
                .buildClient()
                .getBlobContainerClient(containerName);
    }
}
