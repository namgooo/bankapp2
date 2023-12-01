package com.temco.bank_app.handler;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import com.temco.bank_app.handler.exception.CustomPageException;


// view 렌더링을 위해 Modelview 객체를 반환하도록 설정 되어 있다.
// 예외 처리 page 리턴할 때 사용
@ControllerAdvice
public class MyPageExceptionHandler {
	
	// @ExceptionHandler는 컨트롤러에서 발생하는 에러를 잡아서 메서드로
	// 처리해주는 기능이다.(Service와 Repository에서 발생하는 에러는 제외)
	@ExceptionHandler(CustomPageException.class)
	public ModelAndView handleRuntimeException(CustomPageException e) {
		ModelAndView modelAndView = new ModelAndView("errorPage");
		modelAndView.addObject("statusCode", HttpStatus.NOT_FOUND.value());
		modelAndView.addObject("message", e.getMessage());
		return modelAndView;
	}
	
}
