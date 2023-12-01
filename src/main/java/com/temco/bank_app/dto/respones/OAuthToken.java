package com.temco.bank_app.dto.respones;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import lombok.Data;

// {"access_token":"Z6G06hSi0yuq_-y09R8PpdAKwclfSEQEnjMKPXUZAAABjB4QRXd-jFVpBnvzXw",
//  "token_type":"bearer",
//  "refresh_token":"X8144EaPFxhr8xrgomGBp3GD3q5QfJyiEPMKPXUZAAABjB4QRXR-jFVpBnvzXw",
//  "expires_in":21599,"scope":"profile_image profile_nickname",
//  "refresh_token_expires_in":5183999}

@Data
// JSON 형식에 코딩 컨벤션의 스테이크 케이스를 자바 카멜 노테이션으로 변환 처리
@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class) // 스네이크 > 카벨로 변환해줌
public class OAuthToken {

	private String accessToken;
	private String tokenType;
	private String refreshToken;
	private String expiresIn;
	private String scope;
	
}
