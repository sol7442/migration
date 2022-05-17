package com.inzent.sh;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.quantum.mig.MigrationException;
import com.quantum.mig.MigrationHandler;
import com.quantum.mig.PrintStepHandler;
import com.quantum.mig.entity.MigrationAudit;
import com.quantum.mig.entity.MigrationResult;
import com.quantum.mig.service.MigrationAuditService;
import com.quantum.mig.service.MigrationResultService;
import com.quantum.mig.service.MigrationSourceService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AppvMigrationHandler implements MigrationHandler {
	Map<String,Object> conf;
	PrintStepHandler steper = null;
	MigrationSourceService srcService = new MigrationSourceService();
	MigrationAuditService auditService = new MigrationAuditService();
	MigrationResultService resService = new MigrationResultService();
	
	public void migration(Map<String,Object> conf) throws MigrationException {
		this.conf = conf;
		run();

	}
	//file , time , simul 
	@SuppressWarnings("unchecked")
	public void run() {
		MigrationResult result = null;
		
		Map<String,Object> condition = (Map<String,Object>)this.conf.get("condition");
		System.out.println("condition : " + condition);
		try {
			switch ((String)condition.get("type")) {
			case "time":
				condition = (Map<String,Object>)condition.get("time.condition");
				result = migByTime(condition);
				break;
			case "file":
				condition = (Map<String,Object>)condition.get("file.condition");
				result = migByFile(condition);
				break;
			case "simul":
				condition = (Map<String,Object>)condition.get("simul.condition");
				result = migSimulate(condition);
			default:
				break;
			}			
			
		}catch (Exception e) {
			saveErrorInfo();
		}finally {
			storeResult(result);
		}
	}
	

	private MigrationResult migByTime(Map<String,Object> condition) throws MigrationException {
		log.info(" - time.condition :  {} " , condition);
		return makeResult(100);
		

	}
	private MigrationResult migByFile(Map<String,Object> condition) throws MigrationException {
		log.info(" - file.condition :  {} " , condition);
//		MigrationResult result = new MigrationResult();
//		result.migType = "FILE";
//		List<String> dis = readIdsFile();
		return makeResult(100);
	}
	private MigrationResult migSimulate(Map<String, Object> condition) {
		log.info(" - simul.condition :  {} " , condition);
		return makeResult(100);
	}
	
	
	//결과 makeResult 함수
	private MigrationResult makeResult(int size) {
		
		MigrationResult result = new MigrationResult();
		result.setMigClass("KMS");
		result.setMigType("simul");
		result.setConfPath("test/kms");
		result.setTotalCnt(size);
		result.setTargetCnt(size);
		//성공 : target 에 넘어갔을때 
		//log 상으로 임의의 성공값으로 표기한다. 이부분에서 target 으로 성공적으로 넘어간 갯수 찍힘
		result.setSuccessCnt(size);
		result.setFailCnt(0);
		return result;
	}
	
	//이력 함수
	private void auditRecord(int total , String id) {
		try {
			MigrationAudit audit = new MigrationAudit(id);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
			audit.setAction("0");
			audit.setMsg("테스트");
			audit.setTagId("TEST1");
			audit.setResult("0");
			audit.setTime(sdf.format(new Date()));
			log.debug(" - TASK AUDIT  =>   : {} " , audit.toString());
			auditService.record(audit);
		} catch (MigrationException e) {
			new MigrationException(e.getMessage(),e);
		}
	}
	
	private void storeResult(MigrationResult result) {
		try {
			log.debug(" - TASK RESULT  =>   : {} " , result.toString());
			resService.record(result);
		} catch (MigrationException e) {
			new MigrationException(e.getMessage(),e);
		}
		
	}
	private void saveErrorInfo() {
		// TODO Auto-generated method stub
		
	}

	private List<String> readIdsFile() {
		return null;
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
}
