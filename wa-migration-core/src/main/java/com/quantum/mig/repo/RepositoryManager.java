package com.quantum.mig.repo;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.apache.ibatis.type.JdbcType;

import com.quantum.mig.MigrationException;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RepositoryManager {
	private static RepositoryManager instance;
	private Map<String,SqlSessionFactory> factoris = new HashMap<String, SqlSessionFactory>();
	
	
	private RepositoryManager() {}
	public static RepositoryManager getInstance() {
		if(instance == null) {
			instance = new RepositoryManager();
		}
		return instance;
	}
	public void connect(Map<String, Object> conf) throws MigrationException {
		try {
			Map<String,Object> repo = (Map<String, Object>) conf.get("repository");
			
			
			String url = (String)repo.get("tar.url");
			String user = (String)repo.get("tar.user");
			String passwd = (String)repo.get("tar.passwd");
			DataSource datasource = DataSourceBuilder.build(url, user, passwd);
			Environment environment = new Environment("target", new JdbcTransactionFactory(), datasource);
			
			Configuration mybatis_config = new Configuration();
			mybatis_config.setLogImpl(RepositoryLogger.class);
			mybatis_config.setJdbcTypeForNull(JdbcType.NULL);
			mybatis_config.setCallSettersOnNulls(true);
			mybatis_config.setEnvironment(environment);
			
			
			//factoris = new SqlSessionFactoryBuilder().build(mybatis_config);
		}catch (Exception e) {
			// TODO: handle exception
		}
		
	}
	private DataSource getDataSource(String url, String user, String passwd) throws MigrationException {
		HikariConfig config = new HikariConfig();
		try {
			config.setJdbcUrl( url);
			config.setUsername(user);
			config.setPassword(passwd);
            
//			hikari_config.setPoolName(			 config.getString("jdbc.pool.pool-name"));
//			hikari_config.setAutoCommit(		 config.getBoolean("jdbc.pool.auto-commit"));
//			hikari_config.setIdleTimeout(		 config.getInteger("jdbc.pool.idle-timeout"));
//			hikari_config.setMaxLifetime(		 config.getInteger("jdbc.pool.max-lifetime"));
//			hikari_config.setConnectionTimeout(  config.getInteger("jdbc.pool.conntion-timeout"));
//			hikari_config.setConnectionTestQuery(config.getString("jdbc.pool.conntion-test-query"));
//			hikari_config.setMinimumIdle(		 config.getInteger("jdbc.pool.min-idle"));
//			hikari_config.setMaximumPoolSize(	 config.getInteger("jdbc.pool.max-size"));
//			
//			hikari_config.addDataSourceProperty("dataSource.cachePrepStmts"			, config.getString("dataSource.cachePrepStmts"));
//			hikari_config.addDataSourceProperty("dataSource.prepStmtCacheSize"		, config.getString("dataSource.prepStmtCacheSize"));
//			hikari_config.addDataSourceProperty("dataSource.prepStmtCacheSqlLimit"	, config.getString("dataSource.prepStmtCacheSqlLimit"));
//			hikari_config.addDataSourceProperty("dataSource.useServerPrepStmts"		, config.getString("dataSource.useServerPrepStmts"));
//			
//			hikari_config.addDataSourceProperty("dataSource.useLocalSessionState"		, "true");
//			hikari_config.addDataSourceProperty("dataSource.rewriteBatchedStatements"	, "true");
//			hikari_config.addDataSourceProperty("dataSource.cacheResultSetMetadata"		, "true");
//			hikari_config.addDataSourceProperty("dataSource.cacheServerConfiguration"	, "true");
//			hikari_config.addDataSourceProperty("dataSource.elideSetAutoCommits"		, "true");
//		    hikari_config.addDataSourceProperty("dataSource.maintainTimeStats"			, "false");
		 
		}catch (Exception e) {
			throw new MigrationException(e.getMessage(),e);
		}finally {
			log.info("{}",config);
		}
		return new HikariDataSource(config);
	}
	public <T> T getRepository(String string, Class<T> classOfT) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
