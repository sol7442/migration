package com.inzent.sh.aprv.service;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;

import com.inzent.sh.aprv.entity.AprvFile;
import com.inzent.sh.aprv.entity.AprvPerson;
import com.inzent.sh.aprv.repo.AprvSourceRepository;
import com.inzent.sh.print.entity.PrintFile;
import com.inzent.sh.print.entity.PrintFolder;
import com.inzent.sh.print.repo.PrintSourceRepository;
import com.quantum.mig.MigrationException;
import com.quantum.mig.entity.DefaultMigIdentity;
import com.quantum.mig.repo.RepositoryManager;
import com.quantum.mig.service.MigrationSourceService;

import lombok.extern.slf4j.Slf4j;
@Slf4j
public class AprvService extends MigrationSourceService {
	public List<AprvPerson> searchPersonsFromPrgrHist(AprvFile file) throws MigrationException{
		try(SqlSession session = RepositoryManager.getInstance().openSession("source")) {
			AprvSourceRepository repository = session.getMapper(AprvSourceRepository.class);
			return repository.searchPersonsFromPrgrHist(file);
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			throw new MigrationException(e.getMessage(),e);
		} 
	}
	public List<AprvPerson> searchPersonsFromHist(AprvFile file) throws MigrationException{
		try(SqlSession session = RepositoryManager.getInstance().openSession("source")) {
			AprvSourceRepository repository = session.getMapper(AprvSourceRepository.class);
			return repository.searchPersonsFromHist(file);
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			throw new MigrationException(e.getMessage(),e);
		} 
	}
	public List<AprvFile> searchFiles(Map<String,Object> params) throws MigrationException {
		try(SqlSession session = RepositoryManager.getInstance().openSession("source")) {
			AprvSourceRepository repository = session.getMapper(AprvSourceRepository.class);
			return repository.searchFiles(params);
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			throw new MigrationException(e.getMessage(),e);
		}
	}
}
