package com.quantum.mig;

import java.util.Map;

public interface MigrationHandler {
	 public void migration(Map<String,Object> conf) throws MigrationException;
//	 public MigrationAudit migration(Map<String,Object> data) throws MigrationException;

}
