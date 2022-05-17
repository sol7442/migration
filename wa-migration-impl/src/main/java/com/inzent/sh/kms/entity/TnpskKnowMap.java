package com.inzent.sh.kms.entity;

import lombok.Data;

@Data
public class TnpskKnowMap {
	/**
	 * KEY
	 */
	private String MAP_ID;
	private String MAP_NM;
	/**
	 * ECM의 RANK로 등록
	 */
	private String SEQNO;
	/**
	 *  USE_AT=Y 인 MAP 중 MAP_ID = '000000001' 인 지식자료는 지식자료실의 최상위 폴더이므로 UP_MAP_ID null임. 
	 *  생성시 ELEMENTID = '20220418103418ju'를 상위 폴더로 지정하여 처리 요망.
	 */
	private String UP_MAP_ID;	
	/**
	 * Y 인 폴더만 등록, 단 상위 MAP_ID의 USE_AT가 N 인 경우가 존재하므로 상위 MAP_ID의 사용여부도 CHECK 하여야 함.
	 */
	private String USE_AT;		 
	/**
	 * 부서에 대하여 쓰기-삭제 포함 권한 부여
	 */
	private String CREATE_DEPT;	
	private String CREATE_USER;
	/**
	 * 폴더 생성 시 생성일 Parameter의 입력 값으로 사용
	 */
	private String CREATE_DATE;	
}
