package com.quantum.mig.repo.mybatis;

import java.io.File;

import javax.sql.DataSource;

import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import com.quantum.mig.repo.DataSourceBuilder;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MybatisSessionFactory {
	String name;
	String url;
	String user;
	String passwd;
	String path;
	public SqlSessionFactory build() {
		DataSource ds =  DataSourceBuilder.build(url,user,passwd);
		Configuration config = MybatisConfigBuilder.build(name, ds);
		File mapper_path = new File(path);
		MybatisXmlParser.parse(mapper_path, config);
		
		return new SqlSessionFactoryBuilder().build(config);		
	}
}
