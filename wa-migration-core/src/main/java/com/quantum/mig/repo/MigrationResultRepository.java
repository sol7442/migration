package com.quantum.mig.repo;

import com.quantum.mig.entity.MigrationRecord;
import com.quantum.mig.entity.MigrationResult;

public interface MigrationResultRepository {
	public void record(MigrationRecord data);
	public void save(MigrationResult result);
}
