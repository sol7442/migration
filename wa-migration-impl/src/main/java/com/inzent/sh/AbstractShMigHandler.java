package com.inzent.sh;

import java.security.SecureRandom;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.json.simple.JSONObject;

import com.inzent.xedrm.api.Result;
import com.inzent.xedrm.api.XAPIException;
import com.inzent.xedrm.api.XeConnect;
import com.inzent.xedrm.api.XeDocument;
import com.inzent.xedrm.api.XeElement;
import com.inzent.xedrm.api.XeFolder;
import com.inzent.xedrm.api.domain.Document;
import com.inzent.xedrm.api.domain.Folder;

public class AbstractShMigHandler {
	private final String randomChar = "abcdefghijklmnopqrstuvwxyz0123456789";
	private SecureRandom random = new SecureRandom();
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
	protected Result modifyRights(XeConnect con , String eid) {
		XeElement xe = new XeElement(con);
		Result result = xe.getRights(eid);
		Map jsonResult = result.getJsonObject();
		Map security = (Map) jsonResult.get("security");
		Map permission = (Map) jsonResult.get("permission");
		
		List rightListSecuritySimple = (List) security.get("rightListSimple");
		List rightListPermissionSimple = (List) permission.get("rightListSimple");

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
}
