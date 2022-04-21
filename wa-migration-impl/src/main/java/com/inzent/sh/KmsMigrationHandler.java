package com.inzent.sh;

import com.quantum.mig.MigrationException;
import com.quantum.mig.MigrationHandler;
import com.quantum.mig.entity.MigrationAudit;
import com.quantum.mig.entity.MigrationSource;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class KmsMigrationHandler implements MigrationHandler {

	@Override
	public MigrationAudit migration(MigrationSource data) throws MigrationException {
		MigrationAudit record = new MigrationAudit("mig-id");
		log.debug(" -- {}",record.getSrcId());
		
		return record;
	}

}
