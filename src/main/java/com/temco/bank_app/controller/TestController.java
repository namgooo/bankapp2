package com.temco.bank_app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("/temp")
public class TestController {

	// GET 방식
	// 주소설계 = http://localhost:80/temp/temp-test
	@GetMapping("/temp-test")
	public String tempTest() {
		
		// yml 파일
		//   prefix: /WEB-INF/view/
		// suffix: .jsp
		return "temp";
		
	}

	// Get
	// 주소 설계  = http://localhost:80/temp/main-page
	@GetMapping("/main-page")
	public String tempMainPage() {
		
		return "main";
	}
	
	
	

	
//	@GetMapping("/")
//	public String root() {
//		
//		return "redirect:/temp/temp-test";
//		
//	}
	
}
