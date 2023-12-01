package com.temco.bank_app.controller;

import java.security.Principal;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.temco.bank_app.dto.DepositFormDto;
import com.temco.bank_app.dto.SaveFormDto;
import com.temco.bank_app.dto.TransferFormDto;
import com.temco.bank_app.dto.WithdrawFormDto;
import com.temco.bank_app.handler.exception.CustomRestfullException;
import com.temco.bank_app.handler.exception.UnAuthorizedException;
import com.temco.bank_app.repository.entity.Account;
import com.temco.bank_app.repository.entity.History;
import com.temco.bank_app.repository.entity.User;
import com.temco.bank_app.service.AccountService;
import com.temco.bank_app.utils.Define;

@Controller
@RequestMapping("/account")
public class AccountController {
	
	@Autowired
	private HttpSession session;
	
	@Autowired
	private AccountService accountService; 
	
	// 임시 예외 발생 확인 http://localhost:80/account/list
	@GetMapping({"/list","/"})
	public String list(Model model) {
		
		// 인증 검사
		User principal = (User)session.getAttribute(Define.PRINCIPAL);
		
		// 인증 검사 안하고 있음!!
		
		List<Account> accountList = accountService.readAccountList(principal.getId());
		
		
		if(accountList.isEmpty()) {
			model.addAttribute("accountList", null);
		}else {
			model.addAttribute("accountList", accountList);
		}

		
		return "account/list";
	}
	
	
	@GetMapping("/save")
	public String save() {
		
		// 인증 검사 안하고 있음!!
		
		return "account/save";
		
	}
	
	@PostMapping("/save")
	public String saveProc(SaveFormDto dto) {
		
		System.out.println("======================" + dto);
		
		// 1. 인증 검사
		User principal = (User)session.getAttribute(Define.PRINCIPAL);
		
		// 인증 검사 안하고 있음!!
		
		// 2. 유효성 검사
		if(dto.getNumber() == null || dto.getNumber().isEmpty() ) {
			throw new CustomRestfullException("계좌번호를 입력하세요.", HttpStatus.BAD_REQUEST);
		}
		
		if(dto.getPassword() == null || dto.getPassword().isEmpty() ) {
			throw new CustomRestfullException("비밀번호를 입력하세요.", HttpStatus.BAD_REQUEST);
		}
		
		if(dto.getBalance() == null || dto.getBalance() <= 0 ) {
			throw new CustomRestfullException("잘못된 입력입니다.", HttpStatus.BAD_REQUEST);
		}
	
		accountService.createAccount(dto, principal.getId());
		
		
		return "account/list";
		
	}
	
	
	// 출금 페이지 요청(GET)
	@GetMapping("/withdraw")
	public String withdraw() {
		
		// 인증 검사 안하고 있음!!
		
		return "account/withdraw";
	}
	
	
	
	// 출금 요청(POST)
	@PostMapping("/withdraw")
	public String withdrawProc(WithdrawFormDto dto) {
		
		// 1. 인증 검사
		User principal = (User)session.getAttribute(Define.PRINCIPAL);
		
		
		// 2. 유효성 검사
		if(dto.getAmount() == null) {
			throw new CustomRestfullException("금액을 입력하세요", HttpStatus.BAD_REQUEST);
		}
		if(dto.getAmount().longValue() <= 0) {
			throw new CustomRestfullException("출금 금액이 0원 이하일 수 없음", HttpStatus.BAD_REQUEST);
		}
		if(dto.getWAccountNumber() == null || dto.getWAccountNumber().isEmpty()) {	
			throw new CustomRestfullException("계좌 번호를 입력하세요.", HttpStatus.BAD_REQUEST);
		}
		if(dto.getPassword() == null || dto.getPassword().isEmpty()) {
			throw new CustomRestfullException("비밀번호를 입력하세요.", HttpStatus.BAD_REQUEST);
		}
		
		// 서비스 호출
		accountService.updateAccountWithdraw(dto, principal.getId());
	
		return "redirect:/account/list";
		
	}
	
	// 입금 요청(GET)
	@GetMapping("/deposit")
	public String deposit() {
		
		// 인증 검사 안하고 있음!!
		
		return "account/deposit";
	}
	
	
	
	// 입금 요청(POST)
	@PostMapping("/deposit")
	public String depositProc(DepositFormDto dto) {
		
		// 1. 인증 검사
		User principal = (User)session.getAttribute(Define.PRINCIPAL);
		
		// 2. 유효성 검사
		if(dto.getAmount() == null) {
			throw new CustomRestfullException("금액을 입력하세요", HttpStatus.BAD_REQUEST);
		}
		if(dto.getAmount().longValue() <= 0) {
			throw new CustomRestfullException("입금 금액이 0원 이하일 수 없음", HttpStatus.BAD_REQUEST);
		}
		if(dto.getDAccountNumber() == null || dto.getDAccountNumber().isEmpty()) {
			throw new CustomRestfullException("계좌 번호를 입력하세요.", HttpStatus.BAD_REQUEST);
		}
		
		// 3. 서비스 호출
		accountService.updateAccountDeposit(dto, principal.getId());
		
		return "redirect:/account/list";
	}
	
	
	// 이체 요청
	@GetMapping("/transfer")
	public String transfer() {
		
		// 인증 검사 안하고 있음!!
		
		return "account/transfer";
	}
	
	
	// 이체 요청
	@PostMapping("/transfer")
	public String transferProc(TransferFormDto dto) {
		
		// 1. 인증 검사
		User principal = (User)session.getAttribute(Define.PRINCIPAL);
			
		// 2. 유효성 검사
		if(dto.getAmount() == null) {
			throw new UnAuthorizedException("금액을 입력하세요", HttpStatus.BAD_REQUEST);
		}
		if(dto.getAmount().longValue() <= 0) {
			throw new UnAuthorizedException("입금 금액이 0원 이하일 수 없음", HttpStatus.BAD_REQUEST);
		}
		if(dto.getWAccountNumber() == null || dto.getWAccountNumber().isEmpty()) {
			throw new UnAuthorizedException("출금 계좌 번호를 입력하세요.", HttpStatus.BAD_REQUEST);
		}
		if(dto.getDAccountNumber() == null || dto.getDAccountNumber().isEmpty()) {
			throw new UnAuthorizedException("입금 계좌 번호를 입력하세요.", HttpStatus.BAD_REQUEST);
		}
		if(dto.getPassword() == null || dto.getPassword().isEmpty()) {
			throw new UnAuthorizedException("비밀번호를 입력하세요.", HttpStatus.BAD_REQUEST);
		}
		
		this.accountService.updateAccountTransfer(dto, principal.getId());
		
		return "redirect:/account/list";
	}
	
	
	// 계좌 상세 보기
	// 주소 : http://localhost/account/detail/1
	// 주소 : http://localhost/account/detail/1?type=deposit
	// 주소 : http://localhost/account/detail/1?type=withdraw
	// 기본값 세팅 가능
	@GetMapping("/detail/{accountId}")
	public String detail(@PathVariable Integer accountId,
			@RequestParam(name = "type", defaultValue = "all", required = false) String type,
			Model model) {
		// @PathVariable를 사용해서 해당 아이디 값만 조회 가능("/detail/{id}")
		
		// 인증 검사
		User principal = (User)session.getAttribute(Define.PRINCIPAL);
		
		
		// 상세 보기 화면 요청시 데이터를 내려주어야한다.
		// account 데이터, 접근주체, 거래내역 정보
		
		Account account = this.accountService.findById(accountId);
		
		List<History> historyList = this.accountService.readreadHistoryListAccount(type, accountId);
		
		model.addAttribute(Define.PRINCIPAL, principal);
		model.addAttribute("account", account);
		model.addAttribute("historyList", historyList);
		
		
		return "account/detail";
		
	}
	
	
	

}
