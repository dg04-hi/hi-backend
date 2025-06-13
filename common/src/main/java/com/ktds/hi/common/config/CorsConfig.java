package com.ktds.hi.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;
import java.util.List;

/**
 * 전체 서비스 통합 CORS 설정 클래스
 * 모든 마이크로서비스에서 공통으로 사용되는 CORS 정책을 정의
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

	@Value("${app.cors.allowed-origins:http://localhost:3000,http://localhost:8080,http://localhost:3001}")
	private String allowedOrigins;

	@Value("${app.cors.allowed-methods:GET,POST,PUT,DELETE,PATCH,OPTIONS}")
	private String allowedMethods;

	@Value("${app.cors.allowed-headers:*}")
	private String allowedHeaders;

	@Value("${app.cors.exposed-headers:Authorization,X-Total-Count}")
	private String exposedHeaders;

	@Value("${app.cors.allow-credentials:true}")
	private boolean allowCredentials;

	@Value("${app.cors.max-age:3600}")
	private long maxAge;

	/**
	 * WebMvcConfigurer를 통한 CORS 설정
	 */
	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**")
			.allowedOriginPatterns(allowedOrigins.split(","))
			.allowedMethods(allowedMethods.split(","))
			.allowedHeaders(allowedHeaders.split(","))
			.exposedHeaders(exposedHeaders.split(","))
			.allowCredentials(allowCredentials)
			.maxAge(maxAge);
	}

	/**
	 * CorsConfigurationSource Bean 생성
	 * Spring Security와 함께 사용되는 CORS 설정
	 */
	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();

		// Origin 설정
		List<String> origins = Arrays.asList(allowedOrigins.split(","));
		configuration.setAllowedOriginPatterns(origins);

		// Method 설정
		List<String> methods = Arrays.asList(allowedMethods.split(","));
		configuration.setAllowedMethods(methods);

		// Header 설정
		if ("*".equals(allowedHeaders)) {
			configuration.addAllowedHeader("*");
		} else {
			List<String> headers = Arrays.asList(allowedHeaders.split(","));
			configuration.setAllowedHeaders(headers);
		}

		// Exposed Headers 설정
		List<String> exposed = Arrays.asList(exposedHeaders.split(","));
		configuration.setExposedHeaders(exposed);

		// Credentials 설정
		configuration.setAllowCredentials(allowCredentials);

		// Max Age 설정
		configuration.setMaxAge(maxAge);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}

	/**
	 * CorsFilter Bean 생성
	 * 글로벌 CORS 필터로 사용
	 */
	@Bean
	public CorsFilter corsFilter() {
		return new CorsFilter(corsConfigurationSource());
	}
}