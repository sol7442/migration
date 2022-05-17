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

import com.quantum.mig.MigrationException;
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
	
	public SqlSessionFactory build() throws MigrationException {
		DataSource ds =  DataSourceBuilder.build(url,user,passwd);
		Configuration config = MybatisConfigBuilder.build(name, ds);
		File mapper_path = new File(path);
		MybatisXmlParser.parse(mapper_path, config);
		return new SqlSessionFactoryBuilder().build(config);		
	}
	
	public boolean sessionTest() throws MigrationException {
		boolean validation_result = false;

		try (SqlSession session = factory.openSession() ){
			Connection connection = session.getConnection();
			validation_result = connection.prepareStatement(validation).execute();
		}catch (Exception e) {
			throw new MigrationException(e.getMessage(),e);
		}finally {
			log.info("=======================JDBC : {} : {} " ,  validation_result , validation);
		}

		return validation_result;
	}
	

}
