package com.inzent.sh.kms.service;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;

import com.inzent.sh.kms.entity.KmsFile;
import com.inzent.sh.kms.entity.KmsFolder;
import com.inzent.sh.kms.repo.KmsSourceRepository;
import com.quantum.mig.MigrationException;
import com.quantum.mig.repo.RepositoryManager;
import com.quantum.mig.service.MigrationSourceService;

import lombok.extern.slf4j.Slf4j;
@Slf4j
public class KmsService extends MigrationSourceService {
	public List<KmsFile> searchFiles(Map<String,Object> params) throws MigrationException {
		try(SqlSession session = RepositoryManager.getInstance().openSession("source")){
			KmsSourceRepository repository = session.getMapper(KmsSourceRepository.class);
			return repository.searchFiles(params);
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			throw new MigrationException(e.getMessage(),e);
		}
	}
	
	public List<KmsFolder> searchFolders(Map<String,Object> params) throws MigrationException {
		try(SqlSession session = RepositoryManager.getInstance().openSession("source")) {
			KmsSourceRepository repository = session.getMapper(KmsSourceRepository.class);
			return repository.searchFolders(params);
		} catch(Exception e) {
			log.error(e.getMessage(),e);
			throw new MigrationException(e.getMessage(),e);
		}
	}
	
	public String searchFolderIds(Map<String,Object> params) throws MigrationException {
		try(SqlSession session = RepositoryManager.getInstance().openSession("source")) {
			KmsSourceRepository repository = session.getMapper(KmsSourceRepository.class);
			return repository.searchFolderIds(params);
		} catch(Exception e) {
			log.error(e.getMessage(),e);
			throw new MigrationException(e.getMessage(),e);
		}
	}
}
