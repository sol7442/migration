package com.inzent.sh;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.json.simple.JSONObject;

import com.inzent.sh.entity.FileMakeResult;
import com.inzent.sh.entity.FolderList;
import com.inzent.sh.entity.ShFile;
import com.inzent.sh.exception.DuplicatedFileException;
import com.inzent.sh.util.YamlUtil;
import com.inzent.xedrm.api.Result;
import com.inzent.xedrm.api.XAPIException;
import com.inzent.xedrm.api.XeConnect;
import com.inzent.xedrm.api.XeDocument;
import com.inzent.xedrm.api.XeElement;
import com.inzent.xedrm.api.XeFolder;
import com.inzent.xedrm.api.domain.Document;
import com.inzent.xedrm.api.domain.Folder;
import com.quantum.mig.MigrationException;
import com.quantum.mig.PrintStepHandler;
import com.quantum.mig.entity.MigrationAudit;
import com.quantum.mig.service.MigrationAuditService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ShMigHandler {
	
	protected Map<String,Object> conf;
	protected PrintStepHandler steper = null;
	private final String randomChar = "abcdefghijklmnopqrstuvwxyz0123456789";
	private SecureRandom random = new SecureRandom();
		
	protected MigrationAuditService auditService = new MigrationAuditService();
	protected SimpleDateFormat format = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
	
	protected int stepCount = 0; 
	protected int outCount = 0;
	protected int deletedCount = 0;
	protected PrintStepHandler loadStepPrinter(Map<String, Object> conf) {
		outCount = (int)conf.get("out.count");
		return new ConsoleStepPrinter(outCount);
	}
	
	protected Folder makeFolder(XeConnect con , String path ,String eid) {
		XeFolder xf = new XeFolder(con);
		//xf.getOrCreateFolderByPath(폴더경로, 부모폴더eid);
		return xf.getOrCreateFolderByPath(path, eid);
	}
	/**
	 * 폴더 계층쿼리 조회 X srcId 알 수 없어 이력을 남길 수가 없음.
	 * @deprecated
	 * @param con
	 * @param folderList
	 * @param eid
	 * @return
	 * @throws MigrationException 
	 */
	protected String makeFolder(XeConnect con , List<Map<String,Object>> folderList ,String eid) throws MigrationException {
		Result result = null;
		XeFolder xf = new XeFolder(con);
		XeElement xe = new XeElement(con);
		String parentFolderId = eid;
		for(Map<String,Object> folderInfo : folderList) {
			result = xf.createFolder(parentFolderId, (String)folderInfo.get("name"));
			if(result.isSuccess()) {
				parentFolderId = (String)result.getData(0).get("rid");
				modifyRights(con, parentFolderId);
				@SuppressWarnings("unchecked")
				Map<String,String> elementAttr = (Map<String, String>)folderInfo.get("elementAttr");
				if(elementAttr != null) {
					Result attrResult = xe.updateAttrEx(parentFolderId, elementAttr);
					if(!attrResult.isSuccess()) {
						// 확장속성 저장 실패로 인한 폴더 생성 실패
						throw new XAPIException(attrResult.getReturnCode(),attrResult.getErrorMessage());
					}
					continue;
				}
				recordAudit(String.valueOf(folderInfo.get("SrcId")), parentFolderId, "CREATE", "0", "Success");
			} 
			if("ECM0001".equals(result.getReturnCode())) {
				parentFolderId = (String) result.getJsonObject().get("rid");
				continue;
			} 
		}
		return parentFolderId;
	}
	/**
	 * 폴더의 정보 중 폴더명 밖에 활용 못하여 이력 로그에 사용할 srcId를 확보 X
	 * @param con
	 * @param folderList
	 * @param eid
	 * @return
	 * @throws MigrationException 
	 */
	protected String makeFolderWithoutAudit(XeConnect con , List<Map<String,Object>> folderList ,String eid) throws MigrationException {
		Result result = null;
		XeFolder xf = new XeFolder(con);
		XeElement xe = new XeElement(con);
		String parentFolderId = eid;
		for(Map<String,Object> folderInfo : folderList) {
			result = xf.createFolder(parentFolderId, (String)folderInfo.get("name"));
			if(result.isSuccess()) {
				parentFolderId = (String)result.getData(0).get("rid");
				modifyRights(con, parentFolderId);
				@SuppressWarnings("unchecked")
				Map<String,String> elementAttr = (Map<String, String>)folderInfo.get("elementAttr");
				if(elementAttr != null) {
					Result attrResult = xe.updateAttrEx(parentFolderId, elementAttr);
					if(!attrResult.isSuccess()) {
						// 확장속성 저장 실패로 인한 폴더 생성 실패
						throw new XAPIException(attrResult.getReturnCode(),attrResult.getErrorMessage());
					}
					continue;
				}
			} 
			if("ECM0001".equals(result.getReturnCode())) {
				parentFolderId = (String) result.getJsonObject().get("rid");
				continue;
			} 
		}
		return parentFolderId;
	}
	/**
	 * 
	 * @param con
	 * @param folderList
	 * @param eid
	 * @param fullPath : String으로 된 최총 폴더를 먼저 조회.
	 * @return
	 * @throws MigrationException
	 */
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
						@SuppressWarnings("unchecked")
						Map<String,String> elementAttr = (Map<String, String>)folderInfo.get("elementAttr");
						if(elementAttr != null) {
							Result attrResult = xe.updateAttrEx(parentFolderId, elementAttr);
							if(!attrResult.isSuccess()) {
								// 확장속성 저장 실패로 인한 폴더 생성 실패
								throw new XAPIException(attrResult.getReturnCode(),attrResult.getErrorMessage());
							}
							continue;
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
	 * 파일 목록 찾기
	 * @param con xe 커넥션
	 * @param folderEid	폴더 ID
	 * @param fileName	파일 이름
	 * @return
	 * @throws XAPIException
	 */
	protected List<Document> searchFileWithApi(XeConnect con , String folderEid , String fileName) throws XAPIException {
		XeElement xe = new XeElement(con);
		return xe.searchDoc(folderEid, fileName, null, null);
	}
	
	protected Result deleteDoc(XeConnect con , String eid) {
		XeDocument xd = new XeDocument(con);
		Result result = xd.deleteDocument(eid); 
		deletedCount++;
		log.debug("delete target:{}/count : {}/result : {}", eid ,deletedCount,result);
		//System.out.println(result);
		return result;
	}
	/**
	 * 권한 수정(폴더)
	 * @param con
	 * @param eid
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected Map<String,Object> getRights(XeElement xe , String eid) {
		Result result = xe.getRights(eid);
		return result.getJsonObject();
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected Result modifyRights(XeConnect con , String eid) {
		XeElement xe = new XeElement(con);
		Map<String,Object> jsonResult = this.getRights(xe, eid);
		Map<String,Object> security = this.getSecurityFromRight(jsonResult);
		Map<String,Object> permission = this.getPermissionFromRight(jsonResult);
		
		List rightListSecuritySimple = this.getRightListFromMap(security);
		List rightListPermissionSimple = this.getRightListFromMap(permission);
		this.setRightListPermssion(rightListPermissionSimple);
		return this.modifyRight(xe, eid, rightListSecuritySimple, rightListPermissionSimple);
	}
	
	@SuppressWarnings("unchecked")
	protected Map<String,Object> getSecurityFromRight(Map<String,Object> result){
		return (Map<String, Object>) result.get("security");
	}
	
	@SuppressWarnings("unchecked")
	protected Map<String,Object> getPermissionFromRight(Map<String,Object> result) {
		return (Map<String, Object>) result.get("permission");
	}
	
	@SuppressWarnings("rawtypes")
	protected List getRightListFromMap(Map<String,Object> map) {
		return (List)map.get("rightListSimple");
	}
	
	@SuppressWarnings("unchecked")
	protected void setRightListPermssion(List<JSONObject> rightListPermissionSimple) {
		for (JSONObject item : (List<JSONObject>) rightListPermissionSimple) {
			String privType = String.valueOf(item.get("privType"));
			switch(privType) {
			case "9" :
				item.put("templateId", "ELEMENT_OWNER");
				break;
			case "10" :
				item.put("templateId", "ELEMENT_GROUP_NONE");
				break;
			case "11" :
				item.put("templateId", "ELEMENT_ADMIN");
				break;
			case "12" :
				item.put("templateId", "ELEMENT_OTHERS_READ");
				break;
			default :
				break;
			}
		}
	}
	
	
	protected Result modifyRight(XeElement xe , String eid , List<Map<String,Object>> rightListSecuritySimple , List<Map<String,Object>> rightListPermissionSimple ) {
		Result modifyRightsResult = xe.saveRightsSimple(eid, "ACL", rightListSecuritySimple, rightListPermissionSimple);
		return modifyRightsResult;
	}
	
	
	protected String makeUuid() {
		String uuid = UUID.randomUUID().toString();
		StringBuffer sb = new StringBuffer();
		sb.append(uuid.replaceAll("-",""));
		for(int i = 0 ; i < 8 ; i++) {
			sb.append(randomChar.charAt(random.nextInt(randomChar.length())));
		}
		return sb.toString();
	}
	
	protected Map<String,Object> makeTimeParameter(Map<String,Object> condition) {
		Integer page = condition.get("page")==null?null:Integer.valueOf(condition.get("page").toString());
		Integer count = condition.get("count")==null?null:Integer.valueOf(condition.get("count").toString());
		String stime = (String)condition.get("stime");
		String etime = (String)condition.get("etime");
		
		Map<String,Object> queryParam = new HashMap<String,Object>();
		queryParam.put("page", page);
		queryParam.put("count", count);
		queryParam.put("stime", YamlUtil.convertTimeFormat(stime));
		queryParam.put("etime", YamlUtil.convertTimeFormat(etime));
		return queryParam;
	}
	
	@SuppressWarnings("unchecked")
	protected XeConnect getConnection(Map<String,Object> conf) {
		Map<String,Object> connectionInfo = (Map<String, Object>) conf.get("xe");
		XeConnect con = new XeConnect(connectionInfo.get("connect.url").toString()
				,connectionInfo.get("connect.id").toString()
				,connectionInfo.get("connect.pw").toString());
		return con;
	}
	
	protected void recordAudit(String srcId , String tagId , String action , String resultCd , String msg,int step,int total) throws MigrationException {
		MigrationAudit audit = this.makeDefaultAudit(srcId, tagId, action, resultCd, msg);
		log.debug(" - TASK AUDIT  =>   : {} " , audit.toString());
		if((stepCount%step) == 0) {
			steper.print(audit , total , stepCount);
		}
		stepCount++;
		auditService.record(audit);
	}
	

	protected void recordAudit(String srcId , String tagId , String action , String resultCd , String msg) throws MigrationException {
		MigrationAudit audit = this.makeDefaultAudit(srcId, tagId, action, resultCd, msg);
		log.debug(" - TASK AUDIT  =>   : {} " , audit.toString());
		auditService.record(audit);
	}
	
	protected MigrationAudit makeDefaultAudit(String srcId , String tagId , String action , String resultCd , String msg) {
		MigrationAudit audit = new MigrationAudit(srcId);
		audit.setTAG_ID(tagId);
		audit.setACTION(action);
		audit.setRESULT(resultCd);
		audit.setMSG(msg);
		audit.setSEQ_VALUE(this.makeUuid());
		LocalDateTime now = LocalDateTime.now();
		audit.setTIME(now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
		return audit;
	}
	/**
	 * 파일 중복 에러는 특수 처리
	 * 파일 검색 후 중복파일 삭제하고 처리
	 * connection reset 발생으로 인해 파일 삭제 api 호출시 새로운 커넥션 생성
	 * 성능의 이슈로 connection reset이 발생할 때만 connection 재생성.
	 * 파일 중복은 그냥 폴더의 full path + 파일명으로 처리 하는 것으로 재협의 
	 * full path API 호출이 아닌 folder 생성시 사용한 LIst를 조합하여 만든 문자열 사용 - 2022-06-17
	 * 
	 * @param con
	 * @param file
	 * @param folder
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	protected FileMakeResult makeFile(XeConnect con ,ShFile file ,Folder folder,boolean isReconnect) throws Exception {
		XeDocument xd = new XeDocument(con);
		FileMakeResult fileMakeResult = null;
		Result result = null;
		File f = new File(file.getPath());
		try(InputStream is = new FileInputStream(f)){
			// xd.createDocument(업로드 대상 폴더eid, 파일, 업로드파일명, 생성자, 소유자, 생성일, 수정일,
			// 덮어쓰기여부(false), 파일명변경여부(false));
			result = xd.createDocument(folder.getEid(), is, file.getFileName() 
									 , file.getRegister()
									 , file.getOwner()
									 , file.getRegistDate()
									 , file.getModifyDate()
									 , false, false);
			if("ECM0001".equals(result.getReturnCode())) {
				//API 호출 connection reset 문제 발생으로 사용 X
				//Document fd = xd.getDocument(folder.getEid());
				DuplicatedFileException e = new DuplicatedFileException(result.getReturnCode()
																	   ,folder.getPath() +File.separator+file.getFileName()
																	   ,"File Already Exists.");
				throw e;
			} else {
				fileMakeResult = new FileMakeResult(result.getJsonObject());
			}
		} catch(XAPIException e) {
			throw e;
		} catch(FileNotFoundException e) {
			throw e;
		} catch(SocketException e) {
			throw e;
		} catch(IOException e) {
			throw e;
		} catch(Exception e) {
			throw e;
		}
		return fileMakeResult;
	}
	
	/**
	 * 파일 중복 에러는 특수 처리
	 * 파일 검색 후 중복파일 삭제하고 처리
	 * connection reset 발생으로 인해 파일 삭제 api 호출시 새로운 커넥션 생성
	 * 성능의 이슈로 connection reset이 발생할 때만 connection 재생성.
	 * 파일 중복은 그냥 폴더의 full path + 파일명으로 처리 하는 것으로 재협의
	 * @param con
	 * @param file
	 * @param folder
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	protected FileMakeResult makeFile(XeConnect con ,ShFile file ,String folderId,boolean isReconnect) throws Exception {
		XeDocument xd = new XeDocument(con);
		FileMakeResult fileMakeResult = null;
		Result result = null;
		File f = new File(file.getPath());
		try(InputStream is = new FileInputStream(f)){
			// xd.createDocument(업로드 대상 폴더eid, 파일, 업로드파일명, 생성자, 소유자, 생성일, 수정일,
			// 덮어쓰기여부(false), 파일명변경여부(false));
			result = xd.createDocument(folderId, is, file.getFileName() 
									 , file.getRegister()
									 , file.getOwner()
									 , file.getRegistDate()
									 , file.getModifyDate()
									 , false, false);
			if("ECM0001".equals(result.getReturnCode())) {
				Document fd = xd.getDocument(folderId);
				DuplicatedFileException e = new DuplicatedFileException(result.getReturnCode()
																	   ,fd.getFullPath() +File.separator+file.getFileName()
																	   ,"File Already Exists.");
				throw e;
			} else {
				fileMakeResult = new FileMakeResult(result.getJsonObject());
			}
		} catch(XAPIException e) {
			throw e;
		} catch(FileNotFoundException e) {
			throw e;
		} catch(SocketException e) {
			throw e;
		} catch(IOException e) {
			throw e;
		} catch(Exception e) {
			throw e;
		}
		return fileMakeResult;
	}
}
