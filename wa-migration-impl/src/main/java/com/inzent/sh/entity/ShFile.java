package com.inzent.sh.entity;

import java.text.SimpleDateFormat;

public interface ShFile {
	public SimpleDateFormat FILE_DATE_FORMAT = new SimpleDateFormat("YYYYMMddHHmmssSSS");
	public String getPath();	 	//경로
	public String getFileName(); 	//파일명
	public String getRegister(); 	//등록자
	public String getOwner();	 	//소유자
	public String getRegistDate();	//등록일
	public String getModifyDate();	//수정일
}
