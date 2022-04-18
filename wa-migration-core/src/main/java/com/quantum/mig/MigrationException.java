package com.quantum.mig;

public class MigrationException extends Exception {
	private static final long serialVersionUID = -4294313782431585668L;
	public MigrationException(String msg) {
		super(msg);
	}
	public MigrationException(String msg, Exception e) {
		super(msg,e);
	}
}
