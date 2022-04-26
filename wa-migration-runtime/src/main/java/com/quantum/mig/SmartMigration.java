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
//		this.steper   = loadStepPrinter(conf);//new ConsoleStepPrinter(10);
	}

	//total 값과 함께 넘기기 위해 total 값 조회하는 함수에서 호출해야함
	private PrintStepHandler loadStepPrinter(Map<String, Object> conf) {
		//total count는 conf 에서 구할수있는 값이 아님.
		return new ConsoleStepPrinter(100, (int)conf.get("out.count"));
	}

	private MigrationHandler loadMigrationHandler(Map<String, Object> conf) throws MigrationException {
		try {
			//핸들러 메소드 호출
			log.info("- Load Handler : {} ", (MigrationHandler) Class.forName((String) conf.get("handler")).newInstance());
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
