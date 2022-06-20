package com.inzent.sh.util;
/**
 * 폴더/파일명으로 사용할 수 없는 경우 처리
 * 특수문자 : / : * ? " < > | \ 
 * 문자열 : con , prn , aux , nul , com1~com9 , lpt1~lpt9
 * 폴더명이 .으로 끝나는 경우 
 * 모두 _ 치환 처리
 * 문자열은 뒤에 _ 붙히기
 * @param target
 * @return
 */
public class Validator {
	private final static String[] nonValidStrings = {"con","prn","aux","nul",
												 "com1","com2","com3","com4","com5","com6","com7","com8","com9",
												 "lpt1","lpt2","lpt3","lpt4","lpt5","lpt6","lpt7","lpt8","lpt9"};
	
	public static String convertValidStr(String target) {
		String result = target;
		for(String nonValidString : nonValidStrings) {
			if(result.equals(nonValidString)) {
				result = result+"_";
				return result;
			} 
		}
		
		if('.'==target.charAt(target.length()-1)) {
			result = target.substring(0,target.length()-1)+"_";
		}	
				
		
		result = result.replaceAll("[/\"[*]:<>|[?]\\\\]", "_");
		
		return result;
	}
	
	public static void main(String[] args) {
		String test1 = "abcd.";
		String test2 = "/:*?\"<>|\\";
		String test3 = "con";
		System.out.println(test1 + "->" +Validator.convertValidStr(test1));
		System.out.println(test2 + "->" +Validator.convertValidStr(test2));
		System.out.println(test3 + "->" +Validator.convertValidStr(test3));
	}
}
