package com.quantum.mig.entity;

import lombok.Data;

@Data
public class MigrationAudit {
	public final String SRC_ID;	//원본데이터 id
	
	public String  RESULT;	// 성공/실패 -> 코드 값으로 변경 0 이외의 코드는 실패
	public String  TAG_ID;	// 타겟 id (같을수도, 틀릴수도 있음)
	public String  ACTION; // create, update, ignore, error
	public String  TIME;
	public String  MSG;
	public String  SEQ_VALUE;
	public MigrationAudit(String id) {
		this.SRC_ID = id;
	}
}
