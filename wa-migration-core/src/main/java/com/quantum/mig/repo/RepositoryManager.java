package com.quantum.mig.repo;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

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
			Map<String,Object> repo = (Map<String, Object>) conf.get("repository");
			log.info("- Loading Repository => {} " , repo );
			//소스 테이블
			Class.forName((String)repo.get("src.drivder"));
			SqlSessionFactory source = MybatisSessionFactory.builder()
				.name("source")
				.url((String)repo.get("src.url"))
				.user((String)repo.get("src.user"))
				.passwd((String)repo.get("src.passwd"))
				.path((String)repo.get("src.path"))
				.validation((String)repo.get("src.validation"))
				.build().build();
			
			//이력 테이블
			Class.forName((String)repo.get("audit.drivder"));
			SqlSessionFactory audit = MybatisSessionFactory.builder()
					.name("audit")
					.url((String)repo.get("audit.url"))
					.user((String)repo.get("audit.user"))
					.path((String)repo.get("audit.path"))
					.passwd((String)repo.get("audit.passwd"))
					.validation((String)repo.get("audit.validation"))
					.build().build();
			

			// 결과 테이블
			Class.forName((String)repo.get("res.drivder"));
			SqlSessionFactory result = MybatisSessionFactory.builder()
					.name("result")
					.url((String)repo.get("res.url"))
					.user((String)repo.get("res.user"))
					.path((String)repo.get("res.path"))
					.passwd((String)repo.get("res.passwd"))
					.validation((String)repo.get("res.validation"))
					.build().build();
			
			
			/*
			 * Class.forName((String)repo.get("tar.drivder")); SqlSessionFactory
			 * target = MybatisSessionFactory.builder() .name("target")
			 * .url((String)repo.get("tar.url")) .user((String)repo.get("tar.user"))
			 * .path((String)repo.get("tar.path"))
			 * .passwd((String)repo.get("tar.passwd")).build().build();
			 * 
			 */

			this.factory.put("source", source);
			this.factory.put("audit", audit);
			this.factory.put("result", result);
			
			sessionTest((String)repo.get("src.validation"), factory );
		}catch (Exception e) {
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
	
	public boolean sessionTest(String validation , Map<String,SqlSessionFactory> factory) throws MigrationException {
		boolean validation_result = false;
		Iterator<String> keys = factory.keySet().iterator();
		while (keys.hasNext() ) {
			String source_name = keys.next();
			try (SqlSession session =  RepositoryManager.getInstance().openSession(source_name)){
				Connection connection = session.getConnection();
				validation_result = connection.prepareStatement(validation).execute();
			}catch (Exception e) {
				throw new MigrationException(e.getMessage(),e);
			}finally {
				log.info("{} => {} : {} " , source_name, validation_result , validation);
			}
		}
		return validation_result;
	}
}
