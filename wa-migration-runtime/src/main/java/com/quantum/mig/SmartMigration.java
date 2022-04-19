package com.quantum.mig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;

import com.inzent.sh.KmsMigrationHandler;
import com.quantum.mig.entity.MigrationRecord;
import com.quantum.mig.entity.MigrationResult;
import com.quantum.mig.repo.MigrationResultRepository;
import com.quantum.mig.repo.MigrationSourceRepository;
import com.quantum.mig.repo.RepositoryManager;

public class SmartMigration implements Runnable {
	//ExecutorService executor;
	Map<String, Object> conf;
	MigrationHandler handler = null;
	PrintStepHandler steper = null;
	public SmartMigration(Map<String, Object> conf) {
		this.conf = conf;
		//this.executor = Executors.newSingleThreadExecutor();;
		this.handler  = loadMigrationHandler(conf);
		this.steper   = loadStepPrinter(conf);//new ConsoleStepPrinter(10);
	}

	private PrintStepHandler loadStepPrinter(Map<String, Object> conf) {
		//TODO -- 컨피그를 통해서 정확하게 로딩 해라.
		//config > out.count 를 가지고온다. 
		return new ConsoleStepPrinter(10);
		
	}

	private MigrationHandler loadMigrationHandler(Map<String, Object> conf) throws MigrationException {
		try {
			return (MigrationHandler) Class.forName((String) conf.get("handler")).newInstance();
		} catch (ClassNotFoundException  | InstantiationException | IllegalAccessException e) {
			throw new MigrationException(e.getMessage(),e);
		}
	}

	public void start() {
		//this.executor.execute(this);
		run();
	}
	public void stop() {
		//this.executor.shutdown();
	}

	@Override
	public void run() {
		MigrationResult result = null;
		
		@SuppressWarnings("unchecked")
		Map<String,Object> condition = (Map<String,Object>)this.conf.get("condition");
		try {
			switch ((String)condition.get("type")) {
			case "time":
				result = migByTime(condition);
				break;
			case "file":
				result = migByFile();
				break;
			case "simul":
			default:
				result = migSimulate();
				break;
			}			
			
		}catch (Exception e) {
			saveErrorInfo();
		}finally {
			storeResult(result);
		}
	}

	private MigrationResult migSimulate() {
		// 돌아가는 거서 처럼 콘솔에 씀.
		
		return null;
	}

	private MigrationResult migByFile() throws MigrationException {
		MigrationResult result = new MigrationResult();
		result.migType = "FILE";
		
		MigrationSourceRepository src_repo = RepositoryManager.getInstance().getRepository("source",MigrationSourceRepository.class);
		MigrationResultRepository res_repo = RepositoryManager.getInstance().getRepository("result",MigrationResultRepository.class);
		
		List<String> dis = readIdsFile();
		for (String id : dis) {
			Map<String,Object> data = src_repo.read(id);
			
			MigrationRecord recode = handler.migration(data);
			steper.print(recode);
			res_repo.record(recode);
		}
		return result;
	}


	private List<String> readIdsFile() {
		return null;
	}

	private MigrationResult migByTime(Map<String,Object> condition) throws MigrationException {
		MigrationResult result = new MigrationResult();
		result.migType = "TIME";
		
		List<Map<String,Object>> data_list = null; //  src_repo.search(page,count,conf);
		int page = 0;
		int count = 0;
		int total_count = 1000;//src_repo.count(conf);
		int run_count = 0;
		//가져오는 부분 -time으로  - 내가 할거
		try(SqlSession src_session = RepositoryManager.getInstance().openSession("source")){
			
			//dao
			MigrationSourceRepository src_repo = src_session.getMapper(MigrationSourceRepository.class);
			//0,0 - paging , condition : duration -  yml 에서 가져옴
			total_count = src_repo.count(condition);
			
			data_list = src_repo.search(0, 0, condition);
			

		}catch (Exception e) {
			e.printStackTrace();
		}
		
		try(SqlSession res_session = RepositoryManager.getInstance().openSession("source")){
			res_session.getMapper(MigrationSourceRepository.class);
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		//공통파트 -  조회
		counting(data_list, total_count, run_count);
		
		//res_session

		
		//공통파트 - 결과 저장하기   -- 내가 할거
		return result;
	}

	private void counting(List<Map<String, Object>> data_list, int total_count, int run_count)
			throws MigrationException {
		while (run_count < total_count) {
			// 출력 가운트 만큼 조회 = 페이지 네이션 - 10단위로 될 만큼  -> yml 에서 가져와야 한다. 

			run_count++;
			
			for (Map<String, Object> data : data_list) {
				MigrationRecord recode = handler.migration(data);
				//res_repo.record(recode);
				
				steper.print(recode);
			}
		}
	}

	private void saveErrorInfo() {
		// TODO Auto-generated method stub
		// 에러가 난 정보에 대해여 파일로 정확하게 기록할 껏.
	}

	private void storeResult(MigrationResult result) {
		MigrationResultRepository res_repo = RepositoryManager.getInstance().getRepository("report",MigrationResultRepository.class);
		//res_repo.save(result);
	}
	
}
