package com.inzent.sh.aprv.entity;

import lombok.Data;

@Data
public class AprvPerson {
	private String APRV_SEQ;
	private String DECIDER_EMP_NO;
	private String CREATE_USER;
	private String PUB_INSPT_PRSN_EMP_NO;
	
	public String getDECIDER_EMP_NO() {
		return this.getPersonId(DECIDER_EMP_NO, null);
	}
	
	public String getCREATE_USER() {
		return this.getPersonId(CREATE_USER, CREATE_USER);
	}
	
	public String getPUB_INSPT_PRSN_EMP_NO() {
		return this.getPersonId(PUB_INSPT_PRSN_EMP_NO, null);
	}
	
	private String getPersonId(String id,String defaultValue) {
		return id.length()==5?id.substring(1):defaultValue;
	}
}
