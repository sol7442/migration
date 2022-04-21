package com.quantum.mig.manager;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class EnvironmentManager extends Properties {
	private static EnvironmentManager instance;
	
	public static EnvironmentManager getInstance() {
		if(instance == null ) {
			instance = new EnvironmentManager();
		}
		return instance;
	}

	public EnvironmentManager load (String file_name) throws IOException {
		instance = getInstance();
		FileInputStream fis = new FileInputStream(new File(file_name));
		super.load(fis);
		fis.close();
		return instance;
	}
	
	//environment.file 의 key , value 값 일어오는 함수 
	public String get(String key, String def_val) {
		String value = getProperty(key);
		if(value == null) {
			value = def_val;
		}
		return value;
	}

	public String getMigHome() {
		if(System.getProperty("mig.home") != null ){
			return System.getProperty("mig.home");
		}else {
			return System.getProperty("user.dir");
		}
	}
}
