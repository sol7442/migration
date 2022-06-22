package com.inzent.sh.entity;

import java.util.ArrayList;
import java.util.Map;

import lombok.Data;
import lombok.EqualsAndHashCode;
@Data
@EqualsAndHashCode(callSuper=false)
public class FolderList extends ArrayList<Map<String,Object>> {
	private static final long serialVersionUID = -8681114158665096356L;
	private String rootPath;
	
	public FolderList(String rootPath) {
		this.rootPath = rootPath;
	}
	
	public String getFullPath() {
		StringBuffer fullPath = new StringBuffer();
		for(Map<String,Object> folder : this) {
			fullPath.append(String.valueOf(folder.get("name")));
			fullPath.append("/");
		}
		return this.rootPath+"/"+fullPath.toString();
	}
}
