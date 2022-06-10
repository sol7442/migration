package com.inzent.sh.print.handler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.inzent.sh.ShMigHandler;
import com.inzent.sh.entity.FileMakeResult;
import com.inzent.sh.print.entity.PrintFile;
import com.inzent.sh.print.service.PrintService;
import com.inzent.xedrm.api.Result;
import com.inzent.xedrm.api.XAPIException;
import com.inzent.xedrm.api.XeConnect;
import com.inzent.xedrm.api.XeElement;
import com.inzent.xedrm.api.domain.Folder;
import com.quantum.mig.MigrationException;
import com.quantum.mig.MigrationHandler;
import com.quantum.mig.entity.MigrationResult;
import com.quantum.mig.service.MigrationResultService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PrintMigrationHandler extends ShMigHandler implements MigrationHandler {
	private final String DEFAULT_FOLDER_PATH = "/준공도면관리/"; 
	
	PrintService srcService = new PrintService(); //도면 source 서비스
	MigrationResultService resService = new MigrationResultService();
	
	public void migration(Map<String,Object> conf) throws MigrationException {
		super.conf = conf;
		super.steper = loadStepPrinter(conf);
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
			log.error(e.getMessage(),e);
			e.printStackTrace();
			//saveErrorInfo();
		}finally {
			storeResult(result);
		}
	}
	

	private MigrationResult migByTime(Map<String,Object> condition) throws MigrationException {
		condition.put("params", super.makeTimeParameter(condition));
		log.info(" - time.condition :  {} " , condition);
		MigrationResult result = this.migProcess(condition);
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
		/*try {
			log.debug(" - TASK RESULT  =>   : {} " , result.toString());
			resService.record(result);
		} catch (MigrationException e) {
			new MigrationException(e.getMessage(),e);
		}*/
	}
	private void saveErrorInfo() {
		// TODO Auto-generated method stub
		
		
	}
	@SuppressWarnings("unchecked")
	private MigrationResult migProcess(Map<String, Object> condition) throws MigrationException {
		Map<String,Object> params = (Map<String, Object>) condition.get("params");
		log.info("{}",params);
		MigrationResult result = new MigrationResult();
		List<PrintFile> files = srcService.searchFiles(params);
		int fileCount = files.size(); 
		int successCnt = 0;
		for(PrintFile file : files) {
			try {
				/**
				 * 1. 폴더 생성 호출
				 */
				String fldPath = file.getFLD_PATH();
				Folder folder = null;
				XeConnect con = null;
				try {
					con = super.getConnection(this.conf);
					//eid Shared 고정 - 전사 문서함
					//"준공도면관리" 프로퍼티 처리?
					folder = super.makeFolder(con, DEFAULT_FOLDER_PATH+fldPath, "Shared");
					/**
					 * 2. 폴더 권한 변경
					 */
					//Result modifyRightsResult = super.modifyRights(con, folder.getEid());
					super.modifyRights(con, folder.getEid());
					/**
					 * 3. 폴더 추가 속성
					 */
					this.updateAdditionalAttr(con, file, folder);
					super.recordAudit(String.valueOf(file.getFLD_SEQ()), folder.getEid(), "CREATE", "0", "Success");
					
				} catch (XAPIException e) {
					log.error(e.getMessage(),e);
					//폴더 생성 결과 저장
					super.recordAudit(String.valueOf(file.getFLD_SEQ()), folder.getEid(), "CREATE", e.getErrorCode(), e.toString());
				} 
				
				FileMakeResult fileMakeResult = null;
				try {
					/**
					 * 4. 파일 생성
					 */
					fileMakeResult = super.makeFile(con, file, folder,false);
					con = fileMakeResult.getConnection();
					successCnt++;
				} catch(XAPIException e) {
					log.error(e.getMessage(),e);
				} catch (Exception e) {
					log.error(e.getMessage(),e);
				} finally {
					super.recordAudit(String.valueOf(file.getOBJ_SEQ())+","+file.getOBJ_FILE_SEQ(), fileMakeResult.getDocId(), "CREATE", fileMakeResult.getErrCode(), fileMakeResult.getErrMsg(),outCount,files.size());
					if(con != null) {
						con.close();
					}
				}
			} catch (MigrationException e) {
				// TODO Auto-generated catch block
				log.error(e.getMessage(),e);
				e.printStackTrace();
			} catch (Exception e) {
				log.error(e.getMessage(),e);
				e.printStackTrace();
			}
		}
		log.debug("total : {} / target : {}" , fileCount , successCnt);
		log.trace("list Detail : {}",files);
		
		//미사용
		result.setMigClass("PRINT");
		//result.setMigType(condition.get("type").toString());
		result.setConfPath("kjm/print");
		result.setTotalCnt(fileCount);
		result.setTargetCnt(files.size());
		result.setSuccessCnt(files.size());
		result.setFailCnt(0);
		log.debug("result : {}",result);
			
		return result;
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
	
	 
	
	private MigrationResult migSimulate(Map<String,Object> condition) throws MigrationException {
		MigrationResult result = new MigrationResult();
		List<String> dis = readIdsFile();
		return result;
	}

	private List<String> readIdsFile() {
		return null;
	}
	
	
}
