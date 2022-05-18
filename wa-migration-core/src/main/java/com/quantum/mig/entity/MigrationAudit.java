package com.quantum.mig.entity;

import lombok.Data;

@Data
public class MigrationAudit {
	public final String srcId;	//원본데이터 id
	
	public String  result;	// 성공/실패 -> 코드 값으로 변경 0 이외의 코드는 실패
	public String  tagId;	// 타겟 id (같을수도, 틀릴수도 있음)
	public String  action; // create, update, ignore, error
	public String  time;
	public String  msg;
	
	public MigrationAudit(String id) {
		this.srcId = id;
	}
}
