<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%@ include file="/WEB-INF/view/layout/header.jsp"%>

	<div class="col-sm-8">
      <h2>입금 페이지(인증)</h2>
      <h5>어서오세요.</h5>
      <div class="bg-light p-md-5 h-75" >
      <div>
    	<form action="/account/deposit" method="post">
    	
    		<div class="form-group"> 		
    			<label for="amount">입금 금액:</label>
    			<input type="text" class="form-control"
    				placeholder="Enter amount" id="amount" name="amount" value="100">		
  			</div>
  			
  			<div class="form-group">
   			 	<label for="dAccountNumber">입금 계좌 번호:</label>
    			<input type="text" class="form-control"
    				placeholder="Enter dAccountNumber" id="dAccountNumber" name="dAccountNumber" value="1111">
  			</div>
  			
  			
  			
  			<button type="submit" class="btn btn-primary">입금 요청</button>
    		
    	</form>
    
      </div>
      	
      </div>

    </div>


<%@ include file="/WEB-INF/view/layout/footer.jsp"%>