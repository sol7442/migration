package com.inzent.sh.print.handler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.inzent.sh.AbstractShMigHandler;
import com.inzent.sh.entity.FileMakeResult;
import com.inzent.sh.print.entity.PrintFile;
import com.inzent.sh.print.service.PrintService;
import com.inzent.sh.util.YamlUtil;
import com.inzent.xedrm.api.Result;
import com.inzent.xedrm.api.XAPIException;
import com.inzent.xedrm.api.XeConnect;
import com.inzent.xedrm.api.XeDocument;
import com.inzent.xedrm.api.XeElement;
import com.inzent.xedrm.api.domain.Document;
import com.inzent.xedrm.api.domain.Folder;
import com.quantum.mig.MigrationException;
import com.quantum.mig.MigrationHandler;
import com.quantum.mig.PrintStepHandler;
import com.quantum.mig.entity.MigrationAudit;
import com.quantum.mig.entity.MigrationResult;
import com.quantum.mig.service.MigrationAuditService;
import com.quantum.mig.service.MigrationResultService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PrintMigrationHandler extends AbstractShMigHandler implements MigrationHandler {
	private final String DEFAULT_FOLDER_PATH = "/준공도면관리/"; 
	Map<String,Object> conf;
	PrintStepHandler steper = null;
	PrintService srcService = new PrintService(); //도면 source 서비스
	MigrationAuditService auditService = new MigrationAuditService();
	MigrationResultService resService = new MigrationResultService();
	SimpleDateFormat format = new SimpleDateFormat("YYYY-MM-DD hh:mm:ss.ss");
	
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
				result = migByTime((Map<String,Object>)this.conf.get("time.condition"));
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
			storeResult(result);
		}
	}
	

	private MigrationResult migByTime(Map<String,Object> condition) throws MigrationException {
		
		int page = (int)condition.get("page");
		int count = (int)condition.get("count");
		String stime = (String)condition.get("stime");
		String etime = (String)condition.get("etime");
		
		Map<String,Object> query_param = new HashMap<String,Object>();
		query_param.put("page", page);
		query_param.put("count", count);
		query_param.put("stime", YamlUtil.convertTimeFormat(stime));
		query_param.put("etime", YamlUtil.convertTimeFormat(etime));
		
		condition.put("params", query_param);
		log.info(" - time.condition :  {} " , condition);
		MigrationResult result = this.migSimulate(condition);
		return result;
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
		@SuppressWarnings("unchecked")
		Map<String,Object> params = (Map<String, Object>) condition.get("params");
		log.info("{}",params);
		MigrationResult result = new MigrationResult();
		try {
			int fileCount = srcService.fileCount();
			List<PrintFile> files = srcService.searchFiles(params);
			for(PrintFile file : files) {
				XeConnect con = new XeConnect(connectionInfo.get("connect.url").toString()
						,connectionInfo.get("connect.id").toString()
						,connectionInfo.get("connect.pw").toString());
				/**
				 * 1. 폴더 생성 호출
				 */
				String fldPath = file.getFLD_PATH();
				if("Y".equals(file.getCHECK_YN())){
					Folder folder = null;
					try {
						//eid Shared 고정 - 전사 문서함
						//"준공도면관리" 프로퍼티 처리?
						folder = super.makeFolder(con, DEFAULT_FOLDER_PATH+fldPath, "Shared");
						/**
						 * 2. 폴더 권한 변경
						 */
						Result modifyRightsResult = super.modifyRights(con, folder.getEid());
						/**
						 * 3. 폴더 추가 속성
						 */
						this.updateAdditionalAttr(con, file, folder);
						this.recordAudit(String.valueOf(file.getFLD_SEQ()), folder.getEid(), "CREATE", "0", "SUCCESS");
						
					} catch (XAPIException e) {
						log.error(e.getMessage(),e);
						//폴더 생성 결과 저장
						this.recordAudit(String.valueOf(file.getFLD_SEQ()), folder.getEid(), "CREATE", e.getErrorCode(), e.getMessage());
					}
					
					FileMakeResult fileMakeResult = null;
					try {
						/**
						 * 4. 파일 생성
						 */
						fileMakeResult = this.makeFile(con, file, folder);
					} catch(XAPIException e) {
						log.error(e.getMessage(),e);
					} finally {
						this.recordAudit(String.valueOf(file.getOBJ_SEQ())+","+file.getOBJ_FILE_SEQ(), fileMakeResult.getDocId(), "CREATE", fileMakeResult.getErrCode(), fileMakeResult.getErrMsg());
						con.close();
					}
				} 
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
		return result;
	}
	
 
	private void recordAudit(String srcId , String tarId , String action , String resultCd , String msg) throws MigrationException {
		MigrationAudit audit = new MigrationAudit(srcId);
		audit.setTagId(tarId);
		audit.setAction(action);
		audit.setResult(resultCd);
		audit.setMsg(msg);
		log.debug(" - TASK AUDIT  =>   : {} " , audit.toString());
		auditService.record(audit);
	}

	private Result updateAdditionalAttr(XeConnect con ,PrintFile file ,Folder folder) throws XAPIException{
		XeElement xe = new XeElement(con);
		Map<String, String> attrValue = new HashMap<String, String>();
		attrValue.put("sh:ctrNm",file.getFLD_NM());
		attrValue.put("sh:operator ",file.getCOMPANY_NAME());
		// xe.updateAttrEx(파일 eid, map<속성명(String),속성값(String));
		Result result = xe.updateAttrEx(folder.getEid(), attrValue);
		return result;
	}
	 
	@SuppressWarnings("unchecked")
	private FileMakeResult makeFile(XeConnect con ,PrintFile file ,Folder folder) throws XAPIException,FileNotFoundException, IOException {
		XeDocument xd = new XeDocument(con);
		FileMakeResult fileMakeResult = null;
		Result result = null;
		File f = new File(file.getFileFullPath());
		try(InputStream is = new FileInputStream(f)){
			// xd.createDocument(업로드 대상 폴더eid, 파일, 업로드파일명, 생성자, 소유자, 생성일, 수정일,
			// 덮어쓰기여부(false), 파일명변경여부(false));
			result = xd.createDocument(folder.getEid(), is, file.getORG_FILE_NM() , file.getFILE_REG_ID(), file.getOWNER_USER_ID(), format.format(file.getFILE_REG_DT()),format.format(file.getFILE_UPD_DT()),
					false, false);
			if("ECM0001".equals(result.getReturnCode())) {
				//파일 중복 에러는 특수 처리
				//파일 검색 후 중복파일 삭제하고 처리
				List<Document> fileList = this.searchFileWithApi(con, folder.getEid(), file.getORG_FILE_NM());
				for(Document item : fileList) {
					Map<String, String> param = new HashMap<String, String>();
					
					String eid = item.getEid();
					LocalDateTime now = LocalDateTime.now();
					Timestamp timestamp = Timestamp.valueOf(now);
					
					param.put("docId", eid);
					param.put("description", "마이그레이션 중복 파일 삭제 - " + timestamp);
					
					Result updateDocResult = con.requestPost("updateDocProperty", param);
					if(updateDocResult.isSuccess()) {
						//Result deleteResult = this.deleteDoc(con, eid);
						this.deleteDoc(con, eid);
					}
				}
				fileMakeResult = this.makeFile(con, file, folder);
			} else {
				fileMakeResult = new FileMakeResult(result.getJsonObject());
			}
		} catch(XAPIException e) {
			e.printStackTrace();
			throw e;
		} catch(FileNotFoundException e) {
			e.printStackTrace();
			throw e;
		} catch(IOException e) {
			e.printStackTrace();
			throw e;
		} catch(Exception e) {
			e.printStackTrace();
			throw e;
		}
		return fileMakeResult;
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

	
}
