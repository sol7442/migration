package com.inzent.sh.kms.entity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Map;

import com.inzent.sh.entity.ShFile;
import com.quantum.mig.MigrationException;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
public class KmsCnFile implements ShFile {
	private final String EXT = ".txt"; 
	private String name;
	private String contents;
	private String register;
	private String path;
	private Date createDate;
	private Date modifyDate;
	public KmsCnFile(String name , String contents,String register ,Date createDate , Date modifyDate) {
		this.name = name;
		this.contents = contents;
		this.register = register;
		this.createDate = createDate;
		this.modifyDate = modifyDate;
	}
	
	public KmsCnFile(Map<String,Object> data) {
		this(String.valueOf(data.get("name"))
			, String.valueOf(data.get("DATA_CN"))
			, String.valueOf(data.get("CREATE_USER"))
			, (Date)data.get("CREATE_DATE")
			, (Date)data.get("UPDATE_DATE"));
	}
	/**
	 * @param path
	 * @throws MigrationException 
	 */
	public void makeFile(String path) throws MigrationException {
		this.path = path;
		File folder = new File(path);
		if(!folder.exists()) {
			folder.mkdir();
		}
		
		File file = new File(path+File.separator+name+EXT);
		
		try(FileOutputStream fos = new FileOutputStream(file)){
			fos.write(contents.getBytes());
		} catch (IOException e) {
			log.error(e.getMessage(),e);
			e.printStackTrace();
			throw new MigrationException(e.getMessage());
		} 
	}
	@Override
	public String getPath() {
		// TODO Auto-generated method stub
		return path+File.separator+name+EXT;
	}
	@Override
	public String getFileName() {
		// TODO Auto-generated method stub
		return name+EXT;
	}
	@Override
	public String getRegister() {
		// TODO Auto-generated method stub
		return register;
	}
	@Override
	public String getOwner() {
		// TODO Auto-generated method stub
		return register;
	}
	@Override
	public String getRegistDate() {
		// TODO Auto-generated method stub
		return FILE_DATE_FORMAT.format(createDate);
	}
	@Override
	public String getModifyDate() {
		// TODO Auto-generated method stub
		return FILE_DATE_FORMAT.format(modifyDate);
	}
}
