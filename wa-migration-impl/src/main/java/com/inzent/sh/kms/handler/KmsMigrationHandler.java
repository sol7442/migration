package com.inzent.sh.kms.handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.inzent.sh.ShMigHandler;
import com.inzent.sh.entity.FileMakeResult;
import com.inzent.sh.entity.FolderList;
import com.inzent.sh.exception.DuplicatedFileException;
import com.inzent.sh.kms.entity.KmsCnFile;
import com.inzent.sh.kms.entity.KmsFile;
import com.inzent.sh.kms.entity.KmsFolder;
import com.inzent.sh.kms.service.KmsService;
import com.inzent.sh.util.Validator;
import com.inzent.xedrm.api.Result;
import com.inzent.xedrm.api.XAPIException;
import com.inzent.xedrm.api.XeConnect;
import com.inzent.xedrm.api.XeElement;
import com.inzent.xedrm.api.XeFolder;
import com.inzent.xedrm.api.domain.Folder;
import com.quantum.mig.MigrationException;
import com.quantum.mig.MigrationHandler;
import com.quantum.mig.entity.MigrationResult;
import com.quantum.mig.service.MigrationResultService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class KmsMigrationHandler extends ShMigHandler implements MigrationHandler {
	private String KMS_ATTACH_EID ;
	KmsService srcService = new KmsService();
	MigrationResultService resService = new MigrationResultService();
	
	@SuppressWarnings("unchecked")
	public void migration(Map<String,Object> conf) throws MigrationException {
		super.conf = conf;
		super.steper = loadStepPrinter(conf);
		this.KMS_ATTACH_EID = String.valueOf(((Map<String,Object>)conf.get("extra")).get("eid"));
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
//		try {
//			log.debug(" - TASK RESULT  =>   : {} " , result.toString());
//			resService.record(result);
//		} catch (MigrationException e) {
//			new MigrationException(e.getMessage(),e);
//		}
		
	}
	private void saveErrorInfo() {
		// TODO Auto-generated method stub
		
	}
	@SuppressWarnings("unchecked")
	private MigrationResult migProcess(Map<String, Object> condition) throws MigrationException {
		Map<String,Object> params = (Map<String, Object>) condition.get("params");
		log.info("{}",params);
		MigrationResult result = new MigrationResult();
		List<KmsFile> files = srcService.searchFiles(params);
		int fileCount = files.size();
		int successCnt = 0;
		for(KmsFile file : files) {
			try {
				/**
				 * 1. 폴더 생성 
				 * 폴더는 자신을 포함 상위폴더도 모두 USE_AT 값이 Y가 나와야 한다.
				 */
				XeConnect con = null;
				Folder folder = null;
				String folderId = null;
				FileMakeResult fileMakeResult = null;
				FolderList folderList = this.makeFolderList(file.getMAP_ID());
				try {
					if(!this.isUseFolder(folderList)) {
						continue;
					}
					con = super.getConnection(this.conf);
					folderId = this.makeFolderWithoutAudit(con,folderList , KMS_ATTACH_EID);
					//folder = super.makeFolder(con,folderList.getFullPath() , APPR_ATTACH_EID);
					super.recordAudit(String.valueOf(file.getMAP_ID()), folderId, "CREATE", "0", "Success");
				} catch (XAPIException e) {
					log.error(e.getMessage(),e);
					//폴더 생성 결과 저장 진행중
					super.recordAudit(String.valueOf(file.getMAP_ID()),"" , "ERROR", e.getErrorCode(), e.toString());
				}
				
				/**
				 * 2. 파일 생성
				 */
				try {
					fileMakeResult = super.makeFile(con, file, folderId,false);
					super.recordAudit(file.getFILE_ID(), fileMakeResult.getDocId(), "CREATE", fileMakeResult.getErrCode(), fileMakeResult.getErrMsg(),outCount,files.size());
					successCnt++;
				} catch(XAPIException e) {
					log.error(e.getMessage(),e);
				} catch(DuplicatedFileException e) {
					super.recordAudit(file.getFILE_ID(),"", "IGNORE", "0", e.getMessage()+" : "+e.getFullFilePath(),outCount,files.size());
				} catch (Exception e) {
					log.error(e.getMessage(),e);
				} finally {
					if(con != null) {
						con.close();
					}
				}
			} catch (MigrationException e) {
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
	private MigrationResult migSimulate(Map<String, Object> condition) {
		// TODO Auto-generated method stub
		return null;
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
	
	private FolderList makeFolderList(String mapId) throws MigrationException {
		
		List<String> ids = makeIdsForParam(mapId);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("list", ids);
		List<KmsFolder> folders = srcService.searchFolders(params);
		FolderList folderList = new FolderList("지식자료");
		for(KmsFolder folder : folders) {
			Map<String,Object> data = new HashMap<String,Object>();
			data.put("name",Validator.convertValidStr(folder.getDATA_NM()));
			data.put("USE_AT", folder.getUSE_AT());
			data.put("DATA_CN", folder.getDATA_CN());
			data.put("CREATE_USER", folder.getCREATE_USER());
			data.put("CREATE_DATE", folder.getCREATE_DATE());
			data.put("UPDATE_DATE", folder.getUPDATE_DATE());
			folderList.add(data);
		}
		
		return folderList;
	}
	/**
	 * srcService.searchFolderIds(params); 결과 예시
	 * ex) /00000001/00000009
	 * @param mapId
	 * @return
	 * @throws MigrationException 
	 */
	private List<String> makeIdsForParam (String mapId) throws MigrationException {
		List<String> ids = null;
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("MAP_ID", mapId);
		String result = srcService.searchFolderIds(params);
		if(result != null) {
			ids = new ArrayList<String>();
			String[] idArray = result.split("/"); 
			for(int i = 1;i < idArray.length;i++) {
				ids.add(idArray[i]);
			}
		}
		return ids;
	}
	/**
	 * 상위 폴더 중 하나라도 사용여부가 N이면 폴더 생성 X
	 * @param folderList
	 * @return
	 */
	private boolean isUseFolder(FolderList folderList) {
		for(Map<String,Object> folder : folderList) {
			boolean result = "Y".equals(String.valueOf(folder.get("USE_AT")));
			if(result == false) {
				return false;
			}
		}
		return true;
		
	}
	/**
	 * 지식자료용 override
	 * data_cn값을 이용해서 파일도 만들어서 전송.
	 * @param con
	 * @param folderList
	 * @param eid
	 * @param fullPath : String으로 된 최총 폴더를 먼저 조회.
	 * @return
	 * @throws Exception 
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected String makeFolderWithoutAudit(XeConnect con , FolderList folderList ,String eid) throws MigrationException {
		Result result = null;
		XeFolder xf = new XeFolder(con);
		XeElement xe = new XeElement(con);
		String parentFolderId = eid;
		try {
			Folder targetFolder = xf.getFolderByPath(folderList.getFullPath());
			parentFolderId = targetFolder.getEid();
		} catch (XAPIException e) {
			if("FOL0003".equals(e.getErrorCode())) {
				for(Map<String,Object> folderInfo : folderList) {
					result = xf.createFolder(parentFolderId, (String)folderInfo.get("name"));
					if(result.isSuccess()) {
						parentFolderId = (String)result.getData(0).get("rid");
						modifyRights(con, parentFolderId);
						Map<String,String> elementAttr = (Map<String, String>)folderInfo.get("elementAttr");
						if(elementAttr != null) {
							Result attrResult = xe.updateAttrEx(parentFolderId, elementAttr);
							if(!attrResult.isSuccess()) {
								// 확장속성 저장 실패로 인한 폴더 생성 실패
								throw new XAPIException(attrResult.getReturnCode(),attrResult.getErrorMessage());
							}
							continue;
						}
						
						//지식관리 추가로직
						KmsCnFile cnFile = this.makeFile(folderInfo);
						try {
							super.makeFile(con, cnFile, parentFolderId, false);
						} catch (Exception e1) {
							// TODO Auto-generated catch block
							log.error(e1.getMessage(),e1);
							e1.printStackTrace();
						}
					} 
					if("ECM0001".equals(result.getReturnCode())) {
						parentFolderId = (String) result.getJsonObject().get("rid");
						continue;
					} 
				}
			}
		}
		return parentFolderId;
	}
	/**
	 * 게시판 컨텐츠 생성용 파일
	 * @param name
	 * @param contents
	 * @throws MigrationException
	 */
	@SuppressWarnings("unchecked")
	private KmsCnFile makeFile(Map<String,Object> data) throws MigrationException {
		//Data_CN 값으로 생성할 파일이 존재할 임시 폴더
		String tempPath = String.valueOf(((Map<String,Object>)conf.get("extra")).get("tempPath"));
		KmsCnFile cnFile = new KmsCnFile(data);
		cnFile.makeFile(tempPath);
		return cnFile;
	}
	
}
