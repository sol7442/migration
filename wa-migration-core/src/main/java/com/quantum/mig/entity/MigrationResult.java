package com.quantum.mig.entity;

import lombok.Data;

@Data
public class MigrationResult {
	
	//결과는 나중에 재민프로님이 보내주면 내가 자세하게 바꿈 - db에 저장 / mybatis
	public String migClass; 		// KMS,APPV,PRINT
	public String migType;  		// file. time, simul
	public String confPath; 		// 사용한 컨피그
	public String totalCnt;			// 전체 데이터 수
	public String targetCnt;		// 대상 데이터 수
	public String successCnt;		// 성공 데이터 수
	public String failCnt;			// 실패 데이터 수

}
