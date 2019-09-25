package com.baoxue.spartacus.security.browser.support;

import java.security.interfaces.RSAPrivateKey;
import java.util.regex.Pattern;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * 自定义PasswordEncoder，解密页面传过来的加密密码
 * 
 * @author lvchao
 * @email chao9038@hnu.edu.cn
 * @createtime 2019年5月20日 下午7:08:08
 */
public class MyBCryptPasswordEncoder extends BCryptPasswordEncoder {
	
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	private Pattern BCRYPT_PATTERN = Pattern
			.compile("\\A\\$2a?\\$\\d\\d\\$[./0-9A-Za-z]{53}");
	
	@Autowired
	private HttpSession session;
	

	@Override
	public boolean matches(CharSequence rawPassword, String encodedPassword) {
		// 根据模和私钥指数获取私钥，解密
		String modulus = (String) session.getAttribute("modulus");
		String private_exponent = (String)session.getAttribute("private_exponent");
		RSAPrivateKey privateKey = RSAUtils.getPrivateKey(modulus, private_exponent);
		String inputPassword = rawPassword.toString();
		
		try {
			inputPassword = RSAUtils.decrypttoStr(privateKey, inputPassword);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if (encodedPassword == null || encodedPassword.length() == 0) {
			logger.warn("Empty encoded password");
			return false;
		}

		if (!BCRYPT_PATTERN.matcher(encodedPassword).matches()) {
			logger.warn("Encoded password does not look like BCrypt");
			return false;
		}
		return BCrypt.checkpw(inputPassword, encodedPassword);
	}
	
}
