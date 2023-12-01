package com.temco.bank_app.dto.respones;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import lombok.Data;

@Data
@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
public class KakaoProfile {

	private Long id;
	
	private String connectedAt;
	
	private Properties Properties;
	
	
//	// 이렇게 내부 클래스로도 사용 가능
//	@Data
//	public class Properties{
//		
//		private String nickname;
//		
//		private String profileImage;
//		
//		private String thumbnailImage;
//		
//	}
	
   
}
