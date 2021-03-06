package com.quantum.mig.service;

import org.apache.ibatis.session.SqlSession;

import com.quantum.mig.MigrationException;
import com.quantum.mig.entity.MigrationResult;
import com.quantum.mig.repo.MigrationResultRepository;
import com.quantum.mig.repo.RepositoryManager;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MigrationResultService implements MigrationResultRepository{

	@Override
	public void record(MigrationResult data) throws MigrationException {
		try (SqlSession src_session = RepositoryManager.getInstance().openSession("result")) {
			MigrationResultService result_repo = src_session.getMapper(MigrationResultService.class);
			result_repo.record(data);
		}catch (Exception e) {
			throw new MigrationException(e.getMessage(),e);
		}		
	}

	@Override
	public void save(MigrationResult result) throws MigrationException  {
		
	}

}
