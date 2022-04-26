package com.inzent.daemon;
import org.apache.commons.daemon.Daemon;


public class ShutdownHook extends Thread{
	Daemon daemon = null;
	public void attach(Daemon daemon) {
		this.daemon = daemon;
		Runtime.getRuntime().addShutdownHook(this);
	}
	
	public void run() {
		try {
			daemon.stop();
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			daemon.destroy();
		}
	}
}
