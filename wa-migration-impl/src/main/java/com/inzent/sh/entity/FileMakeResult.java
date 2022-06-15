package com.inzent.sh.entity;

import java.util.Map;

import com.inzent.xedrm.api.XeConnect;

import lombok.Data;

@Data
public class FileMakeResult {
	private String folderPath;
	private String errCode;
	private String docId;
	private String uploadUuid;
	private String errMsg;
	private String dupActionId;
	private String version;
	
	private boolean isReconnect;
	private XeConnect connection;
	
	private void init(Map<String,Object> data) {
		this.folderPath 	= String.valueOf(data.get("folderPath"));
		this.errCode 		= String.valueOf(data.get("errcode"));
		this.docId 			= String.valueOf(data.get("docId"));
		this.uploadUuid 	= String.valueOf(data.get("UPLOAD_UUID"));
		this.errMsg 		= String.valueOf(data.get("errmsg"));
		this.dupActionId 	= String.valueOf(data.get("dupActionId"));
		this.version 		= String.valueOf(data.get("version"));
	}
	/*
	 * 2022-06-13
	 * 파일 중복시 처리 업무 변경으로 인해 미사용
	 * */
	@Deprecated
	public FileMakeResult(XeConnect con , boolean isReconnect , Map<String,Object> data) {
		this.connection = con;
		this.isReconnect = isReconnect;
		this.init(data);
	}
	/**
	 * @param fileMakeResultMap 파일 생성 결과 
	 */
	public FileMakeResult(Map<String,Object> fileMakeResultMap) {
		this.init(fileMakeResultMap);
	}
}
