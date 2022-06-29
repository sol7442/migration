package uuid;

import java.security.SecureRandom;
import java.util.UUID;

import org.junit.Test;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UuidTest {
	private final String randomChar = "abcdefghijklmnopqrstuvwxyz0123456789";
	private SecureRandom random = new SecureRandom();
	@Test
	public void test() {
		try {
			for(int i = 0 ; i < 100 ; i++) {
				System.out.println(randomChar.charAt(random.nextInt(randomChar.length())));
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public String makeUuid() {
		String uuid = UUID.randomUUID().toString();
		StringBuffer sb = new StringBuffer();
		sb.append(uuid.replaceAll("-",""));
		for(int i = 0 ; i < 8 ; i++) {
			sb.append(randomChar.charAt(random.nextInt(randomChar.length())));
		}
		return sb.toString();
	}
}
