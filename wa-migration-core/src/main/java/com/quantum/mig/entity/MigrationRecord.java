package com.quantum.mig.entity;

import java.util.Date;

import lombok.Data;

@Data
public class MigrationRecord {
	public final String srcId;
	
	public boolean result;
	public String tagId;
	public String action; // create, update, ignore, error
	public Date   time;
	public String msg;
	
	public MigrationRecord(String id) {
		this.srcId = id;
	}
}
