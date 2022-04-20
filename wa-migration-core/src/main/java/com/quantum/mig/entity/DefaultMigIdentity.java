package com.quantum.mig.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DefaultMigIdentity {
	String id;
	
	public String toStr() {
		return id;
	}
	public void fromStr(String value) {
		id = value;
	}
	
}
