package test.conf.yml;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class testYmlWriter {
	@Test
	public void testWriter() {
		final DumperOptions options = new DumperOptions();
	    options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
	    options.setPrettyFlow(true);
	    
	    SimpleDateFormat sdf = new SimpleDateFormat("yyyy:MM:dd,hh:mm:ss");
	    
	    Map<String,Object> conf_map = new HashMap<String, Object>();
	    Map<String,Object> condition_map = new HashMap<String, Object>();
		Map<String,Object> repository_map = new HashMap<String, Object>();
		conf_map.put("class", "KMS"); //APPV//PLAN
		conf_map.put("op.count", 100);
		conf_map.put("condition", condition_map);
		conf_map.put("repository", repository_map);
		
		condition_map.put("type", "time");//json
		condition_map.put("stime", sdf.format(new Date()));
		condition_map.put("etime", sdf.format(new Date()));
		
		repository_map.put("src.url", "jdbc:mariadb://192.168.0.105:3306/scim");
		repository_map.put("src.user", "scim");
		repository_map.put("src.passwd", "1q2w3e4r!@");
		
		repository_map.put("tar.url", "jdbc:mariadb://192.168.0.105:3306/scim");
		repository_map.put("tar.user", "scim");
		repository_map.put("tar.passwd", "1q2w3e4r!@");
		
		
		try {
			new Yaml(options).dump(conf_map,new PrintWriter(new File("../conf/data.yaml")));
		} catch (FileNotFoundException e) {
			log.error(e.getMessage(),e);
		}
	}
}
