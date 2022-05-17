package com.inzent.sh;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.quantum.mig.MigrationException;
import com.quantum.mig.MigrationHandler;
import com.quantum.mig.PrintStepHandler;
import com.quantum.mig.entity.MigrationAudit;
import com.quantum.mig.entity.MigrationResult;
import com.quantum.mig.entity.MigrationSource;
import com.quantum.mig.service.MigrationAuditService;
import com.quantum.mig.service.MigrationResultService;
import com.quantum.mig.service.MigrationSourceService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class KmsSimulMigrationHandler implements MigrationHandler {
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
				//log.info("- Type => {} " , (String)condition.get("type"));
				break;
			}			
			
		}catch (Exception e) {
			saveErrorInfo();
		}finally {
			storeResult(result);
		}
	}
	public static String subStrByte(String src , String seq , int cutlen) {
		if(src == null) {src = "";}{
			String str = src.replaceAll(seq, "");
			if(str.getBytes().length <= cutlen) {
				return str;
			}
			else {
				StringBuffer sb = new StringBuffer(cutlen);
				int nCnt = 0;
				for(char ch: str.toCharArray()) {
					nCnt += String.valueOf(ch).getBytes().length;
					if(nCnt > cutlen) break;
					sb.append(ch);
				}
				return sb.toString();
			}
		}
	}
	public static String removeSeperator(String src , String sep) {
		if(src == null) {src = "";}
		return src.replaceAll(sep, "");
	}
	private MigrationResult migSimulate(Map<String, Object> condition) throws MigrationException  {
		List<String[]> data_list = null;
		int page = (int)condition.get("page");
		int count = (int)condition.get("count");
		
		
		Map<String,Object> query_param = new HashMap<String,Object>();
		query_param.put("page", page);
		query_param.put("count", count);
		query_param.put("stime", makeSearchRequest((String)condition.get("stime")));
		query_param.put("etime", makeSearchRequest((String)condition.get("etime")));
		log.info(" - Query Param :  {} " , query_param.toString());
		
		//size
		data_list = MakeDummyData();
		String sql_size = "SELECT * FROM TNPSK_KNOW_MAP WHERE 1=1"; 
		log.debug(" - TASK SIZE SQL  =>  :  {} " , sql_size);
		log.info(" - TOTAL COUNT  =>  :  {} " , data_list.size());
		
		//search
		String sql_search = "SELECT MAP_ID , MAP_NM , CREATE_DATE, CREATE_USER FROM TNPSK_KNOW_MAP WHERE 1=1 AND UPDATE_DATE > 'STIME' AND UPDATE_DATE < 'ETIME' "; 
		log.debug(" - TASK SEARCH SQL =>  :  {} " , sql_search);
		
		int c =0;
		if(data_list != null) {
			for (String[] list : data_list) {
				log.info("- TASK SEARCH => : {}" , Arrays.toString(list));
				auditRecord(data_list.size(), data_list.get(c)[0]);
				c++;
			}
		}
		
		return makeResult(data_list.size());
	}

	private MigrationResult makeResult(int size) {
		//결과 makeResult 함수로 빼주기
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
	
	
	private void auditRecord(int total , String id){
		MigrationAudit audit = new MigrationAudit(id);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
		audit.setAction("0");
		audit.setMsg("테스트");
		audit.setTagId("BB11");
		audit.setResult(true);
		audit.setTime(sdf.format(new Date()));
		log.debug(" - TASK AUDIT  =>   : {} " , audit.toString());
		//auditService.record(audit);
	}
	

	private void storeResult(MigrationResult result) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
		log.debug(" - TASK RESULT  =>   : {} " , result.toString());
		//resService.record(result);
		
	}
	private void saveErrorInfo() {
		// TODO Auto-generated method stub
		
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
		
		return makeResult(data_list.size());
	}
	private List<String[]> MakeDummyData () {
		
		
		List<String[]> dummy_list = new ArrayList<String[]>();
		
		String[] data1 = {"AA1" , "TESTER1" , new Date().toString() , "WA-IM" };
		String[] data2 = {"AA2" , "TESTER1" , new Date().toString() , "WA-IM" };
		String[] data3 = {"AA3" , "TESTER1" , new Date().toString() , "WA-IM" };
		String[] data4 = {"AA4" , "TESTER1" , new Date().toString() , "WA-IM" };
		String[] data5 = {"AA5" , "TESTER1" , new Date().toString() , "WA-IM" };
		String[] data6 = {"AA6" , "TESTER1" , new Date().toString() , "WA-IM" };
		String[] data7 = {"AA7" , "TESTER1" , new Date().toString() , "WA-IM" };
		String[] data8 = {"AA8" , "TESTER1" , new Date().toString() , "WA-IM" };
		String[] data9 = {"AA9" , "TESTER1" , new Date().toString() , "WA-IM" };
		String[] data10 = {"AA10" , "TESTER1" , new Date().toString() , "WA-IM" };
		String[] data11 = {"AA11" , "TESTER1" , new Date().toString() , "WA-IM" };
		String[] data12 = {"AA12" , "TESTER1" , new Date().toString() , "WA-IM" };
		String[] data13 = {"AA13" , "TESTER1" , new Date().toString() , "WA-IM" };
		String[] data14 = {"AA14" , "TESTER1" , new Date().toString() , "WA-IM" };
		String[] data15 = {"AA15" , "TESTER1" , new Date().toString() , "WA-IM" };
		String[] data16 = {"AA16" , "TESTER1" , new Date().toString() , "WA-IM" };
		

		dummy_list.add(data1);
		dummy_list.add(data2);
		dummy_list.add(data3);
		dummy_list.add(data4);
		dummy_list.add(data5);
		dummy_list.add(data6);
		dummy_list.add(data7);
		dummy_list.add(data8);
		dummy_list.add(data9);
		dummy_list.add(data10);
		dummy_list.add(data11);
		dummy_list.add(data12);
		dummy_list.add(data13);
		dummy_list.add(data14);
		dummy_list.add(data15);
		dummy_list.add(data16);
		return dummy_list;
	}
}


