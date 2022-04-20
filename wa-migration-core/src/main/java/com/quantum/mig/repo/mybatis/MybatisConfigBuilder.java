package com.quantum.mig.repo.mybatis;

import javax.sql.DataSource;

import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.apache.ibatis.type.JdbcType;

import com.quantum.mig.repo.RepositoryLogger;

public class MybatisConfigBuilder {
	public static Configuration build(String name, DataSource ds) {
		
		Environment environment = new Environment(name, new JdbcTransactionFactory(), ds);
		
		Configuration mybatis_config = new Configuration();
		mybatis_config.setLogImpl(RepositoryLogger.class);
		mybatis_config.setJdbcTypeForNull(JdbcType.NULL);
		mybatis_config.setCallSettersOnNulls(true);
		mybatis_config.setEnvironment(environment);
		
		return mybatis_config;
		
		
	}
}
