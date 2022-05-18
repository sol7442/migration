package com.inzent.sh;

import java.util.List;

import com.inzent.xedrm.api.Result;
import com.inzent.xedrm.api.XAPIException;
import com.inzent.xedrm.api.XeConnect;
import com.inzent.xedrm.api.XeDocument;
import com.inzent.xedrm.api.XeElement;
import com.inzent.xedrm.api.XeFolder;
import com.inzent.xedrm.api.domain.Document;
import com.inzent.xedrm.api.domain.Folder;

public class AbstractShMigHandler {
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
}
