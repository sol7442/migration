package com.inzent.sh.exception;

import com.quantum.mig.MigrationException;
/**
 * 해당 폴더의 상위폴더들 중에 하나라도 존재하지 않을 때 발생
 */

import lombok.Data;
import lombok.EqualsAndHashCode;
@Data
@EqualsAndHashCode(callSuper = false)
public class OmittedFolderException extends MigrationException {
	private static final long serialVersionUID = -1562157519513572754L;
	private int pathSize;	//컬럼에 String으로 저장된 path depth
	private int folderInfoSize; //계층쿼리등을 통해 조회된 관계상의 path depth
	private String message;
	public OmittedFolderException(int pathSize , int folderInfoSize,String message ) {
		super(message);
		this.pathSize = pathSize;
		this.folderInfoSize = folderInfoSize;
		this.message = message;
	}
}
