package com.baoxue.spartacus.security.core.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

/**
 * 读取application.properties里面所有imooc.security开头的配置项
 * 
 * @author lvchao
 * @email chao9038@hnu.edu.cn
 * @createtime 2018年8月6日 下午4:01:32
 */
@Data
@ConfigurationProperties(prefix = "imooc.security")
public class SecurityProperties {
	
	private BrowserProperties browser = new BrowserProperties();
	
	private ValidateCodeProperties code = new ValidateCodeProperties();
	
	private SocialProperties social = new SocialProperties();
	
	private OAuth2Properties oauth2 = new OAuth2Properties();

}

