package api;

import org.junit.Test;

import com.inzent.xedrm.api.XeConnect;
import com.inzent.xedrm.api.XeFolder;
import com.inzent.xedrm.api.domain.Folder;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ApiCallTest {
	@Test
	public void test() {
		try {
			XeConnect con = new XeConnect("http://54.180.132.53:8300/xedrm", "SUPER","SUPER");
			System.out.println(con.login());
//			XeFolder xf = new XeFolder(con);
//			Folder result = xf.getOrCreateFolderByPath("/a/b/c/d/e/h", "2022042909000606");
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
