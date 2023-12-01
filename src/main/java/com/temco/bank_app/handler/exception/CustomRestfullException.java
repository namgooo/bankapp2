package com.temco.bank_app.handler.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class CustomRestfullException extends RuntimeException{
	
	private HttpStatus status;
	
	public CustomRestfullException(String message, HttpStatus httpStatus) {
		super(message); //부모 생성자 호출
		this.status = httpStatus;
		
	}
	
	

}
