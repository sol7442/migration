package com.inzent.sh.print.service;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;

import com.inzent.sh.print.entity.PrintFile;
import com.inzent.sh.print.entity.PrintFolder;
import com.inzent.sh.print.repo.PrintSourceRepository;
import com.quantum.mig.MigrationException;
import com.quantum.mig.entity.DefaultMigIdentity;
import com.quantum.mig.repo.RepositoryManager;
import com.quantum.mig.service.MigrationSourceService;

import lombok.extern.slf4j.Slf4j;
@Slf4j
public class PrintService extends MigrationSourceService {
	public int folderCount() throws MigrationException {
		try(SqlSession session = RepositoryManager.getInstance().openSession("source")) {
			PrintSourceRepository repository = session.getMapper(PrintSourceRepository.class);
			return repository.folderCount();
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			throw new MigrationException(e.getMessage(),e);
		} 
	}	
	public int fileCount() throws MigrationException {
		try(SqlSession session = RepositoryManager.getInstance().openSession("source")) {
			PrintSourceRepository repository = session.getMapper(PrintSourceRepository.class);
			return repository.fileCount();
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			throw new MigrationException(e.getMessage(),e);
		} 
	}	
	public PrintFolder readFolder(DefaultMigIdentity id) throws MigrationException{
		try(SqlSession session = RepositoryManager.getInstance().openSession("source")) {
			PrintSourceRepository repository = session.getMapper(PrintSourceRepository.class);
			return repository.readFolder(id);
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			throw new MigrationException(e.getMessage(),e);
		} 
	}
	public PrintFile readFile(DefaultMigIdentity id) throws MigrationException {
		try(SqlSession session = RepositoryManager.getInstance().openSession("source")) {
			PrintSourceRepository repository = session.getMapper(PrintSourceRepository.class);
			return repository.readFile(id);
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			throw new MigrationException(e.getMessage(),e);
		} 
	}
	
	public List<PrintFolder> searchFolders(Map<String,Object> params) throws MigrationException{
		try(SqlSession session = RepositoryManager.getInstance().openSession("source")) {
			PrintSourceRepository repository = session.getMapper(PrintSourceRepository.class);
			return repository.searchFolders(params);
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			throw new MigrationException(e.getMessage(),e);
		} 
	};
	public List<PrintFile> searchFiles(Map<String,Object> params) throws MigrationException {
		try(SqlSession session = RepositoryManager.getInstance().openSession("source")) {
			PrintSourceRepository repository = session.getMapper(PrintSourceRepository.class);
			return repository.searchFiles(params);
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			throw new MigrationException(e.getMessage(),e);
		}
	}
}
