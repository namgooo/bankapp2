package com.temco.bank_app.handler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.temco.bank_app.handler.exception.UnAuthorizedException;
import com.temco.bank_app.repository.entity.User;
import com.temco.bank_app.utils.Define;


// 만드는 방법
// 1. HandlerInterceptor 구현 IoC 대상
@Component // IoC 대상 - 싱글톤 관리
public class AuthIntercepter implements HandlerInterceptor{
	
	// controller에 들어오기 전에 동작
	// controller에 보내기 위해서 리턴값이 true , false는 안들어감
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		
		System.out.println("인터셉터 동작중");
		
		// 세션에 사용자 정보 확인
		HttpSession session =  request.getSession();
		
		User principal = (User)session.getAttribute(Define.PRINCIPAL);
		
		if(principal == null) {
			// response.sendRedirect("/user/sign-in");
			throw new UnAuthorizedException("로그인 먼저 해주세요.", HttpStatus.UNAUTHORIZED);
		}
		
		return true;
		
	}
	
	
	// 뷰가 렌더링 되기 전에 호출 되는 메서드
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		// TODO Auto-generated method stub
		HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
	}
	
	// 요청 처리가 완료된 후, 즉 뷰 렌더링이 완료된 후에 호출 되는 메서드
	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		// TODO Auto-generated method stub
		HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
	}
	
	
}
