package com.temco.bank_app.controller;

import java.io.File;
import java.util.UUID;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.temco.bank_app.dto.SignInFormDto;
import com.temco.bank_app.dto.SignUpFormDto;
import com.temco.bank_app.dto.respones.KakaoProfile;
import com.temco.bank_app.dto.respones.OAuthToken;
import com.temco.bank_app.handler.exception.CustomRestfullException;
import com.temco.bank_app.repository.entity.User;
import com.temco.bank_app.service.UserService;
import com.temco.bank_app.utils.Define;

import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/user")
@Slf4j
public class UserController {
	
	// 생성자 만들어줌
	@Autowired // DI 처리
	private UserService userService;
	
	@Autowired
	private HttpSession session;
	
	// yml 파일에 있는 초기 파라미터 설정한 변수를 가지고 왔음.
	@Value("${tenco.key}")
	private String tencoKey;
	
	
	// 회원 가입 페이지 요청 (GET)
	// http://localhost:80/user/sign-up
	@GetMapping("/sign-up")
	public String signUp() {
		
		return "user/signUp";
	}
	
	// 로그인 페이지 요청 (GET)
	// http://localhost:80/user/sign-in
	@GetMapping("/sign-in")
	public String signIn() {
		
		return "user/signIn";
	}
	
	/**
	 * 회원 가입 처리 (POST)
	 * @param dto
	 * @return redirect 로그인 페이지 처리
	 */
	// 자원의 등록 요청
	@PostMapping("/sign-up")
	public String signUpProc(SignUpFormDto dto) {
		
		// 1. 유효성 검사
		if(dto.getUsername() == null || dto.getUsername().isEmpty()) {
			throw new CustomRestfullException("username을 입력하세요", HttpStatus.BAD_REQUEST);	
		}
		
		if(dto.getPassword() == null || dto.getPassword().isEmpty()) {			
			throw new CustomRestfullException("password을 입력하세요", HttpStatus.BAD_REQUEST);
		}
		
		if(dto.getFullname() == null || dto.getFullname().isEmpty()) {		
			throw new CustomRestfullException("fullname을 입력하세요", HttpStatus.BAD_REQUEST);	
		}
		
		// 사용자 프로필 이미지 등록 처리
		MultipartFile file = dto.getFile();
		if(file.isEmpty() == false) {
			
			// 파일 사이즈 체크
			if(file.getSize() > Define.MAX_FILE_SIZE) {
				throw new CustomRestfullException("파일 크기는 20MB 이상 클 수 없음", HttpStatus.BAD_REQUEST);
			}
			
			try {
				// 업로드 파일 경로
				String saveDirectory = Define.UPLOAD_DIRECTORY;
				// 폴더가 없다면 오류 발생
				File dir = new File(saveDirectory);
				
				if(dir.exists() == false) {
					dir.mkdir(); // 폴더가 없다면 생성
				}
				
				// 파일 이름 (중복 이름 처리 예방)
				UUID uuid = UUID.randomUUID();
				
				// 새로운 파일 이름 생성
				String fileName = uuid + "_" + file.getOriginalFilename();
				
				// 전체 경로 지정 생성
				String uploadPath = Define.UPLOAD_DIRECTORY + File.separator + fileName;
				
				System.out.println("uploadPath" + uploadPath);
				File destination = new File(uploadPath);
				
				// 반드시 사용
				file.transferTo(destination); // 실제 생성
				
				// 객체 상태 변경 (insert 처리 하기 위함 -> 쿼리 수정해야 함)
				dto.setOriginFileName(file.getOriginalFilename()); // 사용자가 입력한 파일명
				dto.setUploadFileName(fileName);
				
				
			} catch (Exception e) {
				
				System.out.println(e.getMessage());
			}
			
			
		}
		
		
			userService.signUp(dto);
		
	
		return "redirect:/user/sign-in";
	}
	
	
	
	// 로그인 요청 (POST)
	@PostMapping("/sign-in")
	public String signInProc(SignInFormDto dto) {
		
		// 1. 유효성 검사
		
		if(dto.getUsername() == null || dto.getUsername().isEmpty()) {
			throw new CustomRestfullException("username을 입력하세요", HttpStatus.BAD_REQUEST);
		}
		if(dto.getPassword() == null || dto.getPassword().isEmpty()) {
			throw new CustomRestfullException("password을 입력하세요", HttpStatus.BAD_REQUEST);
		}
		
		
		// 서비스 호출
		User principal = userService.signIn(dto);
		session.setAttribute(Define.PRINCIPAL, principal); // 세션 메모리에 저장
		
//		System.out.println("principal" + principal.toString());
		
		
		return "account/list";
	}
	
	
	
	// 로그아웃 요청 (GET)
	@GetMapping("/logout")
	public String logout() {
		
		session.invalidate();
		
		return "redirect:sign-in";
		
	}
	
	
	// 카카오 로그인
	// http://localhost:80/user/kakao-calback?code=fafdaffdafa (code=아무거나)
	@GetMapping("/kakao-callback")
	// @ResponseBody // Controller는 기본적으로 jsp 파일을 찾기만 @ResponseBody를 사용하면 데이터만 찾음
	public String kakaoCallBack(@RequestParam String code) {
		
		// 액세스 토큰 요청 -> Server to Server 를 처리해야 함
		
		// 스프링부트에서 제공하는 통신 객체 (RestTemplate)
		RestTemplate rt1 = new RestTemplate();
		
		// 헤더 구성
		HttpHeaders headers1 = new HttpHeaders();
		
		headers1.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
		
		// 바디 구성
		MultiValueMap<String, String> params1 = new LinkedMultiValueMap<>();
		params1.add("grant_type", "authorization_code");
		params1.add("client_id", "110c8ade440625c2d4aee082f2bb89f9");
		params1.add("redirect_uri", "http://localhost:80/user/kakao-callback");
		params1.add("code", code);
		
		// 헤더와 바디 결합
		HttpEntity<MultiValueMap<String, String>> requestMsg1 = new HttpEntity<>(params1, headers1);
		
		// 요청 처리
		ResponseEntity<OAuthToken> response1 = rt1.exchange("https://kauth.kakao.com/oauth/token", HttpMethod.POST, requestMsg1, OAuthToken.class);
		
		
		System.out.println("______________________________________________");
		System.out.println(response1.getHeaders());
		System.out.println(response1.getBody());
		System.out.println(response1.getBody().getAccessToken());
		System.out.println(response1.getBody().getRefreshToken());
		System.out.println("______________________________________________");		
		// 여기까지 과정이 토큰 받기 위함
		
		

		RestTemplate rt2 = new RestTemplate();
		
		// 헤더 구성
		HttpHeaders headers2 = new HttpHeaders();
		headers2.add("Authorization", "Bearer " + response1.getBody().getAccessToken());
		headers2.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
		
		// 바디 구성 생략(필수 아님)
		
		// 헤더 바디 결합
		HttpEntity<MultiValueMap<String, String>> requestMsg2 = new HttpEntity<>(headers2);
		
		// 요청
		ResponseEntity<KakaoProfile> response2 = rt2.exchange("https://kapi.kakao.com/v2/user/me", HttpMethod.POST, requestMsg2, KakaoProfile.class);
		
		System.out.println("__________ 카카오 서버 정보 받기 완료 ______________");
		System.out.println("_______________________________________________");
		System.out.println(response2.getBody().getProperties().getNickname());
		System.out.println(response2.getBody().getProperties().getProfileImage());
		System.out.println(response2.getBody().getProperties().getThumbnailImage());
		System.out.println("_______________________________________________");
		System.out.println("__________ 카카오 서버 정보 받기 완료 ______________");
		// 여기 까지 카카오 서버에 존재하는 정보를 요청 처리 과정

		
	
		// 1. 회원 가입 여부 확인
		// 최초 사용자라면 우리 사이트에 회원 가입을 자동 완료
		// 추가 정보 입력 화면 (추가 정보가 있다면 기능을 만들기) -> DB 저장 처리
		
		KakaoProfile kakaoProfile = response2.getBody();
		
		// 소셜 회원 가입자는 전부 비밀번호가 동일하게 된다.
		SignUpFormDto signUpFormDto = SignUpFormDto.builder()
				                      .username("OAuth_" + kakaoProfile.getId() + "님")
				                      .fullname("Kakao")
				                      .password(tencoKey)
				                      .file(null)
				                      .originFileName(null)
				                      .uploadFileName(null)
				                      .build();
		
		// tencoKey가 콘솔에 찍히는지 확인
		System.out.println("tencoKey : " + tencoKey);
		
		// null 일때는 세션에 로그인을 하기 위해 값을 할당 해주어야 한다.
		User oldUser = this.userService.searchUsername(signUpFormDto.getUsername());
		
		if(oldUser == null) {
			// 여기는 oldUser가 null 이라면 최초 회원 가입 처리를 해주어야 한다.
			// 회원가입 자동 처리
			this.userService.signUp(signUpFormDto); // 회원 가입 됨
			
		}

		// 로그인 처리
		// 만약 소셜 로그인 사용자가 회원가입 처리 완료된 사용자라면
		// 바로 세션 처리 및 로그인 처리
		oldUser.setPassword(null);
		session.setAttribute(Define.PRINCIPAL, oldUser);
		
		return "redirect:/account/list";
			
	}
	
	@GetMapping("/show")
	@ResponseBody
	public String show() {
		
		
		
		return "";
	}
	

}
