package com.quantum.mig.repo;

import java.util.List;
import java.util.Map;

import com.quantum.mig.MigrationException;
import com.quantum.mig.entity.MigrationAudit;
import com.quantum.mig.entity.MigrationSource;

//소스
public interface MigrationSourceRepository {
//	public int size(Map<String,Object> params);
	public int size() throws MigrationException;
	public List<MigrationSource> search(int page, int count, Map<String,Object> params) throws MigrationException;
	public Map<String,Object> read(String id) throws MigrationException;
	public void record(MigrationAudit data) throws MigrationException;
}
