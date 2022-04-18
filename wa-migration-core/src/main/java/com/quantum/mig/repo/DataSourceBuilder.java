package com.quantum.mig.repo;


import javax.sql.DataSource;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import lombok.Builder;

@Builder
public class DataSourceBuilder extends HikariDataSource {
	public static DataSource build(String url, String user, String passwd) {
		HikariConfig config = new HikariConfig();
		config.setJdbcUrl(url);
		config.setUsername(user);
		config.setPassword(passwd);
		return new HikariDataSource(config);
	}
}
