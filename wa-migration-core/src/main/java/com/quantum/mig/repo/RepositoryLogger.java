package com.quantum.mig.repo;

import org.apache.ibatis.logging.Log;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class RepositoryLogger implements Log {
	public RepositoryLogger(String name) {
		log.debug("{}",name);
	}
	
	@Override
	public boolean isDebugEnabled() {
		return log.isDebugEnabled();
	}

	@Override
	public boolean isTraceEnabled() {
		return log.isTraceEnabled();
	}

	@Override
	public void error(String s, Throwable e) {
		log.error(s,e);
	}

	@Override
	public void error(String s) {
		log.error(s);
	}

	@Override
	public void debug(String s) {
		log.debug(s);
	}

	@Override
	public void trace(String s) {
		log.trace(s);
	}

	@Override
	public void warn(String s) {
		log.warn(s);
	}
}
