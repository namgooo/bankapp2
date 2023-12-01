package com.temco.bank_app.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.temco.bank_app.dto.DepositFormDto;
import com.temco.bank_app.dto.SaveFormDto;
import com.temco.bank_app.dto.TransferFormDto;
import com.temco.bank_app.dto.WithdrawFormDto;
import com.temco.bank_app.handler.exception.CustomRestfullException;
import com.temco.bank_app.repository.entity.Account;
import com.temco.bank_app.repository.entity.History;
import com.temco.bank_app.repository.interfaces.AccountRepository;
import com.temco.bank_app.repository.interfaces.HistoryRepository;

@Service // IOC 대상 + 싱글톤 관리
public class AccountService {

	@Autowired
	private AccountRepository accountRepository;
	
	@Autowired
	private HistoryRepository historyRepository;
	
	
	
	@Transactional
	public void createAccount(SaveFormDto dto, Integer principalId) {
		
		
		// 계좌 중복 여부 확인
		
		Account account = Account.builder()
				.number(dto.getNumber())
				.password(dto.getPassword())
				.balance(dto.getBalance())
				.userId(principalId)
				.build();
		
		
		int resultRowCount = accountRepository.insert(account);
		
		if(resultRowCount != 1) {
			throw new CustomRestfullException("계좌 생성 실패",
					HttpStatus.INTERNAL_SERVER_ERROR);
			
		}
		
	}
	
	// 계좌 목록 보기 기능
	public List<Account> readAccountList(Integer userId){
		
		List<Account> list = accountRepository.findByUserId(userId);
		
		return list;
	}
	
	
	// 출금 기능 로직 고민해보기
	// 1. 계좌 존재 여부 확인 -> select
	// 2. 본인 계좌 여부 확인 -> select
	// 3. 계좌 비밀번호 확인
	// 4. 잔액 여부 확인
	// 5. 출금 처리 -> update
	// 6. 거래 내역 등록 -> insert
	// 7. 트랜잭션 처리
	@Transactional // 롤백시킴
	public void updateAccountWithdraw(WithdrawFormDto dto, Integer principalId) {
		
		Account accountEntity = accountRepository.findByNumber(dto.getWAccountNumber());
		
		// 1
		if(accountEntity == null) {
			throw new CustomRestfullException("해당 계좌가 없음.", HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		// 2
		if(accountEntity.getUserId() != principalId) {
			throw new CustomRestfullException("본인 소유 계좌가 아님.", HttpStatus.UNAUTHORIZED);
		}
		
		// 3
		if(accountEntity.getPassword().equals(dto.getPassword()) == false) {
			throw new CustomRestfullException("출금 계좌 비밀번호가 틀렸음.", HttpStatus.BAD_REQUEST);
		}
		
		// 4
		if(accountEntity.getBalance() <= dto.getAmount()) {
			throw new CustomRestfullException("계좌 잔액이 부족함.", HttpStatus.BAD_REQUEST);
		}
		
		// 5 객체 모델 상태값 변경 처리
		accountEntity.withdraw(dto.getAmount()); // 출금 코드
		
		accountRepository.updateById(accountEntity); // 업데이트 코드
		
		// 6 거래 내역 등록
		History history = new History(); 
		
		history.setAmount(dto.getAmount()); // history에 총액 넣기
		
		// 출금 거래 내역에서는 사용자가 출금 후에 잔액을 입력합니다.
		history.setWBalance(accountEntity.getBalance()); 
		history.setDBalance(null); 
		history.setWAccountId(accountEntity.getId());
		history.setDAccountId(null);
		history.setWAccountId(accountEntity.getId());
		
		int resultRowCount = historyRepository.insert(history);
		
		if(resultRowCount != 1) {
			throw new CustomRestfullException("정상 처리 되지 않았음.", HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
	}
	
	// 입금 기능
	@Transactional
	public void updateAccountDeposit(DepositFormDto dto, Integer principalId) {
		
		Account accountEntity = accountRepository.findByNumber(dto.getDAccountNumber());
		
		// 유효성 검사
		if(accountEntity == null) {
			throw new CustomRestfullException("해당 계좌가 없음.", HttpStatus.INTERNAL_SERVER_ERROR);
		}
		if(accountEntity.getUserId() != principalId) {
			throw new CustomRestfullException("본인 소유 계좌가 아님.", HttpStatus.UNAUTHORIZED);
		}
		
		if(accountEntity.getBalance() <= dto.getAmount()) {
			throw new CustomRestfullException("계좌 잔액이 부족함.", HttpStatus.BAD_REQUEST);
		}
		
		// 객체 모델 상태값 변경 처리
		accountEntity.deposit(dto.getAmount());
		
		accountRepository.updateById(accountEntity); // 업데이트 코드
		
		// 거래 내역 등록
		History history = new History(); 
		
		history.setAmount(dto.getAmount()); // history에 금액 넣기
		
		// 출금 거래 내역에서는 사용자가 출금 후에 잔액을 입력합니다.
		history.setDBalance(accountEntity.getBalance()); 
		history.setWBalance(null); // null이 담겨야 입금인걸 알 수 있음
		history.setDAccountId(accountEntity.getId());
		history.setWAccountId(null);
		history.setDAccountId(accountEntity.getId());
		
		// insert 시키기
		int resultRowCount = historyRepository.insert(history);
		
		if(resultRowCount != 1) {
			throw new CustomRestfullException("정상 처리 되지 않았음.", HttpStatus.INTERNAL_SERVER_ERROR);
		}
			
	}
	
	
	// 이체 기능
	@Transactional
	public void updateAccountTransfer(TransferFormDto dto, Integer principalId) {
		
		// 1. 
		Account withdrawAccountEntity = accountRepository.findByNumber(dto.getWAccountNumber());
		if(withdrawAccountEntity == null) {
			throw new CustomRestfullException("출금 계좌가 없음.", HttpStatus.INTERNAL_SERVER_ERROR);
		}
		// 2. 
		Account depositAccountEntity = accountRepository.findByNumber(dto.getDAccountNumber());
		if(depositAccountEntity == null) {
			throw new CustomRestfullException("입금 계좌가 없음.", HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		// 3.
		withdrawAccountEntity.checkOwner(principalId);
		
		// 4.
		withdrawAccountEntity.checkPassword(dto.getPassword());
		
		// 5.
		withdrawAccountEntity.checkBalance(dto.getAmount());
		
		// 6. 객체 상태 변경
		withdrawAccountEntity.withdraw(dto.getAmount());
		
		// 업데이트 처리
		this.accountRepository.updateById(depositAccountEntity);
		
		// 입금 처리
		depositAccountEntity.deposit(dto.getAmount());
		accountRepository.updateById(depositAccountEntity);
		
		History history = new History(); 
		
		history.setAmount(dto.getAmount()); // history에 총액 넣기
		
		// 출금 거래 내역에서는 사용자가 출금 후에 잔액을 입력합니다.
		history.setWBalance(withdrawAccountEntity.getBalance());
		history.setDBalance(depositAccountEntity.getBalance());
		history.setWAccountId(withdrawAccountEntity.getId());
		history.setDAccountId(depositAccountEntity.getId());
		
		int resultRowCount = historyRepository.insert(history);
		
		if(resultRowCount != 1) {
			throw new CustomRestfullException("정상 처리 되지 않았음.", HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
	}
	
	
	public Account findById(Integer accountId) {
		
		Account accountEntity = this.accountRepository.findById(accountId);
		
		if(accountEntity == null) {
			throw new CustomRestfullException("해당 계좌를 찾을 수 없음",
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		return accountEntity;
		
	}
	
	// @param type = [all, deposit, withdraw]
	// @param accountId
	// @return 입금내역, 출금내역, 입출금 내역(3가지 타입)
	// 단일 계좌 조회
	public List<History> readreadHistoryListAccount(String type, Integer accountId){
		
		List<History> historyEntity = this.historyRepository.findByIdAndDynamicType(type, accountId);
		
		return historyEntity;
		
	}

	
	
}
