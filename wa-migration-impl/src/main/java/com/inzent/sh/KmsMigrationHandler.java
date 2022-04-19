package com.inzent.sh;

import java.util.Map;

import com.quantum.mig.MigrationException;
import com.quantum.mig.MigrationHandler;
import com.quantum.mig.entity.MigrationRecord;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class KmsMigrationHandler implements MigrationHandler {

	@Override
	public MigrationRecord migration(Map<String, Object> data) throws MigrationException {
		MigrationRecord record = new MigrationRecord("mig-id");
		
		log.debug(" -- {}","test");
		
		return record;
	}

}
