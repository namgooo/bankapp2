package com.temco.bank_app.dto;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignUpFormDto {
	
	private String username;
	private String password;
	private String fullname;
	
	// 파일 업로드 (.jsp에서 name 속성과 일치 시켜야함)
	private MultipartFile file;
	
	private String originFileName;
	private String uploadFileName;

}
