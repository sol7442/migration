package com.inzent.daemon;

import org.apache.commons.daemon.Daemon;
import org.apache.commons.daemon.DaemonContext;
import org.apache.commons.daemon.DaemonInitException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.quantum.mig.MigrationMain;


public class DaemonMain implements Daemon {
	static DaemonMain daemon = null;
	Logger log = LoggerFactory.getLogger(DaemonMain.class);
	public static void main(String[] args) {
		try {
			start(args);
			new ShutdownHook().attach(daemon);
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static void start(String[] args) {
		try {
			daemon = new DaemonMain();
			daemon.init(new LauncherContext(args));
			daemon.start();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static void stop(String args[]) {
		try {
			daemon.stop();
			daemon.destroy();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void init(DaemonContext context) throws DaemonInitException, Exception {
		
		// config ���� �ε� 
		// log ��� ���� 
		// init_jetty �� ����

		
	}
	@Override
	public void start() throws Exception {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void stop() throws Exception {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}

}
