package com.quantum.mig;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.quantum.mig.MigrationException;
import com.quantum.mig.MigrationHandler;
import com.quantum.mig.entity.MigrationRecord;
import com.quantum.mig.entity.MigrationResult;
import com.quantum.mig.repo.MigrationResultRepository;
import com.quantum.mig.repo.MigrationSourceRepository;
import com.quantum.mig.repo.RepositoryManager;

public class SmartMigration implements Runnable {
	ExecutorService executor;
	Map<String, Object> conf;
	MigrationHandler handler = null;
	public SmartMigration(Map<String, Object> conf) {
		this.conf = conf;
		this.executor = Executors.newSingleThreadExecutor();;
		this.handler = null; // load
	}

	public void start() {
		this.executor.execute(this);
	}
	public void stop() {
		this.executor.shutdown();
	}

	@Override
	public void run() {
		MigrationResult result = null;
		try {
			switch ((String)this.conf.get("type")) {
			case "time":
				result = migByTime();
				break;
			case "file":
				result = migByFile();
				break;
			default:
				break;
			}			
			
		}catch (Exception e) {
			saveErrorInfo();
		}finally {
			storeResult(result);
		}
	}

	private MigrationResult migByFile() throws MigrationException {
		MigrationResult result = new MigrationResult();
		result.migType = "FILE";
		
		MigrationSourceRepository src_repo = RepositoryManager.getInstance().getRepository("key",MigrationSourceRepository.class);
		MigrationResultRepository res_repo = RepositoryManager.getInstance().getRepository("key",MigrationResultRepository.class);
		
		List<String> dis = readIdsFile();
		for (String id : dis) {
			Map<String,Object> data = src_repo.read(id);
			
			MigrationRecord recode = handler.migration(data);
			res_repo.record(recode);
		}
		return result;
	}


	private List<String> readIdsFile() {
		return null;
	}

	private MigrationResult migByTime() throws MigrationException {
		MigrationResult result = new MigrationResult();
		result.migType = "TIME";
		
		MigrationSourceRepository src_repo = RepositoryManager.getInstance().getRepository("key",MigrationSourceRepository.class);
		MigrationResultRepository res_repo = RepositoryManager.getInstance().getRepository("key",MigrationResultRepository.class);
		
		src_repo.count(conf);
		List<Map<String,Object>> data_list =  src_repo.search(conf);
		for (Map<String, Object> data : data_list) {
			MigrationRecord recode = handler.migration(data);
			res_repo.record(recode);
		}
		
		return result;
	}

	private void saveErrorInfo() {
		// TODO Auto-generated method stub
		// save error info file...
	}

	private void storeResult(MigrationResult result) {
		MigrationResultRepository res_repo = RepositoryManager.getInstance().getRepository("key",MigrationResultRepository.class);
		res_repo.save(result);
	}
	
}
