package com.inzent.sh;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.SecureRandom;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.json.simple.JSONObject;

import com.inzent.sh.entity.FileMakeResult;
import com.inzent.sh.entity.ShFile;
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
	protected SimpleDateFormat format = new SimpleDateFormat("YYYY-MM-dd hh:mm:ss");
	
	protected int step_count = 0; 
	protected int out_count = 0;
	
	protected PrintStepHandler loadStepPrinter(Map<String, Object> conf) {
		out_count = (int)conf.get("out.count");
		return new ConsoleStepPrinter(out_count);
	}
	
	protected Folder makeFolder(XeConnect con , String path ,String eid) {
		XeFolder xf = new XeFolder(con);
		//xf.getOrCreateFolderByPath(폴더경로, 부모폴더eid);
		return xf.getOrCreateFolderByPath(path, eid);
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
		return xd.deleteDocument(eid);
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
		Integer page = condition.get("page")==null?null:Integer.valueOf((String)condition.get("page"));
		Integer count = condition.get("count")==null?null:Integer.valueOf((String)condition.get("count"));
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
		if((step_count%step) == 0) {
			steper.print(audit , total , step_count);
		}
		step_count++;
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
	 * 해당 경로에 파일이 이미 존재하는 경우 삭제하고 재등록
	 * @param con
	 * @param file
	 * @param folder
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	protected FileMakeResult makeFile(XeConnect con ,ShFile file ,Folder folder) throws Exception {
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
				//파일 중복 에러는 특수 처리
				//파일 검색 후 중복파일 삭제하고 처리
				List<Document> fileList = this.searchFileWithApi(con, folder.getEid(), file.getFileName());
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
			throw e;
		} catch(FileNotFoundException e) {
			throw e;
		} catch(IOException e) {
			throw e;
		} catch(Exception e) {
			throw e;
		}
		return fileMakeResult;
	}
	
}
