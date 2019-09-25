package com.baoxue.spartacus.security.core.properties;

import lombok.Data;

/**
 * @author zhailiang
 *
 */
@Data
public class OAuth2ClientProperties {
	
	private String clientId;
	
	private String clientSecret;
	
	private int accessTokenValidateSeconds = 7200; //默认失效时间，单位是秒（设成0的话，永久有效）
	
}
