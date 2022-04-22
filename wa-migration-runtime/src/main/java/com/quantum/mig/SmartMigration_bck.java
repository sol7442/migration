package com.quantum.mig;
//package com.quantum.mig;
//
//import java.text.SimpleDateFormat;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import com.quantum.mig.entity.MigrationAudit;
//import com.quantum.mig.entity.MigrationResult;
//import com.quantum.mig.entity.MigrationSource;
//import com.quantum.mig.service.MigrationAuditService;
//import com.quantum.mig.service.MigrationResultService;
//import com.quantum.mig.service.MigrationSourceService;
//
//import lombok.extern.slf4j.Slf4j;
//
//@Slf4j
//public class SmartMigration implements Runnable {
//
//	Map<String, Object> conf;
//	MigrationHandler handler = null;
//	PrintStepHandler steper = null;
//	MigrationSourceService srcService = new MigrationSourceService();
//	MigrationAuditService auditService = new MigrationAuditService();
//	MigrationResultService resService = new MigrationResultService();
//	
//	public SmartMigration(Map<String, Object> conf) throws MigrationException {
//		this.conf = conf;
//		//this.executor = Executors.newSingleThreadExecutor();;
//		this.handler  = loadMigrationHandler(conf);
//		this.steper   = loadStepPrinter(conf);//new ConsoleStepPrinter(10);
//		
//	}
//
//	//total 값과 함께 넘기기 위해 total 값 조회하는 함수에서 호출해야함
//	private PrintStepHandler loadStepPrinter(Map<String, Object> conf) {
//		//total count는 conf 에서 구할수있는 값이 아님.
//		return new ConsoleStepPrinter(100, (int)conf.get("out.count"));
//	}
//
//	private MigrationHandler loadMigrationHandler(Map<String, Object> conf) throws MigrationException {
//		try {
//			//핸들러 메소드 호출
//			log.info("- Load Handler : {} ", (MigrationHandler) Class.forName((String) conf.get("handler")).newInstance());
//			return (MigrationHandler) Class.forName((String) conf.get("handler")).newInstance();
//		} catch (ClassNotFoundException  | InstantiationException | IllegalAccessException e) {
//			throw new MigrationException(e.getMessage(),e);
//		}
//	}
//
//	public void start() {
//		//this.executor.execute(this);
//		run();
//	}
//	public void stop() {
//		//this.executor.shutdown();
//		
//	}
//
//	@Override
//	public void run() {
//		try {
//			handler.migration(conf);
//		} catch (MigrationException e) {
//			// TODO Auto-generated catch block
//			saveErrorInfo();
//		}
//		MigrationResult result = null;
//		
//		Map<String,Object> condition = (Map<String,Object>)this.conf.get("condition");
//		System.out.println("condition : " + condition);
//		try {
//			switch ((String)condition.get("type")) {
//			case "time":
//				condition = (Map<String,Object>)this.conf.get("time.condition");
//				result = migByTime(condition);
//				break;
//			case "file":
//				condition = (Map<String,Object>)this.conf.get("file.condition");
//				result = migByFile(condition);
//				break;
//			case "simul":
//				condition = (Map<String,Object>)this.conf.get("simul.condition");
//				result = migSimulate(condition);
//			default:
//				break;
//			}			
//			
//		}catch (Exception e) {
//			saveErrorInfo();
//		}finally {
//			storeResult(result);
//		}
//	}
//
//	private MigrationResult migSimulate(Map<String, Object> condition) {
//		try {
//			migByTime(condition);
//		} catch (MigrationException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//		return null;
//	}
//
//	private MigrationResult migByFile(Map<String,Object> condition) throws MigrationException {
//		MigrationResult result = new MigrationResult();
//		result.migType = "FILE";
//		List<String> dis = readIdsFile();
//		/*
//		 * for (String id : dis) { Map<String,Object> data = src_repo.read(id);
//		 * 
//		 * MigrationAudit recode = handler.migration(data); steper.print(recode); //
//		 * res_repo.record(recode); audit_repo.record(recode); }
//		 */
//		return result;
//	}
//
//
//	private List<String> readIdsFile() {
//		return null;
//	}
//	
//	//이부분 샘플로 개발
//	private MigrationResult migByTime(Map<String,Object> condition) throws MigrationException {
//		log.info(" - time.condition :  {} " , condition);
//
//		List<MigrationSource> data_list = null;
//		
//		int page = (int)condition.get("page");
//		int count = (int)condition.get("count");
//		String stime = (String)condition.get("stime");
//		String etime = (String)condition.get("etime");
//		int total_count = 0;
//		int run_count = 0;
//		
//		Map<String,Object> query_param = new HashMap<String,Object>();
//		query_param.put("page", page);
//		query_param.put("count", count);
//		query_param.put("stime", makeSearchRequest(stime));
//		query_param.put("etime", makeSearchRequest(etime));
//		//조건으로 검색
//		total_count = srcService.size();
//		data_list = srcService.search(page, count, query_param);
//		log.info("- TASK COUNT  =>  :  {} " , total_count);
//		if(data_list != null) {
//			for (MigrationSource list : data_list) {
//				log.info("- TASK SEARCH => : {}" , list.toString());
//				auditRecord(total_count,list.getUSER_ID());
//				
//			}
//		}
//		
//		//결과 
//		MigrationResult result = new MigrationResult();
//		result.setMigClass("KMS");
//		result.setMigType((String) condition.get("type"));
//		result.setConfPath("test/kms");
//		result.setTotalCnt(total_count);
//		result.setTargetCnt(data_list.size());
//		result.setSuccessCnt(10);
//		result.setFailCnt(0);
//		storeResult(result);
//		//공통파트 -  조회
//		counting(data_list, total_count, run_count);
//		//공통파트 - 결과 저장하기   -- 내가 할거
//
//		return result;
//	}
//	public MigrationResult setResult(MigrationResult result) {
//		return result;
//	}
//	//yml date formt 20:01:01 -> 20-01-01 
//	public String makeSearchRequest(String time) {
//		StringBuffer dateForm = new StringBuffer();
//		
//		String[] searchRequest = time.split(" ");  
//		String dateStr = searchRequest[0].replaceAll(":", "-");
//		dateForm.append(dateStr+" ");
//		dateForm.append(searchRequest[1]);
//		return dateForm.toString();
//	}
//	
//	private void counting(List<MigrationSource> data_list, int total_count, int run_count) throws MigrationException {
//		while (run_count < total_count) {
//			// 출력 가운트 만큼 조회 = 페이지 네이션 - 10단위로 될 만큼  -> yml 에서 가져와야 한다. 
//			run_count++;
//			for (MigrationSource data : data_list) {
//				//MigrationAudit recode = handler.migration(data);
//				//res_repo.record(recode);
//				//steper.print(recode);
//			}
//		}
//	}
//
//	private void saveErrorInfo() {
//		// TODO Auto-generated method stub
//		// 에러가 난 정보에 대해여 파일로 정확하게 기록할 껏.
//	}
//
//	private void storeResult(MigrationResult result) {
//		log.debug(" - TASK RESULT  =>   : {} " , result.toString());
////			resService.record(result);
//	}
//	private void auditRecord(int total , String id) {
//		try {
//			MigrationAudit audit = new MigrationAudit(id);
//			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
//			audit.setAction("0");
//			audit.setMsg("테스트");
//			audit.setTagId("TEST1");
//			audit.setResult(true);
//			audit.setTime(sdf.format(new Date()));
//			log.debug(" - TASK AUDIT  =>   : {} " , audit.toString());
//			auditService.record(audit);
//		} catch (MigrationException e) {
//			new MigrationException(e.getMessage(),e);
//		}
//	}
//}
