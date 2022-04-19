package com.quantum.mig.repo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.apache.ibatis.type.JdbcType;

import com.quantum.mig.MigrationException;
import com.quantum.mig.repo.mybatis.MybatisSessionFactory;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RepositoryManager {
	private static RepositoryManager instance;
	private Map<String,SqlSessionFactory> factory = new HashMap<String, SqlSessionFactory>();


	private RepositoryManager() {}
	public static RepositoryManager getInstance() {
		if(instance == null) {
			instance = new RepositoryManager();
		}
		return instance;
	}
	@SuppressWarnings("unchecked")
	public void connect(Map<String, Object> conf) throws MigrationException {
		try {
			Map<String,Object> repo = (Map<String, Object>) conf.get("repository");
	
			Class.forName((String)repo.get("src.drivder"));
			SqlSessionFactory source = MybatisSessionFactory.builder()
				.name("source")
				.url((String)repo.get("src.url"))
				.user((String)repo.get("src.user"))
				.passwd((String)repo.get("src.passwd"))
				.path((String)repo.get("src.path"))
				.build().build();
			
			Class.forName((String)repo.get("tar.drivder"));
			SqlSessionFactory target = MybatisSessionFactory.builder()
					.name("target")
					.url((String)repo.get("tar.url"))
					.user((String)repo.get("tar.user"))
					.path((String)repo.get("tar.path"))
					.passwd((String)repo.get("tar.passwd")).build().build();
			
			Class.forName((String)repo.get("res.drivder"));
			SqlSessionFactory result = MybatisSessionFactory.builder()
					.name("result")
					.url((String)repo.get("res.url"))
					.user((String)repo.get("res.user"))
					.path((String)repo.get("res.path"))
					.passwd((String)repo.get("res.passwd")).build().build();
			
			this.factory.put("source", source);
			this.factory.put("target", target);
			this.factory.put("result", result);

		}catch (Exception e) {
			throw new MigrationException(e.getMessage(),e);
		}
		
	}
	
	public <T> T getRepository(String string, Class<T> classOfT) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
