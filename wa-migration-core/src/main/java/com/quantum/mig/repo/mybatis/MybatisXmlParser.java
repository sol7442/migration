package com.quantum.mig.repo.mybatis;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.session.Configuration;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MybatisXmlParser {
	public static void parse(File path, Configuration conf) {
		if(path.isDirectory()) {
			for (File map_file : path.listFiles()) {
				if(map_file.getName().endsWith(".xml")) {
					try {
						log.info(" - {}", map_file);
						XMLMapperBuilder xmlMapperBuilder = new XMLMapperBuilder(new FileInputStream(map_file),conf,map_file.getName(),conf.getSqlFragments());
						xmlMapperBuilder.parse();
					} catch (FileNotFoundException e) {
						log.error(e.getMessage(),e);
					}
				}
			}
		}
	}
}
