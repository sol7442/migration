package com.inzent.sh.util;

public class YamlUtil {
	//yml date formt 20:01:01 -> 20-01-01 
	public static String convertTimeFormat(String time) {
		StringBuffer dateForm = new StringBuffer();
		
		String[] searchRequest = time.split(" ");  
		String dateStr = searchRequest[0].replaceAll(":", "-");
		dateForm.append(dateStr+" ");
		dateForm.append(searchRequest[1]);
		return dateForm.toString();
	}
}
