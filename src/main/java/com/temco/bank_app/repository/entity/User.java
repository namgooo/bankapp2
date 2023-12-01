package com.temco.bank_app.repository.entity;

import java.security.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

	private Integer id;
	private String username;
	private String password;
	private String fullname;
	// 파일 업로드 컬럼
	private String originFileName;
	private String uploadFileName;
	
	private Timestamp createdAt;
	
	public String setUpUserImage() {
		
		return uploadFileName == null ?
				"https://picsum.photos/id/1/350" : "/images/uploads/" + uploadFileName;
		
	}
	
	
}
