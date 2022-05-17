package com.quantum.mig.service;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;

import com.quantum.mig.MigrationException;
import com.quantum.mig.entity.MigrationAudit;
import com.quantum.mig.entity.MigrationSource;
import com.quantum.mig.repo.MigrationSourceRepository;
import com.quantum.mig.repo.RepositoryManager;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MigrationSourceService implements MigrationSourceRepository{

	@Override
	public int size() throws MigrationException {
		try (SqlSession src_session = RepositoryManager.getInstance().openSession("source")) {
			MigrationSourceRepository src_repo = src_session.getMapper(MigrationSourceRepository.class);
			return src_repo.size();
		}catch (Exception e) {
			throw new MigrationException(e.getMessage(),e);
		}
	}

	@Override
	public List<MigrationSource> search(int page, int count, Map<String, Object> params) throws MigrationException {
		try (SqlSession src_session = RepositoryManager.getInstance().openSession("source")) {
			MigrationSourceRepository src_repo = src_session.getMapper(MigrationSourceRepository.class);
			log.info("filter : {} , {} " , params.get("stime") , params.get("etime"));
			return src_repo.search(page, count, params);
		}catch (Exception e) {
			throw new MigrationException(e.getMessage(),e);
		}
	}

	@Override
	public Map<String, Object> read(String id) throws MigrationException  {
		try (SqlSession src_session = RepositoryManager.getInstance().openSession("source")) {
			MigrationSourceRepository src_repo = src_session.getMapper(MigrationSourceRepository.class);
			return src_repo.read(id);
		}catch (Exception e) {
			throw new MigrationException(e.getMessage(),e);
		}
	}

	@Override
	public void record(MigrationAudit data) throws MigrationException  {
		// TODO Auto-generated method stub
		
	}

}
