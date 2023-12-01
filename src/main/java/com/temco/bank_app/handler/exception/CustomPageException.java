package com.temco.bank_app.handler.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class CustomPageException extends RuntimeException{
	
	private HttpStatus httpStatus;

	
	public CustomPageException(String message, HttpStatus status) {
		super(message); //부모 생성자 호출
		this.httpStatus = status;
		
	}
	

}
