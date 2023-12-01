package com.temco.bank_app.repository.interfaces;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.temco.bank_app.repository.entity.History;

@Mapper
public interface HistoryRepository {
	
	
	// 거래내역 등록
	public int insert(History history);
	
	// 수정
	public int updateById(History history);
	
	// 삭제
	public int deleteById(Integer id);
	
	// 전체 조회
	public List<History> findAll();
	
	// 상세 조회
	public List<History> findByIdAndDynamicType
			(@Param("type") String type, @Param("accountId") Integer accountId);
	
	// 거래내역 조회
	// public List<History> findByAccountNumber(String id);
	
	// 동적 쿼리 생성
	// 입금 / 출금 / 전체
	// 반드시 두개 이상에 파라미터 사용시 @Param 를 사용해야 한다.
//	public List<History> findByIdAndDynamicType(@Param("type") String type, @Param("id") Integer id);
	
	

}
