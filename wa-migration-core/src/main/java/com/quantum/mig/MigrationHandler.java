package com.quantum.mig;

import com.quantum.mig.entity.MigrationAudit;
import com.quantum.mig.entity.MigrationSource;

public interface MigrationHandler {
	 public MigrationAudit migration(MigrationSource data) throws MigrationException;
//	 public MigrationAudit migration(Map<String,Object> data) throws MigrationException;
}
