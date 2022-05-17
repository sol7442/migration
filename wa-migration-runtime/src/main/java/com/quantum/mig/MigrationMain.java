package com.quantum.mig;


import java.io.File;
import java.io.FileReader;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;

import com.quantum.mig.manager.EnvironmentManager;
import com.quantum.mig.repo.RepositoryManager;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MigrationMain {
	public static void main(String[] args) {
		try {
			Map<String, Object> conf = load(args[0]);
			log.info(" - Main Conf => {} " , conf);
			//DB 커넥션
			RepositoryManager.getInstance().connect(conf);
			new SmartMigration(conf).start();
		}catch (Exception e) {
			log.error(e.getMessage(),e);
		}finally {
			log.info(" - Main Stop ----------------------------");	
		}
	}


	private static Map<String, Object> load(String moduleName) throws Exception {
		StringBuilder str_builder = new StringBuilder();
		str_builder.append(System.getProperty("conf.path")).append(File.separator);  //../conf/module
		str_builder.append(moduleName).append(File.separator); //kms, appv, print	//kms				//../conf/module/kms/conf.yml
		str_builder.append("conf.yml");
		System.setProperty("module.name" , moduleName);
		Yaml yaml = new Yaml();
		Map<String, Object> conf = yaml.load(new FileReader(str_builder.toString()));
		print_start_header(conf);

		return conf;
	}
	
	private static void print_start_header(Map<String, Object> conf) throws Exception {
		String sys_name = System.getProperty("system.properties");
		EnvironmentManager.getInstance().load(sys_name);
		//system.properties 에서 가져오는 걸로 수정
		String log_mode = (String) EnvironmentManager.getInstance().get("log.mode");
//		String log_path = (String) EnvironmentManager.getInstance().get("log.path");
//		String log_conf = (String) EnvironmentManager.getInstance().get("log.conf");
		
		log.info("======================== SYSTEM PROPERTIES =============================");
		log.info(" - system properties => {}"  	,	sys_name);
		log.info(" - module name       => {}"	,	conf.get("class"));
		log.info(" - log mode          => {}" 	,	log_mode);
		log.info("=========================================================================");
	}
}
