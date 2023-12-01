package com.temco.bank_app.dto;

import lombok.Data;

@Data
public class WithdrawFormDto {

	private Long amount;
	
	private String wAccountNumber;
	
	private String password;
	
	
}
