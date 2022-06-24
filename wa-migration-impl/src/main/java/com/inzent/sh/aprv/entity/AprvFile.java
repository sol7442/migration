package com.inzent.sh.aprv.entity;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.inzent.sh.entity.ShFile;

import lombok.Data;

@Data
public class AprvFile implements ShFile{
	SimpleDateFormat format = new SimpleDateFormat("YYYY-MM-dd");
	private int APRV_SEQ;
	private String FILE_ID;
	private String CREATE_USER;
	private Date CREATE_DATE;
	private Date UPDATE_DATE;
	private String PHY_SAVE_PATH_NM;
	private String FILE_NM;
	private String CREATE_USER_HIST;
	
	
	public String getYear() {
		return format.format(CREATE_DATE).substring(0, 4);
	}
	
	public String getMonthDay() {
		String formatString = format.format(CREATE_DATE);
		StringBuffer sb = new StringBuffer();
		sb.append(formatString.substring(5, 7));
		sb.append(formatString.substring(8));
		return sb.toString();
	}

	private String getCREATE_USER() {
		String createUser = this.CREATE_USER==null?this.CREATE_USER_HIST:this.CREATE_USER;
		if(createUser.length() == 5) {
			return createUser.substring(1);
		}
		return createUser;
	}
	@Override
	public String getPath() {
		// TODO Auto-generated method stub
		return this.PHY_SAVE_PATH_NM+File.separator+this.FILE_ID;
	}

	@Override
	public String getFileName() {
		// TODO Auto-generated method stub
		return this.FILE_NM;
	}
	/**
	 * 겸직자의 경우 같은 USER_ID를 부서에 따라 다르게 사용하는 경우가 있음.
	 * Ex) A1234 , B1234 , C1234, 1234
	 * 5자리 문자열인 경우 뒤 4자리만 사용, 나머지 경우 원래값 사용.
	 * CREATE_USER가 NULL인 경우 CREATE_USER_HIST(TNZZE_FORM_APRV_HIST)의 값 사용.
	 */
	@Override
	public String getRegister() {
		// TODO Auto-generated method stub
		return this.getCREATE_USER();
	}

	@Override
	public String getOwner() {
		// TODO Auto-generated method stub
		return this.getCREATE_USER();
	}

	@Override
	public String getRegistDate() {
		// TODO Auto-generated method stub
		return FILE_DATE_FORMAT.format(CREATE_DATE);
	}

	@Override
	public String getModifyDate() {
		// TODO Auto-generated method stub
		return FILE_DATE_FORMAT.format(UPDATE_DATE);
	}
}
