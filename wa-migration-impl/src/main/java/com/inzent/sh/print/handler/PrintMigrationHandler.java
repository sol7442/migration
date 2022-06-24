package com.inzent.sh.print.handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.inzent.sh.ShMigHandler;
import com.inzent.sh.entity.FileMakeResult;
import com.inzent.sh.entity.FolderList;
import com.inzent.sh.exception.DuplicatedFileException;
import com.inzent.sh.exception.OmittedFolderException;
import com.inzent.sh.print.entity.PrintFile;
import com.inzent.sh.print.entity.PrintFolder;
import com.inzent.sh.print.service.PrintService;
import com.inzent.sh.util.Validator;
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
	public static final String REPLACED_CHAR = "^%";
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
		
		
		String omittedFolderId = null;
		/**
		 * 누락파일 저장용 폴더 생성
		 * 누락파일 -> 계층쿼리를 통해 조회된 폴더에 누락이 생긴 경우 해당 파일은 누락파일 저장용 폴더에 넣는다.
		 * 누락파일 처리 X
		 */
		/*XeConnect conOmittedFolder = null;
		try {
			conOmittedFolder = super.getConnection(this.conf);
			Folder omittedFolder = this.makeFolderForOmittedFile(conOmittedFolder);
			omittedFolderId = omittedFolder.getEid();
		} catch(Exception e) {
			log.error(e.getMessage(),e);
			throw e;
		} finally {
			if(conOmittedFolder !=null) {
				conOmittedFolder.close();
			}
		}*/
		
		for(PrintFile file : files) {
			try {
				/**
				 * 1. 폴더 생성 호출 , 폴더 권한 변경 , 폴더 추가 속성 처리
				 */
				String folderId = null;
				XeConnect con = null;
				//List<Map<String,Object>> folderList = null;
				FolderList folderList = null;
				FileMakeResult fileMakeResult = null;
				try {
					//folderList = makeFolderListFromPathWithoutQuery(file);
					folderList = makeFolderList(file);
					con = super.getConnection(this.conf);
					//eid Shared 고정 - 전사 문서함
					//"준공도면관리" 프로퍼티 처리?
					folderId = super.makeFolderWithoutAudit(con,folderList , "Shared");
					super.recordAudit(String.valueOf(file.getFLD_SEQ()), folderId, "CREATE", "0", "Success");
				} catch (XAPIException e) {
					log.error(e.getMessage(),e);
					//폴더 생성 결과 저장 진행중
					super.recordAudit(String.valueOf(file.getFLD_SEQ()),"" , "ERROR", e.getErrorCode(), e.toString());
				} catch (OmittedFolderException e) {
					/*folderId = omittedFolderId;
					fileMakeResult = super.makeFile(con, file, folderId,false);
					successCnt++;*/
				}
				if(fileMakeResult==null) {
					try {
						/**
						 * 4. 파일 생성
						 */
						//1)중복파일 폴더 API 통해 추출
						//fileMakeResult = super.makeFile(con, file, folderId,false);
						//2)중복파일 폴더 LIST 사용
						Folder fold = new Folder();
						fold.setEid(folderId);
						fold.setPath(this.makeFoldPathStringFromList(folderList));
						fileMakeResult = super.makeFile(con, file, fold,false);
						super.recordAudit(String.valueOf(file.getOBJ_SEQ())+","+file.getOBJ_FILE_SEQ(), fileMakeResult.getDocId(), "CREATE", fileMakeResult.getErrCode(), fileMakeResult.getErrMsg(),outCount,files.size());
						successCnt++;
					} catch(XAPIException e) {
						log.error(e.getMessage(),e);
					} catch(DuplicatedFileException e) {
						super.recordAudit(String.valueOf(file.getOBJ_SEQ())+","+file.getOBJ_FILE_SEQ(),"", "IGNORE", "0", e.getMessage()+" : "+e.getFullFilePath(),outCount,files.size());
					} catch (Exception e) {
						log.error(e.getMessage(),e);
					} finally {
						if(con != null) {
							con.close();
						}
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
	@Deprecated
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
	/**
	 * @deprecated 
	 * 계층쿼리 수행 후 비교 로직 사용 X - 성능상의 이슈 (2022-06-20 이병희 수석님)
	 * @param file
	 * @return
	 * @throws MigrationException
	 */
	private List<Map<String, Object>> makeFolderListFromPath(PrintFile file) throws MigrationException {
		String fldPath = file.getFLD_PATH();
		List<PrintFolder> folderInfoList = srcService.searchFolders(makeParamForFolders(String.valueOf(file.getFLD_SEQ())));
		List<Map<String, Object>> folderList = new ArrayList<Map<String, Object>>();
		//최상위 폴더
		Map<String, Object> folder = new HashMap<String, Object>();
		folder.put("name", "준공도면관리");
		// api개선 이후 폴더생성일 적용 시 사용. 상수값?
		// 이병희 수석님 의견으로 준공도면관리 라고 하는 폴더는 생성되어 있을 것이라고 확인.
		// 중복에러 처리 나면서 ignore 될 것임.
	    folder.put("createDate", "2020-01-01 09:03:25"); 
	    folder.put("SrcId" , "준공도면관리");
	    folderList.add(folder);
	    
	    String[] fldPathArray = fldPath.split("/");
	    
	    /**
	     * depth 비교 
	     * 누락폴더 있는지 확인 누락폴더 존재시 해당 파일은 누락폴더에 생성
	     * 불필요 로직
	     */
	    int fldPathSize = folderInfoList.size();
	    if(fldPathSize!=fldPathArray.length) {
	    	throw new OmittedFolderException(fldPathArray.length, fldPathSize, "omitted folder : " + folderInfoList.get(fldPathSize-1));
	    }
	    
	    for(int i = 0 ; i <fldPathArray.length ; i++) {
	    	if (i == 0 && "SH공사".equals(fldPathArray[0])) {
               continue;
	        }
	    	if (fldPathArray[i] != null && !(fldPathArray[i].trim()).equals("")) {
	    		PrintFolder folderInfo = folderInfoList.get(i);
	    		folder = new HashMap<String, Object>();
	        	folder.put("name", Validator.convertValidStr(fldPathArray[i]));
	        	folder.put("createDate", format.format(folderInfo.getREG_DT()));
	        	folder.put("SrcId" , folderInfo.getFLD_SEQ());
    		}
	    	
	    	if (i == 2 && folderInfoList.get(i).isAddAttrValue()) {
	    		Map<String, String> attrValue = new HashMap<String, String>();
	    		attrValue.put("sh:ctrNm", fldPathArray[i]);
	    		folder.put("elementAttr", attrValue);
	    	}
	    	folderList.add(folder);
	    	log.debug("{}=={}",fldPathArray[i],folderInfoList.get(i).getFLD_NM());
	    }
	    return folderList;
	}
	/**
	 * CreateDate 사용 안함으로 관련 소스 제거
	 * @deprecated 폴더 검증로직 추가 버젼으로 인해 미사용
	 * @param file
	 * @return
	 * @throws MigrationException
	 */
	private List<Map<String, Object>> makeFolderListFromPathWithoutQuery(PrintFile file) throws MigrationException {
		String fldPath = file.getFLD_PATH();
		List<Map<String, Object>> folderList = new ArrayList<Map<String, Object>>();
		
		//최상위 폴더
		Map<String, Object> folder = new HashMap<String, Object>();
		folder.put("name", "준공도면관리");
		// api개선 이후 폴더생성일 적용 시 사용. 상수값?
		// 이병희 수석님 의견으로 준공도면관리 라고 하는 폴더는 생성되어 있을 것이라고 확인.
		// 중복에러 처리 나면서 ignore 될 것임.
	    folder.put("SrcId" , "준공도면관리");
	    folderList.add(folder);
	    String[] fldPathArray = fldPath.split("/");
	    for(int i = 0 ; i <fldPathArray.length ; i++) {
	    	if (i == 0 && "SH공사".equals(fldPathArray[0])) {
               continue;
	        }
	    	if (fldPathArray[i] != null && !(fldPathArray[i].trim()).equals("")) {
	    		folder = new HashMap<String, Object>();
	        	folder.put("name", Validator.convertValidStr(fldPathArray[i]));
    		}
	    	
	    	if (i == 2 && PrintFolder.isAddAttrValue(String.valueOf(folderList.get(i-1).get("name")))) {
	    		Map<String, String> attrValue = new HashMap<String, String>();
	    		attrValue.put("sh:ctrNm", fldPathArray[i]);
	    		folder.put("elementAttr", attrValue);
	    	}
	    	folderList.add(folder);
	    }
	    return folderList;
	}
	
	/**
	 * CreateDate 사용 안함으로 관련 소스 제거
	 * @param file
	 * @return
	 * @throws MigrationException
	 */
	private FolderList makeFolderList(PrintFile file) throws MigrationException {
		String fldPath = file.getFLD_PATH();
		//FolderList folderList = new FolderList("전사함");
		String rootPathName = (Map<String,Object>)conf.get("extra")==null?"전사함":String.valueOf(((Map<String,Object>)conf.get("extra")).get("rootPathName"));
		FolderList folderList = new FolderList(rootPathName==null?"전사함":rootPathName);
		
		//최상위 폴더
		Map<String, Object> folder = new HashMap<String, Object>();
		folder.put("name", "준공도면관리");
		// api개선 이후 폴더생성일 적용 시 사용. 상수값?
		// 이병희 수석님 의견으로 준공도면관리 라고 하는 폴더는 생성되어 있을 것이라고 확인.
		// 중복에러 처리 나면서 ignore 될 것임.
	    folder.put("SrcId" , "준공도면관리");
	    folderList.add(folder);
	    String[] fldPathArray = fldPath.split("/");
	    for(int i = 0 ; i <fldPathArray.length ; i++) {
	    	if (i == 0 && "SH공사".equals(fldPathArray[0])) {
               continue;
	        }
	    	if (fldPathArray[i] != null && !(fldPathArray[i].trim()).equals("")) {
	    		folder = new HashMap<String, Object>();
	        	folder.put("name", Validator.convertValidStr(fldPathArray[i]));
    		}
	    	
	    	if (i == 2 && PrintFolder.isAddAttrValue(String.valueOf(folderList.get(i-1).get("name")))) {
	    		Map<String, String> attrValue = new HashMap<String, String>();
	    		attrValue.put("sh:ctrNm", fldPathArray[i]);
	    		folder.put("elementAttr", attrValue);
	    	}
	    	folderList.add(folder);
	    }
	    return folderList;
	}
	
	
	
	private Map<String,Object> makeParamForFolders(String fldSeq) {
		Map<String,Object> params = new HashMap<String, Object>();
		params.put("FLD_SEQ", fldSeq);
		return params;
	}
	
	public Folder makeFolderForOmittedFile(XeConnect con) {
		return super.makeFolder(con, DEFAULT_FOLDER_PATH+"부모없는 파일", "Shared");
	}
	/**
	 * 파일 중복시 로그를 위해 중복파일이 존재하는 폴더 조회시 connection reset이 자주 발생. 
	 * 해당 원인 파악이 어려워 수정
	 * Map key 구성
	 * name : 폴더명
	 * createDate : 생성일
	 * SrcId : 이력저장용 소스ID
	 * 확장속성 대상 폴더인 경우 
	 * elementAttr - sh:ctrNm / 폴더명
	 * @param list
	 * @return
	 */
	public String makeFoldPathStringFromList(List<Map<String,Object>> list) {
		StringBuffer fldName = new StringBuffer();
		fldName.append("/전사문서함/");
		for(Map<String,Object> folderInfo : list) {
			fldName.append(folderInfo.get("name"));
			fldName.append("/");
		}
		fldName.deleteCharAt(fldName.length()-1);
		return fldName.toString();
	}
}
