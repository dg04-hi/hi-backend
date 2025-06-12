package com.ktds.hi.analytics.infra.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		// Swagger UI 리소스 핸들러 추가
		registry.addResourceHandler("/swagger-ui/**")
			.addResourceLocations("classpath:/META-INF/resources/webjars/swagger-ui/")
			.resourceChain(false);

		registry.addResourceHandler("/webjars/**")
			.addResourceLocations("classpath:/META-INF/resources/webjars/")
			.resourceChain(false);

		// 기본 정적 리소스 핸들러
		registry.addResourceHandler("/**")
			.addResourceLocations("classpath:/static/", "classpath:/public/")
			.resourceChain(false);

		registry.addResourceHandler("/static/**")
			.addResourceLocations("classpath:/static/");
	}
}