package com.quantum.mig;

import com.quantum.mig.entity.MigrationRecord;

public interface PrintStepHandler {
	public void print(MigrationRecord record);
}
