package com.inzent.daemon;

import org.apache.commons.daemon.DaemonContext;
import org.apache.commons.daemon.DaemonController;

public class LauncherContext implements DaemonContext{
	private String[] args;
	public LauncherContext(String[] args) {
		this.args = args;
	}
	@Override
	public DaemonController getController() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] getArguments() {
		// TODO Auto-generated method stub
		return this.args;
	}

}
