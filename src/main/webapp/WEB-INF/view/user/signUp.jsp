<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%@ include file="/WEB-INF/view/layout/header.jsp"%>

    <div class="col-sm-8">
      <h2>회원가입</h2>
      <h5>어서오세요 여기는 후미진 어느 언덕입니다.</h5>
      
      <div>
    	<form action="/user/sign-up" method="post" enctype="multipart/form-data">
    	
    		<div class="form-group"> 		
    			<label for="username">username:</label>
    			<input type="text" class="form-control"
    				placeholder="Enter username" id="username" name="username">		
  			</div>
  			
  			<div class="form-group">
   			 	<label for="pwd">Password:</label>
    			<input type="password" class="form-control"
    				placeholder="Enter password" id="pwd" name="password">
  			</div>
  			
  			 <div class="form-group">
   			 	<label for="fullname">fullname:</label>
    			<input type="text" class="form-control"
    				placeholder="Enter fullname" id="fullname" name="fullname">
  			</div>
  			
  			<!-- 파일 업로드 -->
  			<div class="custom-file">
    			<input type="file" class="custom-file-input" id="file" name="file">
    			<label class="custom-file-label" for="file">Choose file</label>
  			</div>
  			
  			<button type="submit" class="btn btn-primary">회원가입</button>
    		
    	</form>
    
      </div>
     
    </div>
    
	<script>
		$(".custom-file-input").on("change", function() {
		  var fileName = $(this).val().split("\\").pop();
		  $(this).siblings(".custom-file-label").addClass("selected").html(fileName);
		});
	</script>


<%@ include file="/WEB-INF/view/layout/footer.jsp"%>