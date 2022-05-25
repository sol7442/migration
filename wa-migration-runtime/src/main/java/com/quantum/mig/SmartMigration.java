package com.quantum.mig;

import java.util.Map;

import com.quantum.mig.entity.MigrationResult;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SmartMigration implements Runnable {

	Map<String, Object> conf;
	MigrationHandler handler = null;
	PrintStepHandler steper = null;
	
	public SmartMigration(Map<String, Object> conf) throws MigrationException {
		this.conf = conf;
		//this.executor = Executors.newSingleThreadExecutor();;
		this.handler  = loadMigrationHandler(conf);
	}


	private MigrationHandler loadMigrationHandler(Map<String, Object> conf) throws MigrationException {
		try {
			//핸들러 메소드 호출
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
		try {
			handler.migration(conf);
		} catch (MigrationException e) {
			// TODO Auto-generated catch block
			log.error(e.getMessage(),e);
			saveErrorInfo();
		}
	}


	
	
	public MigrationResult setResult(MigrationResult result) {
		return result;
	}


	private void saveErrorInfo() {
		// TODO Auto-generated method stub
		// 에러가 난 정보에 대해여 파일로 정확하게 기록할 껏.
	}

}
