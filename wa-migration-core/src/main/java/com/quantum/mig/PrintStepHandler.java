package com.quantum.mig;

import com.quantum.mig.entity.MigrationAudit;

public interface PrintStepHandler {
	public void print(MigrationAudit record);
}
