package com.inzent.sh.exception;

import com.quantum.mig.MigrationException;

import lombok.Data;
import lombok.EqualsAndHashCode;
@Data
@EqualsAndHashCode(callSuper = false)
public class DuplicatedFileException extends MigrationException {
	private static final long serialVersionUID = -1562157519513572754L;
	private String code;
	private String fullFilePath;
	private String message;
	public DuplicatedFileException(String code , String fullFilePath,String message ) {
		super(message);
		this.code = code; 
		this.fullFilePath= fullFilePath;
		this.message = message;
	}
	
}
