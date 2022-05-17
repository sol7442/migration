package com.inzent.sh.print.entity;

import java.util.Date;

import lombok.Data;

@Data
public class PrintFile {
	//OBJ_OBJECT_MST
	private int OBJ_SEQ;
	private String OBJ_NM;
	private String DELETE_YN;
	private String OWNER_USER_ID;
	
	//OBJ_FILE_LST - 등록자 / 수정자 정보 해당 테이블에서 사용?
	private int OBJ_FILE_SEQ;
	private String ORG_FILE_NM;
	private String FILE_PATH;
	private String FILE_NM;
	//FILE_ PREFIX
	private String FILE_REG_ID;
	private Date FILE_REG_DT;
	private String FILE_UPD_ID;
	private Date FILE_UPD_DT;
	
	//OBJ_FLD_REL
	private int FLD_SEQ;
	
	//USR_FOLDER_MST
	private String FLD_NM;
	private String PAR_FLD_SEQ;
	private String COMPANY_NAME;
	private String CHECK_YN;
	//FLD_ PREFIX
	private String FLD_REG_ID;
	private Date FLD_REG_DT;
	private String FLD_UPD_ID;
	private Date FLD_UPD_DT;
	////////////////////////
	private String FLD_PATH;
	/////////////////////////
	public String getFLD_PATH() {
		return FLD_PATH.replaceAll(">", "/");
	}
}
