package com.ktds.hi.store.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;

import lombok.extern.slf4j.Slf4j;

/**
 * Azure Blob Storage 설정
 *
 * @author 하이오더 개발팀
 * @version 1.0.0
 */
@Slf4j
// @Configuration
public class AzureStorageConfig {

	@Value("${azure.storage.connection-string}")
	private String connectionString;

	@Bean
	public BlobServiceClient blobServiceClient() {
		log.info("Azure Blob Storage 클라이언트 초기화");
		return new BlobServiceClientBuilder()
			.connectionString(connectionString)
			.buildClient();
	}
}