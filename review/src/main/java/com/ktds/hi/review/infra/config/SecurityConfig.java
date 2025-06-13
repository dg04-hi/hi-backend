package com.ktds.hi.review.infra.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfigurationSource;

import lombok.RequiredArgsConstructor;

/**
 * Analytics 서비스 보안 설정 클래스
 * 테스트를 위해 모든 엔드포인트를 인증 없이 접근 가능하도록 설정
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	private final CorsConfigurationSource corsConfigurationSource;

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
			.csrf(AbstractHttpConfigurer::disable)
			.cors(cors -> cors.configurationSource(corsConfigurationSource))
			.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.authorizeHttpRequests(auth -> auth
				// Swagger 관련 경로 모두 허용
				.requestMatchers("/swagger-ui.html","/swagger-ui/**", "/swagger-ui.html").permitAll()
				.requestMatchers("/api-docs/**", "/v3/api-docs/**").permitAll()
				.requestMatchers("/swagger-resources/**", "/webjars/**").permitAll()

				// Analytics API 모두 허용 (테스트용)
				.requestMatchers("/api/analytics/**").permitAll()
				.requestMatchers("/api/action-plans/**").permitAll()

				// Actuator 엔드포인트 허용
				.requestMatchers("/actuator/**").permitAll()

				// 기타 모든 요청 허용 (테스트용)
				.anyRequest().permitAll()
			);

		return http.build();
	}
}
