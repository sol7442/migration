package com.inzent.sh.aprv.handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.inzent.sh.ShMigHandler;
import com.inzent.sh.aprv.entity.AprvFile;
import com.inzent.sh.aprv.entity.AprvPerson;
import com.inzent.sh.aprv.service.AprvService;
import com.inzent.sh.entity.FileMakeResult;
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
public class AprvMigrationHandler extends ShMigHandler implements MigrationHandler {
	private String APPR_ATTACH_EID ;
	//private final String APPR_ATTACH_EID = "Shared";
	//= "202203281636335j"
	AprvService srcService = new AprvService(); 
	MigrationResultService resService = new MigrationResultService();
	
	@SuppressWarnings("unchecked")
	public void migration(Map<String,Object> conf) throws MigrationException {
		super.conf = conf;
		super.steper = loadStepPrinter(conf);
		this.APPR_ATTACH_EID = String.valueOf(((Map<String,Object>)conf.get("extra")).get("eid"));
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
	@Deprecated
	private MigrationResult migProcess_old(Map<String, Object> condition) throws MigrationException {
		Map<String,Object> params = (Map<String, Object>) condition.get("params");
		log.info("{}",params);
		MigrationResult result = new MigrationResult();
		List<AprvFile> files = srcService.searchFiles(params);
		int fileCount = files.size(); 
		int successCnt = 0;
		for(AprvFile file : files) {
			try {
				XeConnect con = super.getConnection(this.conf);
				Folder folder = null;
				try {
					/**
					 * 1. 폴더 생성 호출
					 */
					String fldPath = makeFldPath(file);
					folder = super.makeFolder(con, fldPath, APPR_ATTACH_EID);
					/**
					 * 2. 폴더 권한 변경
					 */
					List<AprvPerson> personsInHist = srcService.searchPersonsFromHist(file);
					List<AprvPerson> personsInPrgrHist = srcService.searchPersonsFromPrgrHist(file);
					Result modifyRightsResult = this.modifyRights(con, folder.getEid(),personsInHist,personsInPrgrHist);
					//폴더 ID -> 결재번호로 대체
					super.recordAudit(folder.getPath()+","+file.getAPRV_SEQ(), folder.getEid(), "CREATE", "0", "Success");
				} catch(XAPIException e) {
					log.error(e.getMessage(),e);
					super.recordAudit(folder.getPath()+","+file.getAPRV_SEQ(), folder.getEid(), "CREATE", e.getErrorCode(), e.toString());
				} 
				/**
				 * 3. 파일 생성
				 */
				FileMakeResult fileMakeResult = null;
				try {
					/**
					 * 4. 파일 생성
					 */
					fileMakeResult = super.makeFile(con, file, folder,false);
					con = fileMakeResult.getConnection();
					/**
					 * 5. file id Description 갱신
					 */
					Map<String, String> param = new HashMap<String, String>();
					param.put("docId", fileMakeResult.getDocId());
					param.put("description", file.getFILE_ID());
					con.requestPost("updateDocProperty", param);
					successCnt++;
				} catch(XAPIException e) {
					log.error(e.getMessage(),e);
				} catch (Exception e) {
					log.error(e.getMessage(),e);
				} finally {
					super.recordAudit(file.getFILE_ID(), fileMakeResult.getDocId(), "CREATE", fileMakeResult.getErrCode(), fileMakeResult.getErrMsg(),outCount,files.size());
					con.close();
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
		
		return result;
	}
	
	@SuppressWarnings("unchecked")
	private MigrationResult migProcess(Map<String, Object> condition) throws MigrationException {
		Map<String,Object> params = (Map<String, Object>) condition.get("params");
		log.info("{}",params);
		MigrationResult result = new MigrationResult();
		List<AprvFile> files = srcService.searchFiles(params);
		int fileCount = files.size(); 
		int successCnt = 0;
		for(AprvFile file : files) {
			try {
				/**
				 * 1. 폴더 생성 호출
				 */
				List<Map<String,Object>> folderList = this.makeFolderListFromPath(file);
				XeConnect con = null;
				String folderId = null;
				try {
					con = super.getConnection(this.conf);
					folderId = super.makeFolderWithoutAudit(con, folderList, APPR_ATTACH_EID);
					/**
					 * 2. 폴더 권한 변경
					 * 2022-06-24 이병희 수석님 요청으로 불필요
					 */
//					List<AprvPerson> personsInHist = srcService.searchPersonsFromHist(file);
//					List<AprvPerson> personsInPrgrHist = srcService.searchPersonsFromPrgrHist(file);
//					this.modifyRights(con, folderId,personsInHist,personsInPrgrHist);
					/////////////////////불필요///////////////////////
					super.modifyRights(con, folderId);
					//폴더 ID -> 결재번호로 대체
					super.recordAudit(this.makeFldPath(file)+","+file.getAPRV_SEQ(), folderId, "CREATE", "0", "Success");
				} catch(XAPIException e) {
					log.error(e.getMessage(),e);
					super.recordAudit(this.makeFldPath(file)+","+file.getAPRV_SEQ(), folderId, "ERROR", e.getErrorCode(), e.toString());
				} 
				/**
				 * 3. 파일 생성
				 */
				FileMakeResult fileMakeResult = null;
				try {
					/**
					 * 4. 파일 생성
					 */
					fileMakeResult = super.makeFile(con, file, folderId,false);
					/**
					 * 5. file id Description 갱신
					 */
					Map<String, String> param = new HashMap<String, String>();
					param.put("docId", fileMakeResult.getDocId());
					param.put("description", file.getFILE_ID());
					con.requestPost("updateDocProperty", param);
					successCnt++;
				} catch(XAPIException e) {
					log.error(e.getMessage(),e);
				} catch (Exception e) {
					log.error(e.getMessage(),e);
				} finally {
					super.recordAudit(file.getFILE_ID(), fileMakeResult.getDocId(), "CREATE", fileMakeResult.getErrCode(), fileMakeResult.getErrMsg(),outCount,files.size());
					con.close();
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
		
		return result;
	}
	/**
	 * 기안자, 결재자 등....사용자수만큼 rightListSecuritySimple.add(addRights); 한다.
	 * 데이터 구조상 한 사용자에 대해 한 번만 권한을 주고자 List를 분리(쿼리 분리)
	 * @param con
	 * @param eid
	 * @param personsInHist
	 * @param personsInPrgrHist
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private Result modifyRights(XeConnect con , String eid, List<AprvPerson> personsInHist , List<AprvPerson>personsInPrgrHist ) {
		XeElement xe = new XeElement(con);
		Map<String,Object> jsonResult = getRights(xe, eid);
		Map<String,Object> security = getSecurityFromRight(jsonResult);
		Map<String,Object> permission = getPermissionFromRight(jsonResult);
		List rightListSecuritySimple = getRightListFromMap(security);
		List rightListPermissionSimple = getRightListFromMap(permission);
		
		for(AprvPerson person : personsInPrgrHist) {
			if(person.getDECIDER_EMP_NO() != null) {
				rightListSecuritySimple.add(this.makeAddRights(person.getDECIDER_EMP_NO()));
			}
			if(person.getCREATE_USER() != null) {
				rightListSecuritySimple.add(this.makeAddRights(person.getCREATE_USER()));
			}
		}
		
		for(AprvPerson person : personsInHist) {
			if(person.getPUB_INSPT_PRSN_EMP_NO() != null) {
				rightListSecuritySimple.add(this.makeAddRights(person.getPUB_INSPT_PRSN_EMP_NO()));
			}
		}
		
		this.setRightListPermssion(rightListPermissionSimple);
		return this.modifyRight(xe, eid, rightListSecuritySimple, rightListPermissionSimple);
	} 
	
	private Map<String,Object> makeAddRights(String userId) {
		Map<String,Object> addRights = new HashMap<String, Object>();
		addRights.put("privType", "1");
		addRights.put("groupId", "_user_" + userId); 
		addRights.put("groupType", "-1");
		addRights.put("templateId", "MODIFY");
		return addRights;
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
	
	private String makeFldPath(AprvFile file) {
		StringBuffer sb = new StringBuffer();
		sb.append("/");
		sb.append(file.getYear());
		sb.append("/");
		sb.append(file.getMonthDay());
		sb.append("/");
		sb.append(file.getAPRV_SEQ());
		return sb.toString();
	}
	
	private List<Map<String,Object>> makeFolderListFromPath(AprvFile file) throws MigrationException {
		String fldPath = this.makeFldPath(file);
		List<Map<String, Object>> folderList = new ArrayList<Map<String, Object>>();
		Map<String, Object> folder = null;
		String[] fldPathArray = fldPath.split("/");
		/**
		 * 맨앞자리 공백으로 index 1부터 시작
		 * ex) ["" , "2022" , "0614" , "12"]
		 * SrcId는 이력 테이블용	
		 */
		for(int i = 1 ; i <fldPathArray.length ; i++) {
	    	if (fldPathArray[i] != null && !(fldPathArray[i].trim()).equals("")) {
	    		folder = new HashMap<String, Object>();
	        	folder.put("name", fldPathArray[i]);
	        	folder.put("createDate", format.format(file.getCREATE_DATE()));
	        	folder.put("SrcId",this.makeFldPath(file)+","+file.getAPRV_SEQ());
    		}
	    	folderList.add(folder);
	    }
	    return folderList;
	}
	
}
