package com.quantum.mig.repo;

import java.util.List;
import java.util.Map;

public interface MigrationSourceRepository {
	public Map<String,Object> read(String id);
	
	public int count(Map<String,Object> params);
	public List<Map<String,Object>> search(Map<String,Object> params);
}
