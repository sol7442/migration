package com.quantum.mig.repo;

import java.util.Map;

public interface MigrationTargetRepository {
	public Map<String,Object> read(String id);
	
	public int count(Map<String,Object> params);
	public void create(Map<String,Object> params);
	public Map<String,Object> modify(Map<String,Object> params);
}
