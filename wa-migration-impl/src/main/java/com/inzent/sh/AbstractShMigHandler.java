package com.inzent.sh;

import com.inzent.xedrm.api.XeConnect;
import com.inzent.xedrm.api.XeFolder;
import com.inzent.xedrm.api.domain.Folder;

public class AbstractShMigHandler {
	protected Folder makeFolder(XeConnect con , String path ,String eid) {
		XeFolder xf = new XeFolder(con);
		//xf.getOrCreateFolderByPath(폴더경로, 부모폴더eid);
		return xf.getOrCreateFolderByPath(path, eid);
	}
}
