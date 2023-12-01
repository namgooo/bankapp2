package com.temco.bank_app.repository.entity;

import java.security.Timestamp;

import org.springframework.http.HttpStatus;

import com.temco.bank_app.handler.exception.CustomRestfullException;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Account {

	private Integer id;
	private String number;
	private String password;
	private Long balance;
	private Integer userId;
	private Timestamp createdAt;
	
	// 출금 기능
	public void withdraw(long amount) {
		
		// 방어적 코드 작성 예정
		this.balance -= amount;
		
	}
	
	
	// 입금 기능
	public void deposit(Long amount) {
		
		this.balance += amount;
		
	}

	// password 체크 기능
	public void checkPassword(String password) {
		if(this.password.equals(password) == false) {
			throw new CustomRestfullException("계좌 비밀번호가 틀렸습니다.",HttpStatus.BAD_REQUEST);
		}
	}
	
	// 잔액 여부 확인
	public void checkBalance(Long amount) {
		if(this.balance < amount) {
			throw new CustomRestfullException("출금 잔액이 부족합니다.",HttpStatus.BAD_REQUEST);
		}
	}
	
	// 계좌 소유자 확인 기능
	public void checkOwner(Integer principalId) {
		if(this.userId != principalId) {
			throw new CustomRestfullException("본인 계좌가 아닙니다.",HttpStatus.BAD_REQUEST);
		}
		
	}
	
	
}
