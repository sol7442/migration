package com.inzent.sh.kms.entity;

import java.io.File;
import java.util.Date;

import com.inzent.sh.entity.ShFile;

import lombok.Data;

@Data
public class KmsFile implements ShFile{
	
	private String 	FILE_ID;
	private String 	KNOW_DATA_ID;
	private String 	CREATE_USER;
	private String 	CREATE_DEPT;
	private String 	PHY_SAVE_PATH_NM;
	private String 	FILE_NM;
	private Date 	CREATE_DATE;
	private Date 	UPDATE_DATE;
	private String	FLD_REG_DT;
	private String 	MAP_ID;
	private String 	USE_AT;
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
	@Override
	public String getRegister() {
		// TODO Auto-generated method stub
		return this.CREATE_USER;
	}
	@Override
	public String getOwner() {
		// TODO Auto-generated method stub
		return this.CREATE_USER;
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
