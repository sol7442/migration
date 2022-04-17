package com.quantum.mig;

import java.util.Map;

import com.quantum.mig.entity.MigrationRecord;

public interface MigrationHandler {
	 public MigrationRecord migration(Map<String,Object> data) throws MigrationException;
}
