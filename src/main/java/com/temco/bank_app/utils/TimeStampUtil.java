package com.temco.bank_app.utils;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

public class TimeStampUtil {

	// 상태값을 가지는 변수를 사용하면 안된다.
	public static String timestampToString(Timestamp createdAt) {
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
		
		return sdf.format(createdAt);
	}
	
	
	
}
