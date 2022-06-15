package com.inzent.sh.print.entity;

import java.util.Date;

import lombok.Data;
@Data
public class PrintFolder {
	private int FLD_SEQ;
	private String FLD_NM;
	private String FLD_DESC;
	private String FLD_ID;
	private int PAR_FLD_SEQ;
	private int CLS_SEQ;
	private String PROJECT_START_DT;
	private String PROJECT_END_DT;
	private String PROJECT_CODE;
	private String PROJECT_NAME;
	private String COMPANY_NAME;
	private String INPUT_USER;
	private String INPUT_USER_NUMBER;
	private String CHECK_YN;
	private String CHECK_HISTORY_YN;
	private String CHECK_USER_ID;
	private int DSIP_SEQ;
	private int DEPTH;
	private String UPD_ID;
	private Date UPT_DT;
	private String REG_ID;
	private Date REG_DT;
	private String FLD_ADMIN_USER_ID;
	private String FLD_PATH;
	private String CNTRC_NO;
	private String CNTRC_DT;
	/**
	 * 부모 SEQ가 56400,79497 , 79574 , 82358 , 82359 , 82360 일 경우 
	 * 확장속성 부여
	 * @return
	 */
	public boolean isAddAttrValue() {
		switch (this.PAR_FLD_SEQ) {
		case 56400:
			return true;
		case 79497:
			return true;
		case 79574:
			return true;				
		case 82358:
			return true;					
		case 82359:
			return true;
		case 82360:
			return true;
		default:
			return false;
		}
	}
}
