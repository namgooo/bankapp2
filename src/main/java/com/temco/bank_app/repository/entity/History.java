package com.temco.bank_app.repository.entity;

import java.sql.Timestamp;
import java.text.DecimalFormat;

import com.temco.bank_app.utils.TimeStampUtil;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class History {
	
	private Integer id;
	private Long amount;
	private Long wBalance;
	private Long dBalance;
	private Integer wAccountId;
	private Integer dAccountId;
	private Timestamp createdAt;
	
	// 거래내역 정보 추가
	private String sender;
	private String receiver;
	private Long balance;

	
	public String formatCreatedAt() {
		
		return TimeStampUtil.timestampToString(createdAt);
	}
	
	public String formatBalance() {
		
		// data format 천단위에 쉼표 찍는 기능을 구현하세요.
		// 1,000원
		DecimalFormat df = new DecimalFormat("#.###");
		
		String fromatBalance = df.format(balance);
		
		return fromatBalance + "원";
	}

}
