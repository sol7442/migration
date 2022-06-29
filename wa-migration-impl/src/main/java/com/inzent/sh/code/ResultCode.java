package com.inzent.sh.code;

public enum ResultCode {
	CREATE("0","CREATE","SUCCESS"),
	IGNORE("1","IGNORE","IGNORE"),
	ERROR("-1","ERROR","ERROR");
	
	ResultCode(String code, String action, String msg) {
		this.code = code;
		this.action = action;
		this.msg = msg;
	}
	private String code;
	private String action;
	private String msg;
	
	public String getCode() {
		return code;
	}
	public String getAction() {
		return action;
	}
	public String getMsg() {
		return msg;
	}
}
