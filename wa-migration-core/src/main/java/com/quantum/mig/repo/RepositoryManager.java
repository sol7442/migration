package com.quantum.mig.repo;

import java.util.Map;

import com.quantum.mig.MigrationException;

public class RepositoryManager {
	private static RepositoryManager instance;
	private RepositoryManager() {}
	public static RepositoryManager getInstance() {
		if(instance == null) {
			instance = new RepositoryManager();
		}
		return instance;
	}
	public void connect(Map<String, Object> conf) throws MigrationException {
		// TODO Auto-generated method stub
		
	}
	public <T> T getRepository(String string, Class<T> classOfT) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
