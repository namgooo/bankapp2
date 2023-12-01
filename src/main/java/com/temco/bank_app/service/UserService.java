package com.temco.bank_app.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.temco.bank_app.dto.SignInFormDto;
import com.temco.bank_app.dto.SignUpFormDto;
import com.temco.bank_app.handler.exception.CustomRestfullException;
import com.temco.bank_app.repository.entity.User;
import com.temco.bank_app.repository.interfaces.UserRepository;

@Service
public class UserService {
	
	@Autowired // 의존주입 (생성자, 메서드)
	private UserRepository userRepository;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	// 회원 가입 기능
	@Transactional
	// 메소드, 클래스, 인터페이스 위에 추가하여 사용하는 방식이 일반적이다.
	// 프록시 객체가 생성되어 자동으로 commit 혹은 rollback을 진행해준다.
	public int signUp(SignUpFormDto dto) {
		
		String rawPwd = dto.getPassword();
		String hashPwd = passwordEncoder.encode(rawPwd); 
		System.out.println("hashPwd : " + hashPwd);
		
		User user = User.builder()
					.username(dto.getUsername())
					.password(hashPwd)
					.fullname(dto.getFullname())
					.originFileName(dto.getOriginFileName())
					.uploadFileName(dto.getUploadFileName())
					.build();
		
		int resultRowCount = userRepository.insert(user);
		
		if(resultRowCount !=1 ) {	
			throw new CustomRestfullException("회원 가입 실패", 
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		return resultRowCount;
		
	}
	
	
	// 로그인 기능
	public User signIn(SignInFormDto dto) {
		
		
		// 1. username 아이디 존재 여부 확
		User userEntity = this.userRepository.findByUsername(dto.getUsername());
		
		if(userEntity == null) {
			throw new CustomRestfullException("존재하지 않는 계정입니다.", HttpStatus.BAD_REQUEST);
		}
		
		boolean isPwdMatched = passwordEncoder.matches(dto.getPassword(), userEntity.getPassword());
		
		// 2. 객체 상태값의 비번과 암호화된 비밀번호 일치 여부 확인
			
		if(isPwdMatched == false) {
			throw new CustomRestfullException("비밀번호가 잘못 되었습니다", HttpStatus.BAD_REQUEST);			
		}

		
		return userEntity;
		
	}
	
	
	// 카카오 로그인 (유저이름 찾기)
	public User searchUsername(String username) {
		
		return this.userRepository.findByUsername(username);
		
	}
	
	
	

}
