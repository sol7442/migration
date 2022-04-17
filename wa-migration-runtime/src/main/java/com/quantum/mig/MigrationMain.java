package com.quantum.mig;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;

import com.quantum.mig.repo.RepositoryManager;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MigrationMain {
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


	private static Map<String, Object> load() throws FileNotFoundException {
		StringBuilder str_builder = new StringBuilder();
		str_builder.append(System.getProperty("conf.path")).append(File.separator);
		str_builder.append(System.getProperty("sys.path")).append(File.separator);
		str_builder.append("conf.yml");
		log.info(" - configuratin path : {}", str_builder.toString());
		
		Yaml yaml = new Yaml();
		Map<String, Object> conf = yaml.load(new FileReader(str_builder.toString()));
		log.info("{}",conf);
		return conf;
	}
}
