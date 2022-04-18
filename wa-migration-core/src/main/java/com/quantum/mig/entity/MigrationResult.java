package com.quantum.mig.entity;

import lombok.Data;

@Data
public class MigrationResult {
	public String migClass; // KMS,APPV,PRINT
	public String migType;  // file. time, simul
	public String confPath; // 사용한 컨피그
	// 전체 데이터 수
	// 대상 데이터 수
	// 성공 데이터 수
	// 실패 데이터 수
}
