package com.quantum.mig.repo.mybatis;

import java.io.File;
import java.sql.Connection;
import java.util.Iterator;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.codehaus.janino.Java.ThisReference;
import org.slf4j.Logger;

import com.quantum.mig.MigrationException;
import com.quantum.mig.log.LOGGER;
import com.quantum.mig.repo.DataSourceBuilder;
import com.quantum.mig.repo.RepositoryManager;
import com.quantum.mig.service.MigrationResultService;

import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
@Builder
public class MybatisSessionFactory {
	private transient SqlSessionFactory factory;
	String name;
	String url;
	String user;
	String passwd;
	String path;
	String validation;
	
	static String xml_full_path = null;
	public SqlSessionFactory build() throws MigrationException {
		DataSource ds =  DataSourceBuilder.build(url,user,passwd);
		Configuration config = MybatisConfigBuilder.build(name, ds);
		getXmlpath(path);
		File mapper_path = new File(xml_full_path);
		MybatisXmlParser.parse(mapper_path, config);
		return new SqlSessionFactoryBuilder().build(config);		
	}

	public static String getXmlpath (String path) {
		if(System.getProperty("mig.home") != null) {
			xml_full_path = System.getProperty("mig.home") + path; 
			return xml_full_path;
		}else {
			xml_full_path = System.getProperty("user.dir") + path;
			return xml_full_path;
		}
	}
}
