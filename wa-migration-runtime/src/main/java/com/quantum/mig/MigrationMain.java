package com.quantum.mig;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;

import com.quantum.mig.config.MigConfiguration;
import com.quantum.mig.manager.EnvironmentManager;
import com.quantum.mig.repo.RepositoryManager;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MigrationMain implements MigConfiguration {
	public static void main(String[] args ) {
		try {
			Map<String, Object> conf = load();
			RepositoryManager.getInstance().connect(conf);
			new SmartMigration(conf).start();
		}catch (Exception e) {
			log.error(e.getMessage(),e);
		}finally {
			log.info("-------");	
		}
	}


	private static Map<String, Object> load() throws Exception {
		String sys_name = System.getProperty("system.properties");
		EnvironmentManager.getInstance().load(sys_name);
		
		
		StringBuilder str_builder = new StringBuilder();
		str_builder.append(System.getProperty("sys.path")).append(File.separator);
		str_builder.append(System.getProperty("conf.path")).append(File.separator);
		str_builder.append(System.getProperty("module.name")).append(File.separator);
		str_builder.append("conf.yml");
		Yaml yaml = new Yaml();
		Map<String, Object> conf = yaml.load(new FileReader(str_builder.toString()));
		log.info(" - Configuratin path : {}"  ,  str_builder.toString());
		print_start_header(conf);

		return conf;
	}
	
	private static void print_start_header(Map<String, Object> conf) {
		String log_mode = EnvironmentManager.getInstance().get(LOGGER_MODE, "deubg");
		String log_path = EnvironmentManager.getInstance().get(LOGGER_PATH, "./logs");
		String log_conf = EnvironmentManager.getInstance().get(LOGGER_CONF, "./config/logback.xml");
		String app_id = EnvironmentManager.getInstance().get(APP_ID, "wa-migration");
		
		log.info("===========================================");
		log.info(" - System Properties : {}"  ,  System.getProperty("system.properties"));
		log.info(" - Migration Home : {}"     ,  EnvironmentManager.getInstance().getMigHome());
		log.info(" - APP ID : {}"			  ,  app_id);
		log.info(" - Module Name : {}"		  ,  conf.get("class"));
		log.info(" - LOG : {}  {}  {} " 	  ,  log_mode , log_path , log_conf);
		log.info("===========================================");
		
	}
}
