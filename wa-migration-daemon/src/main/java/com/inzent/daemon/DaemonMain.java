package com.inzent.daemon;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Date;
import java.util.Map;

import org.apache.commons.daemon.Daemon;
import org.apache.commons.daemon.DaemonContext;
import org.apache.commons.daemon.DaemonInitException;
import org.yaml.snakeyaml.Yaml;

import com.quantum.mig.MigrationException;
import com.quantum.mig.SmartMigration;
import com.quantum.mig.repo.RepositoryManager;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DaemonMain implements Daemon {
	static DaemonMain daemon = new DaemonMain();
	public static void main(String[] args ) {
		try {
			ShutdownHook hook = new ShutdownHook();
			hook.attach(daemon);
			start(args);
			
		}catch (Exception e) {
			log.error(e.getMessage(),e);
		}
	}


	private static Map<String, Object> load_conf() throws FileNotFoundException {
		StringBuilder str_builder = new StringBuilder();

		str_builder.append(System.getProperty("sys.path")).append(File.separator);
		str_builder.append(System.getProperty("conf.path")).append(File.separator);
		str_builder.append(System.getProperty("module.name")).append(File.separator);
		str_builder.append("conf.yml");
		log.info(" - CONFIG PATH : {}", str_builder.toString());
		
		Yaml yaml = new Yaml();
		Map<String, Object> conf = yaml.load(new FileReader(str_builder.toString()));
		log.info("{}",conf);
		return conf;
	}
	
	private static void print_start_header() {
		
		
		log.info("===========================================");
		log.info("APP_ID 		: {}",System.getProperty("app.id"));

		log.info("WA-daemon-RUNTIM START : {}, {}", daemon.getClass() , new Date() );
		
		log.info("LOG ({}) : {} : {} ",System.getProperty("log.mode"), System.getProperty("log.path"), System.getProperty("log.conf"));
		log.info("=============================================================================");
		
	}
	private static void print_start_footer() {
		log.info("===========================================");
	}
	@Override
	public void init(DaemonContext context) throws DaemonInitException, Exception {
		// TODO Auto-generated method stub
		
	}


	public static void start(String[] args) throws MigrationException {
		try {
			daemon.init(new LauncherContext(args));
			print_start_header();
			daemon.start();
			print_start_footer();
			stop(args);
		} catch (Exception e) {
			throw new MigrationException(e.getMessage(),e);
		} finally {
		}
		
	}
	@Override
	public void start() throws MigrationException {
		if(daemon != null) {
			Map<String, Object> conf;
			try {
				conf = load_conf();
				RepositoryManager.getInstance().connect(conf);
				new SmartMigration(conf).start();
			} catch (FileNotFoundException e) {
				throw new MigrationException(e.getMessage(),e);
			}
		} else {
			throw new MigrationException("SCIM AGNET IS NULL");
		}
		
	}
	
	public static void stop(String[] args) throws MigrationException {
		try {
			log.info("Module Stop .. {} "  , new Date());
			daemon.stop();
		}catch (Exception e) {
			throw new MigrationException(e.getMessage(),e);
		}finally {
			daemon.destroy();
		}
	}

	@Override
	public void stop() throws Exception {
		daemon.stop();
	}


	@Override
	public void destroy() {
		System.exit(0);
	}



}
