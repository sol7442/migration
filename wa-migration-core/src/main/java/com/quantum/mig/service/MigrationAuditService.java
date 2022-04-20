package com.quantum.mig.service;

import org.apache.ibatis.session.SqlSession;

import com.quantum.mig.MigrationException;
import com.quantum.mig.entity.MigrationAudit;
import com.quantum.mig.repo.MigrationAuditRepository;
import com.quantum.mig.repo.RepositoryManager;

public class MigrationAuditService implements MigrationAuditRepository{

	//이력 테이블 
	@Override
	public void record(MigrationAudit data) throws MigrationException  {
		try (SqlSession src_session = RepositoryManager.getInstance().openSession("audit")) {
			MigrationAuditRepository audit_repo = src_session.getMapper(MigrationAuditRepository.class);
			audit_repo.record(data);
		}catch (Exception e) {
			throw new MigrationException(e.getMessage(),e);
		}		
	}

}
