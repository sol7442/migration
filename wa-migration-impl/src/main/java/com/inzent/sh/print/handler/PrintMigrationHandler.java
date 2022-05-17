package com.inzent.sh.print.handler;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.inzent.sh.AbstractShMigHandler;
import com.inzent.sh.print.entity.PrintFile;
import com.inzent.sh.print.service.PrintService;
import com.inzent.xedrm.api.XAPIException;
import com.inzent.xedrm.api.XeConnect;
import com.inzent.xedrm.api.XeDocument;
import com.inzent.xedrm.api.domain.Folder;
import com.quantum.mig.MigrationException;
import com.quantum.mig.MigrationHandler;
import com.quantum.mig.PrintStepHandler;
import com.quantum.mig.entity.MigrationAudit;
import com.quantum.mig.entity.MigrationResult;
import com.quantum.mig.entity.MigrationSource;
import com.quantum.mig.service.MigrationAuditService;
import com.quantum.mig.service.MigrationResultService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PrintMigrationHandler extends AbstractShMigHandler implements MigrationHandler {
	Map<String,Object> conf;
	PrintStepHandler steper = null;
	PrintService srcService = new PrintService(); //도면 source 서비스
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
			//saveErrorInfo();
		}finally {
			//storeResult(result);
		}
	}
	

	private MigrationResult migByTime(Map<String,Object> condition) throws MigrationException {
		log.info(" - time.condition :  {} " , condition);

		List<MigrationSource> data_list = null;
		
		int page = (int)condition.get("page");
		int count = (int)condition.get("count");
		String stime = (String)condition.get("stime");
		String etime = (String)condition.get("etime");
		int total_count = 0;
		int run_count = 0;
		
		Map<String,Object> query_param = new HashMap<String,Object>();
		query_param.put("page", page);
		query_param.put("count", count);
		query_param.put("stime", makeSearchRequest(stime));
		query_param.put("etime", makeSearchRequest(etime));
		//조건으로 검색
		total_count = srcService.size();
		data_list = srcService.search(page, count, query_param);
		log.info("- TASK COUNT  =>  :  {} " , total_count);
		if(data_list != null) {
			for (MigrationSource list : data_list) {
				log.info("- TASK SEARCH => : {}" , list.toString());
				auditRecord(total_count,list.getUSER_ID());
				
			}
		}
		//XeConnect con = new XeCon
		//결과 makeResult 함수로 빼주기
		MigrationResult result = new MigrationResult();
		//
		result.setMigClass("KMS");
		result.setMigType((String) condition.get("type"));
		result.setConfPath("test/kms");
		result.setTotalCnt(total_count);
		result.setTargetCnt(data_list.size());
		//성공 : target 에 넘어갔을때 
		//log 상으로 임의의 성공값으로 표기한다. 이부분에서 target 으로 성공적으로 넘어간 갯수 찍힘
		result.setSuccessCnt(data_list.size());
		result.setFailCnt(0);
	
		
		return result;
	}
	
	
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
	//migrationResult 세팅해주는 메소드 필요
//	private void makeResult(MigrationResult result) {
//		MigrationResult result = new MigrationResult();
//		result.setMigClass("KMS");
//		result.setMigType((String) condition.get("type"));
//		result.setConfPath("test/kms");
//		result.setTotalCnt(total_count);
//		result.setTargetCnt(data_list.size());
//		result.setSuccessCnt(10);
//		result.setFailCnt(0);
//	}
	
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
	private MigrationResult migSimulate(Map<String, Object> condition) {
		@SuppressWarnings("unchecked")
		Map<String,Object> connectionInfo = (Map<String, Object>) this.conf.get("xe");
		Map<String,Object> params = new HashMap<String, Object>();
		MigrationResult result = new MigrationResult();
		SimpleDateFormat format = new SimpleDateFormat("YYYY-MM-DD hh:mm:ss.ss");
		try {
			int fileCount = srcService.fileCount();
			//List<PrintFolder> list = srcService.searchFolders(params);
			
			
			List<PrintFile> files = srcService.searchFiles(params);
			for(PrintFile file : files) {
				//DefaultMigIdentity fldId = DefaultMigIdentity.builder().id(String.valueOf(file.getFLD_SEQ())).build();
				//PrintFolder fld = srcService.readFolder(fldId);
				
				//매번 커넥션 생성? 혹은 커넥션 재활용 가능?
				//설정화 시킬 예정
				//XeConnect con = new XeConnect("http://54.180.132.53:8300/xedrm", "test01","qwer1234!");
				XeConnect con = new XeConnect(connectionInfo.get("connect.url").toString()
						,connectionInfo.get("connect.id").toString()
						,connectionInfo.get("connect.pw").toString());
				//폴더 생성 호출
				String fldPath = file.getFLD_PATH();
				if("Y".equals(file.getCHECK_YN())){
					Folder fldMakeResult = null;
					try {
						//eid Shared 고정 - 전사 문서함
						//"준공도면관리" 프로퍼티 처리?
						fldMakeResult = super.makeFolder(con, "/준공도면관리/"+fldPath, "Shared");
						this.recordAudit(String.valueOf(file.getFLD_SEQ()), fldMakeResult.getEid(), "CREATE", "0", "SUCCESS");
					} catch (XAPIException e) {
						//폴더 생성 결과 저장
						this.recordAudit(String.valueOf(file.getFLD_SEQ()), fldMakeResult.getEid(), "CREATE", e.getErrorCode(), e.getMessage());
					}
					
					 
					
					//audit.set
					
					XeDocument xd = new XeDocument(con);

					/*File f = new File(file.getFILE_PATH());
					InputStream is = new FileInputStream(f);

					// xd.createDocument(업로드 대상 폴더eid, 파일, 업로드파일명, 생성자, 소유자, 생성일, 수정일,
					// 덮어쓰기여부(false), 파일명변경여부(false));
					Result fileMakeResult = xd.createDocument(fldMakeResult.getEid(), is, file.getORG_FILE_NM() , file.getFILE_REG_ID(), file.getOWNER_USER_ID(), format.format(file.getFILE_REG_DT()),format.format(file.getFILE_UPD_DT()),
							false, false);*/
					
				} 
				
				
				
						
				
 
				
				
				//권한 변경
				
				//
				
			}
			
			
			
			log.debug("total : {} / target : {}" , fileCount , files.size());
			log.debug("list Detail : {}",files);
			
			result.setMigClass("PRINT");
			//result.setMigType(condition.get("type").toString());
			result.setConfPath("kjm/print");
			result.setTotalCnt(fileCount);
			result.setTargetCnt(files.size());
			result.setSuccessCnt(files.size());
			result.setFailCnt(0);
			
			
			log.debug("result : {}",result);
			
			
		} catch (MigrationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
 
	private void recordAudit(String srcId , String tarId , String action , String resultCd , String msg) throws MigrationException {
		MigrationAudit audit = new MigrationAudit(srcId);
		audit.setTagId(tarId);
		audit.setAction(action);
		audit.setResult(resultCd);
		audit.setMsg(msg);
		
		auditService.record(audit);
	}
	
	
	
	private MigrationResult migByFile(Map<String,Object> condition) throws MigrationException {
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
