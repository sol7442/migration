package com.quantum.mig.repo;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import com.quantum.mig.MigrationException;
import com.quantum.mig.repo.mybatis.MybatisSessionFactory;

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
			Map<String,Map<String,String>> repo = (Map<String, Map<String,String>>) conf.get("repository");
			log.info("- Loading Repository => {} " , repo );
			Set<String> keySet = repo.keySet();
			for(String key : keySet) {
				Map<String,String> dsInfo = (Map<String, String>) repo.get(key);
				Class.forName(dsInfo.get("driver"));
				SqlSessionFactory dataSource = MybatisSessionFactory.builder()
														.name(key)
														.url(dsInfo.get("url"))
														.user(dsInfo.get("user"))
														.passwd(dsInfo.get("passwd"))
														.path(dsInfo.get("path"))
														.validation(dsInfo.get("validation"))
														.build().build();
				this.factory.put(key, dataSource);
				sessionTest(key,dsInfo.get("validation"));
			}
		} catch (Exception e) {
			throw new MigrationException(e.getMessage(),e);
		}
		
	}
	
	public SqlSession openSession(String name) throws MigrationException {
		if(factory.get(name) == null) {
			throw new MigrationException ("Session Factory is null");
		}
		SqlSession session = factory.get(name).openSession();
		
		if(session == null) {
			throw new MigrationException("Sql-Session Open Failed");
		}
		return session;
	}
	
	public boolean sessionTest(String sourceName,String validation) throws MigrationException {
		boolean validation_result = false;
		try (SqlSession session =  RepositoryManager.getInstance().openSession(sourceName)){
			Connection connection = session.getConnection();
			validation_result = connection.prepareStatement(validation).execute();
		}catch (Exception e) {
			throw new MigrationException(e.getMessage(),e);
		}finally {
			log.info("{} => {} : {} " , sourceName, validation_result , validation);
		}
		return validation_result;
	}
}
