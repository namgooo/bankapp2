package com.temco.bank_app.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.temco.bank_app.handler.AuthIntercepter;


// 스프링 부트 설정 클래스이다 의미
@Configuration // Ioc 등록 --> 2개 이상의 Ioc 등록 처리
public class WebMvcConfig implements WebMvcConfigurer{

	@Autowired
	private AuthIntercepter authIntercepter; 
	
	
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		
		registry.addInterceptor(authIntercepter)
				.addPathPatterns("/account/**")
				.addPathPatterns("/auth/**"); // 추가하는 방법
			
	}
	
	// 리소스 등록 처리
	// 서버 컴퓨터에 위치한 Resource를 활용하는 방법(프로젝트 외부 폴더 접근)
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/images/uploads/**")
		.addResourceLocations("file:///C:\\spring_upload\\bank\\upload/");
	}
	

	@Bean // Ioc 관리 대상 처리 - 싱글톤
	// 비밀번호 인코딩해줌
	public PasswordEncoder passwordEncoder() {
		return new  BCryptPasswordEncoder();
	}
	
	
	
	
	
}
