package com.quantum.mig;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.quantum.mig.entity.MigrationAudit;
import com.quantum.mig.entity.MigrationResult;
import com.quantum.mig.entity.MigrationSource;
import com.quantum.mig.service.MigrationAuditService;
import com.quantum.mig.service.MigrationResultService;
import com.quantum.mig.service.MigrationSourceService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SmartMigration implements Runnable {

	Map<String, Object> conf;
	MigrationHandler handler = null;
	PrintStepHandler steper = null;
	MigrationSourceService srcService = new MigrationSourceService();
	MigrationAuditService auditService = new MigrationAuditService();
	MigrationResultService resService = new MigrationResultService();
	
	public SmartMigration(Map<String, Object> conf) throws MigrationException {
		this.conf = conf;
		//this.executor = Executors.newSingleThreadExecutor();;
		this.handler  = loadMigrationHandler(conf);
		this.steper   = loadStepPrinter(conf);//new ConsoleStepPrinter(10);
		
	}

	//total 값과 함께 넘기기 위해 total 값 조회하는 함수에서 호출해야함
	private PrintStepHandler loadStepPrinter(Map<String, Object> conf) {
		//total count는 conf 에서 구할수있는 값이 아님.
		return new ConsoleStepPrinter(10, (int)conf.get("out.count"));
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

	@SuppressWarnings("unchecked")
	@Override
	public void run() {
		MigrationResult result = null;
		
		Map<String,Object> condition = (Map<String,Object>)this.conf.get("condition");
		try {
			switch ((String)condition.get("type")) {
			case "time":
				condition = (Map<String,Object>)this.conf.get("time.condition");
				result = migByTime(condition);
				break;
			case "file":
				condition = (Map<String,Object>)this.conf.get("file.condition");
				result = migByFile();
				break;
			case "simul":
				condition = (Map<String,Object>)this.conf.get("simul.condition");
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
		List<String> dis = readIdsFile();
		/*
		 * for (String id : dis) { Map<String,Object> data = src_repo.read(id);
		 * 
		 * MigrationAudit recode = handler.migration(data); steper.print(recode); //
		 * res_repo.record(recode); audit_repo.record(recode); }
		 */
		return result;
	}


	private List<String> readIdsFile() {
		return null;
	}
	
	//이부분 샘플로 개발
	private MigrationResult migByTime(Map<String,Object> condition) throws MigrationException {
		log.info(" - time.condition :  {} " , condition);
		
		//결과 
		MigrationResult result = new MigrationResult();
		result.migType = "TIME";
		
		//한줄한줄에 대한 이력
		MigrationAudit audit = new MigrationAudit(null);

		List<MigrationSource> data_list = null;
		
		int page = (int)condition.get("page");
		int count = (int)condition.get("count");
		String stime = (String)condition.get("stime");
		String etime = (String)condition.get("etime");
		int total_count = 0;
		int run_count = 0;
		
		total_count = srcService.size();
		log.info("TOTAL_COUNT  =>  :  {} " , total_count);
		
		Map<String,Object> query_param = new HashMap<String,Object>();
		query_param.put("page", page);
		query_param.put("count", count);
		query_param.put("stime", makeSearchRequest(stime));
		query_param.put("etime", makeSearchRequest(etime));
		//조건으로 검색
		data_list = srcService.search(page, count, query_param);
		log.info("TOTAL_COUNT  =>  :  {} " , total_count);
		if(data_list != null) {
			for (MigrationSource list : data_list) {
				list.getUSER_ID();
				
				System.out.println(list.toString());
			}
		}
		
		//공통파트 -  조회
		counting(data_list, total_count, run_count);
		//공통파트 - 결과 저장하기   -- 내가 할거
		auditService.record(audit);
		return result;
	}
	
	//yml date formt 20:01:01 -> 20-01-01 
	public String makeSearchRequest(String time) {
		StringBuffer dateForm = new StringBuffer();
		
		String[] searchRequest = time.split(" ");  
		String dateStr = searchRequest[0].replaceAll(":", "-");
		dateForm.append(dateStr+" ");
		dateForm.append(searchRequest[1]);
		return dateForm.toString();
	}
	
	private void counting(List<MigrationSource> data_list, int total_count, int run_count) throws MigrationException {
		while (run_count < total_count) {
			// 출력 가운트 만큼 조회 = 페이지 네이션 - 10단위로 될 만큼  -> yml 에서 가져와야 한다. 
			run_count++;
			for (MigrationSource data : data_list) {
				MigrationAudit recode = handler.migration(data);
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
		try {
			resService.record(result);
		} catch (MigrationException e) {
			new MigrationException(e.getMessage(),e);
		}
	}
	private void auditRecord(MigrationAudit audit) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
			audit.setAction("0");
			audit.setMsg("테스트");
			audit.setTagId("TEST1");
			audit.setResult(true);
			audit.setTime(sdf.format(new Date()));
			auditService.record(audit);
		} catch (MigrationException e) {
			new MigrationException(e.getMessage(),e);
		}
	}
}
